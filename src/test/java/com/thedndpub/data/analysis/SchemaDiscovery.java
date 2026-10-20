package com.thedndpub.data.analysis;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Standalone, streaming 5eTools JSON structure analysis.
 */
public final class SchemaDiscovery {
    private static final JsonFactory JSON = new JsonFactory();
    private static final int EXAMPLES_PER_TYPE = 3;
    private static final int MAX_EXAMPLE_CHARS = Integer.getInteger("schema.example.maxChars", 300);
    private static final int MAX_EXAMPLE_DEPTH = Integer.getInteger("schema.example.maxDepth", 6);
    private static final int MAX_EXAMPLE_FIELDS = Integer.getInteger("schema.example.maxFields", 30);
    private static final int MAX_DISTINCT_VALUES = Integer.getInteger("schema.values.maxDistinct", 20);
    private static final Pattern NUMERIC_KEY = Pattern.compile("\\d+(?:e)?");
    private static final Set<String> METADATA_TOKENS = configuredTokens("schema.classification.metadata",
            "_meta,source,sources,page,_copy,_versions,foundry,token,hasfluff,basicrules,srd,srd52");
    private static final Set<String> CORE_TOKENS = configuredTokens("schema.classification.gameplayCore",
            "ac,hp,speed,attack,attacks,damage,save,saves,condition,conditioninflict,conditionimmune,spell,spells,spellcasting,recharge,uses,limiteduses,resist,resistances,immune,immunities,vulnerable,dc,bonus,ability,abilitycheck,savingthrow,cr,hit,hitpoints,armorclass,damageinflict");
    private static final Set<String> DESCRIPTION_TOKENS = configuredTokens("schema.classification.gameplayDescription",
            "entries,entrieshigherlevel,entry,action,actions,trait,traits,reaction,reactions,legendary,description,text,fluff");
    private static final Set<String> PRESENTATION_TOKENS = configuredTokens("schema.classification.presentation",
            "name,type,size,alignment,image,language,languages,skill,skills,senses,race,rarity");

    private SchemaDiscovery() {
    }

    public static void main(String[] args) throws IOException {
        Path input = Path.of(args.length > 0 ? args[0] : "src/main/resources/data");
        Path output = Path.of(args.length > 1 ? args[1] : "analysis");
        Result result = scan(input, output);
        long paths = result.domains.values().stream().mapToLong(Domain::pathCount).sum();
        long entities = result.domains.values().stream().mapToLong(Domain::entityCount).sum();
        System.out.printf("Scanned %d files, %d entities, %d domains, %d paths; %d parsing failures.%n",
                result.scannedFiles, entities, result.domains.size(), paths, result.failures.size());
        if (!result.failures.isEmpty()) {
            System.exit(1);
        }
    }

    static Result scan(Path input, Path output) throws IOException {
        TreeMap<String, Domain> domains = new TreeMap<>();
        List<Map<String, Object>> failures = new ArrayList<>();
        Path root = input.toAbsolutePath().normalize();
        List<Path> files;
        try (var walk = Files.walk(root)) {
            files = walk.filter(Files::isRegularFile).filter(p -> p.toString().toLowerCase().endsWith(".json"))
                    .sorted().toList();
        }

        for (Path file : files) {
            Path relPath = root.relativize(file.toAbsolutePath().normalize());
            String relative = relPath.toString().replace('\\', '/');
            String domainName = relPath.getParent() == null ? root.getFileName().toString()
                    : relPath.getParent().toString().replace('\\', '/');
            Domain domain = domains.computeIfAbsent(domainName, Domain::new);
            domain.files.add(relative);
            ScanContext.currentPath = "$";
            try (JsonParser parser = JSON.createParser(file.toFile())) {
                if (parser.nextToken() != JsonToken.START_OBJECT) {
                    throw new IOException("Expected top-level object");
                }
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    if (parser.currentToken() != JsonToken.FIELD_NAME) {
                        throw new IOException("Expected top-level property");
                    }
                    String collection = parser.currentName();
                    JsonToken value = parser.nextToken();
                    Domain.CollectionCount count = domain.collections.computeIfAbsent(collection, ignored -> new Domain.CollectionCount());
                    count.files.add(relative);
                    if (value == JsonToken.START_ARRAY) {
                        count.arrays++;
                        Stat rootStat = domain.stats.computeIfAbsent(collection, ignored -> new Stat());
                        recordType(rootStat, "array");
                        rootStat.arrayCount++;
                        if (parser.nextToken() == JsonToken.END_ARRAY) {
                            rootStat.emptyArrays++;
                            addExample(rootStat, "array", relative, count.entities, List.of());
                        } else {
                            do {
                                count.entities++;
                                ScanContext.beginEntity();
                                ScanContext.currentPath = collection + "[]";
                                Object sample = visit(parser, collection + "[]", domain, relative, count.entities, 0, false);
                                ScanContext.finishEntity();
                                if (sample != null) {
                                    addExample(rootStat, "array", relative, count.entities, List.of(sample));
                                }
                            } while (parser.nextToken() != JsonToken.END_ARRAY);
                        }
                    } else {
                        count.entities++;
                        ScanContext.beginEntity();
                        ScanContext.currentPath = collection;
                        visit(parser, collection, domain, relative, count.entities, 0, false);
                        ScanContext.finishEntity();
                    }
                }
                if (parser.nextToken() != null) {
                    throw new IOException("Trailing content after top-level object");
                }
            } catch (Exception e) {
                Map<String, Object> failure = new TreeMap<>();
                failure.put("file", relative);
                failure.put("path", ScanContext.currentPath);
                failure.put("location", location(e));
                failure.put("error", e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
                failures.add(failure);
                domain.failures.add(failure);
            }
        }

        boolean inputIsSingleDomain = domains.size() == 1 && root.getFileName() != null
                && domains.firstKey().equals(root.getFileName().toString());
        TreeMap<String, Path> reportDirectories = new TreeMap<>();
        for (Domain domain : domains.values()) {
            Path reportDirectory = inputIsSingleDomain ? output : output.resolve(domain.name);
            reportDirectories.put(domain.name, reportDirectory);
            writeDomainReports(output, domain, reportDirectory);
        }
        writeCoverageManifest(output, files.size(), domains, reportDirectories, failures);
        return new Result(files.size(), domains, failures);
    }

    private static void writeDomainReports(Path output, Domain domain, Path reportDirectory) throws IOException {
        Path directory = reportDirectory.normalize();
        if (!directory.startsWith(output.normalize())) {
            throw new IOException("Unsafe domain report path: " + domain.name);
        }
        Files.createDirectories(directory);

        List<Map<String, Object>> conflicts = new ArrayList<>();
        List<Map<String, Object>> dynamics = new ArrayList<>();
        List<Map<String, Object>> gameplay = new ArrayList<>();
        List<Map<String, Object>> propertyListing = new ArrayList<>();
        int optional = 0, metadata = 0, relevant = 0, polymorphic = 0, dynamicCount = 0;
        long occurrences = 0;

        for (var entry : domain.stats.entrySet()) {
            String path = entry.getKey();
            Stat stat = entry.getValue();
            List<String> classifications = classifications(domain, path, stat);
            String gameplayClass = gameplayClassification(path);
            if (stat.missing > 0) {
                optional++;
            }
            if (classifications.contains("POLYMORPHIC")) {
                polymorphic++;
            }
            if (gameplayClass.equals("SOURCE_METADATA")) {
                metadata++;
            }
            if (gameplayClass.startsWith("GAMEPLAY")) {
                relevant++;
            }
            occurrences += stat.occurrences;

            Map<String, Object> row = new TreeMap<>();
            row.put("path", path);
            row.put("types", stat.types);
            row.put("occurrences", stat.occurrences);
            row.put("missingCount", stat.missing);
            row.put("nullCount", stat.nullCount);
            row.put("classifications", classifications);
            row.put("gameplayClassification", gameplayClass);
            row.put("javaTypeSuggestion", javaTypeSuggestion(stat, path));
            if (stat.minStringLength != Long.MAX_VALUE) {
                row.put("minStringLength", stat.minStringLength);
                row.put("maxStringLength", stat.maxStringLength);
            }
            if (!stat.lowCardinalityValues.isEmpty()) {
                row.put("observedValues", stat.lowCardinalityValues);
            }
            if (stat.lowCardinalityExceeded) {
                row.put("distinctValueCount", ">" + MAX_DISTINCT_VALUES);
                row.put("distinctValuesTruncated", true);
            }
            if (stat.arrayCount > 0) {
                row.put("arrayCount", stat.arrayCount);
            }
            if (stat.emptyArrays > 0) {
                row.put("emptyArrayCount", stat.emptyArrays);
            }
            if (stat.emptyObjects > 0) {
                row.put("emptyObjectCount", stat.emptyObjects);
            }
            propertyListing.add(row);

            if (isConflict(stat)) {
                Map<String, Object> conflict = new TreeMap<>();
                conflict.put("path", path);
                conflict.put("classification", "POLYMORPHIC");
                conflict.put("types", stat.types);
                conflict.put("totalOccurrences", stat.occurrences);
                conflict.put("examples", stat.examples);
                conflict.put("gameplayClassification", gameplayClass);
                conflict.put("javaTypeSuggestion", javaTypeSuggestion(stat, path));
                conflicts.add(conflict);
            }
            if (isDynamicCandidate(stat)) {
                dynamicCount++;
                Map<String, Object> dynamic = new TreeMap<>();
                dynamic.put("parentPath", path);
                dynamic.put("classification", "DYNAMIC_KEYS");
                dynamic.put("observedKeyNames", stat.dynamicKeys);
                dynamic.put("keyPattern", "numeric or numeric-with-recharge-suffix");
                dynamic.put("valueTypes", stat.dynamicValueTypes);
                dynamic.put("affectedObjectOccurrences", stat.dynamicObjectCount);
                dynamic.put("confidence", stat.dynamicObjectCount >= 3 ? "HIGH" : "MEDIUM");
                dynamic.put("reason", "Object keys consistently match numeric spell-level or recharge notation.");
                dynamic.put("examples", stat.dynamicExamples);
                dynamic.put("javaTypeSuggestion", "Map<String, T> (T depends on observed value variants)");
                dynamics.add(dynamic);
            }
            Map<String, Object> item = new TreeMap<>();
            item.put("path", path);
            item.put("classification", gameplayClass);
            item.put("reason", classificationReason(path, gameplayClass));
            item.put("advisoryOnly", true);
            gameplay.add(item);
        }

        List<Map<String, Object>> variants = structuralVariants(domain);
        Map<String, Object> summary = new TreeMap<>();
        summary.put("domain", domain.name);
        summary.put("scanComplete", domain.failures.isEmpty());
        summary.put("totalFilesAnalyzed", domain.files.size());
        summary.put("totalEntitiesAnalyzed", domain.entityCount());
        summary.put("totalUniquePropertyPaths", domain.stats.size());
        summary.put("totalPolymorphicProperties", polymorphic);
        summary.put("totalDynamicKeyCandidates", dynamicCount);
        summary.put("totalOptionalProperties", optional);
        summary.put("totalPotentialInternalMetadataProperties", metadata);
        summary.put("totalPotentialGameplayProperties", relevant);
        summary.put("totalPropertyOccurrences", occurrences);
        summary.put("sourceFiles", domain.files);
        summary.put("collectionEntityCounts", domain.collectionCounts());
        summary.put("collections", collectionReportRows(domain, directory, output));
        summary.put("properties", propertyListing);
        summary.put("structuralVariants", variants);
        summary.put("scanFailures", domain.failures);
        summary.put("classificationDefinitions", classificationDefinitions());
        summary.put("classificationRules", classificationRules());
        Map<String, Object> configuration = new TreeMap<>();
        configuration.put("maxExampleChars", MAX_EXAMPLE_CHARS);
        configuration.put("maxExampleDepth", MAX_EXAMPLE_DEPTH);
        configuration.put("maxExampleFields", MAX_EXAMPLE_FIELDS);
        configuration.put("maxDistinctValues", MAX_DISTINCT_VALUES);
        configuration.put("examplesPerType", EXAMPLES_PER_TYPE);
        summary.put("configuration", configuration);
        writeJson(directory.resolve("schema-summary.json"), summary);
        Map<String, Object> conflictsReport = new TreeMap<>();
        conflictsReport.put("domain", domain.name);
        conflictsReport.put("scanComplete", domain.failures.isEmpty());
        conflictsReport.put("conflicts", conflicts);
        conflictsReport.put("scanFailures", domain.failures);
        writeJson(directory.resolve("schema-conflicts.json"), conflictsReport);
        Map<String, Object> dynamicsReport = new TreeMap<>();
        dynamicsReport.put("domain", domain.name);
        dynamicsReport.put("scanComplete", domain.failures.isEmpty());
        dynamicsReport.put("candidates", dynamics);
        dynamicsReport.put("scanFailures", domain.failures);
        writeJson(directory.resolve("schema-dynamic-keys.json"), dynamicsReport);
        Map<String, Object> gameplayReport = new TreeMap<>();
        gameplayReport.put("domain", domain.name);
        gameplayReport.put("scanComplete", domain.failures.isEmpty());
        gameplayReport.put("advisoryOnly", true);
        gameplayReport.put("classificationDefinitions", classificationDefinitions());
        gameplayReport.put("classificationRules", classificationRules());
        gameplayReport.put("properties", gameplay);
        gameplayReport.put("scanFailures", domain.failures);
        writeJson(directory.resolve("schema-gameplay.json"), gameplayReport);
    }

    private static List<Map<String, Object>> collectionReportRows(Domain domain, Path reportDirectory, Path outputRoot) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (var entry : domain.collections.entrySet()) {
            String collection = entry.getKey();
            Domain.CollectionCount count = entry.getValue();
            long paths = domain.stats.keySet().stream().filter(path -> belongsToCollection(path, collection)).count();
            Map<String, Object> row = new TreeMap<>();
            row.put("collection", collection);
            row.put("contributingFileCount", count.files.size());
            row.put("sourceFiles", count.files);
            row.put("entityCount", count.entities);
            row.put("discoveredPropertyPathCount", paths);
            String location = outputRoot.relativize(reportDirectory).toString().replace('\\', '/');
            if (location.isBlank() || location.equals(".")) {
                location = ".";
            }
            List<Map<String, Object>> reports = new ArrayList<>();
            for (String file : List.of("schema-summary.json", "schema-conflicts.json", "schema-dynamic-keys.json", "schema-gameplay.json")) {
                Map<String, Object> report = new TreeMap<>();
                report.put("path", location.equals(".") ? file : location + "/" + file);
                report.put("propertyPathPrefix", collection);
                reports.add(report);
            }
            row.put("reportLocations", reports);
            rows.add(row);
        }
        return rows;
    }

    private static boolean belongsToCollection(String path, String collection) {
        return path.equals(collection) || path.startsWith(collection + ".") || path.startsWith(collection + "[]");
    }

    private static void writeCoverageManifest(Path output, int scannedFiles, Map<String, Domain> domains,
                                              Map<String, Path> reportDirectories,
                                              List<Map<String, Object>> failures) throws IOException {
        List<Map<String, Object>> collections = new ArrayList<>();
        TreeSet<String> collectionFiles = new TreeSet<>();
        for (Domain domain : domains.values()) {
            Path reportDirectory = reportDirectories.get(domain.name);
            collections.addAll(collectionReportRows(domain, reportDirectory, output));
            for (Domain.CollectionCount count : domain.collections.values()) {
                collectionFiles.addAll(count.files);
            }
            for (int i = collections.size() - domain.collections.size(); i < collections.size(); i++) {
                collections.get(i).put("domain", domain.name);
            }
        }
        TreeSet<String> allFiles = new TreeSet<>();
        for (Domain domain : domains.values()) {
            allFiles.addAll(domain.files);
        }
        TreeSet<String> unaccountedFiles = new TreeSet<>(allFiles);
        unaccountedFiles.removeAll(collectionFiles);
        Map<String, Object> manifest = new TreeMap<>();
        manifest.put("scanComplete", failures.isEmpty() && unaccountedFiles.isEmpty());
        manifest.put("totalFilesAnalyzed", scannedFiles);
        manifest.put("totalCollections", collections.size());
        manifest.put("accountedFileCount", collectionFiles.size());
        manifest.put("unaccountedFiles", unaccountedFiles);
        manifest.put("scanFailures", failures);
        manifest.put("collections", collections);
        writeJson(output.resolve("coverage-manifest.json"), manifest);
    }

    private static Object visit(JsonParser parser, String rawPath, Domain domain, String file, long entity,
                                int depth, boolean forceCapture) throws IOException {
        String path = normalizePath(rawPath);
        JsonToken token = parser.currentToken();
        String type = type(token);
        ScanContext.currentPath = path;
        Stat stat = domain.stats.computeIfAbsent(path, ignored -> new Stat());
        recordType(stat, type);
        boolean capture = forceCapture || stat.examples.getOrDefault(type, List.of()).size() < EXAMPLES_PER_TYPE;
        Object sample = null;

        if (token == JsonToken.START_OBJECT) {
            stat.objectCount++;
            domain.objectInstances.merge(path, 1L, Long::sum);
            TreeSet<String> present = new TreeSet<>();
            Map<String, Object> sampleObject = capture ? new TreeMap<>() : null;
            int fields = 0;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                if (parser.currentToken() != JsonToken.FIELD_NAME) {
                    throw new IOException("Expected object property at " + path);
                }
                String name = parser.currentName();
                present.add(name);
                fields++;
                String childPath = normalizePath(path + "." + name);
                domain.stats.computeIfAbsent(childPath, ignored -> {
                    Stat created = new Stat();
                    created.parentCount = domain.objectInstances.getOrDefault(path, 1L) - 1;
                    domain.children.computeIfAbsent(path, unused -> new TreeSet<>()).add(childPath);
                    return created;
                });
                parser.nextToken();
                JsonToken childToken = parser.currentToken();
                if (depth == 0 && (name.equals("name") || (name.equals("id") && ScanContext.entityIdentifier == null))
                        && childToken.isScalarValue() && childToken != JsonToken.VALUE_NULL) {
                    ScanContext.entityIdentifier = truncate(parser.getValueAsString());
                }
                Object childSample = visit(parser, childPath, domain, file, entity, depth + 1,
                        capture && depth < MAX_EXAMPLE_DEPTH && fields <= MAX_EXAMPLE_FIELDS);
                if (sampleObject != null && fields <= MAX_EXAMPLE_FIELDS && depth < MAX_EXAMPLE_DEPTH) {
                    sampleObject.put(name, childSample == null && childToken != JsonToken.VALUE_NULL ? "<sample omitted>" : childSample);
                }
            }
            for (String childPath : domain.children.getOrDefault(path, new TreeSet<>())) {
                Stat child = domain.stats.get(childPath);
                child.parentCount++;
                if (!present.contains(childPath.substring(path.length() + 1))) {
                    child.missing++;
                }
            }
            if (sampleObject != null) {
                if (fields > MAX_EXAMPLE_FIELDS) {
                    sampleObject.put("$schemaAnalysisOmittedFields", fields - MAX_EXAMPLE_FIELDS);
                }
                if (depth >= MAX_EXAMPLE_DEPTH && fields > 0) {
                    sampleObject.put("$schemaAnalysisMaxDepthReached", true);
                }
            }
            if (present.isEmpty()) {
                stat.emptyObjects++;
            }
            observeObjectShape(stat, present);
            if (present.size() >= 2 && present.stream().allMatch(k -> NUMERIC_KEY.matcher(k).matches())) {
                stat.dynamicObjectCount++;
                for (String key : present) {
                    stat.dynamicKeys.add(key);
                    Stat child = domain.stats.get(normalizePath(path + "." + key));
                    if (child != null) {
                        stat.dynamicValueTypes.addAll(child.types.keySet());
                    }
                }
                if (stat.dynamicExamples.size() < EXAMPLES_PER_TYPE && sampleObject != null) {
                    addDynamicExample(stat, file, entity, sampleObject);
                }
            }
            sample = sampleObject;
            if (capture) {
                addExample(stat, type, file, entity, sample);
            }
        } else if (token == JsonToken.START_ARRAY) {
            stat.arrayCount++;
            List<Object> sampleArray = capture ? new ArrayList<>() : null;
            int index = 0;
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                index++;
                JsonToken elementToken = parser.currentToken();
                Object childSample = visit(parser, path + "[]", domain, file, entity, depth + 1,
                        capture && depth < MAX_EXAMPLE_DEPTH && index <= MAX_EXAMPLE_FIELDS);
                if (sampleArray != null && index <= MAX_EXAMPLE_FIELDS && depth < MAX_EXAMPLE_DEPTH) {
                    sampleArray.add(childSample == null && elementToken != JsonToken.VALUE_NULL ? "<sample omitted>" : childSample);
                }
            }
            if (sampleArray != null && index > MAX_EXAMPLE_FIELDS) {
                Map<String, Object> omitted = new TreeMap<>();
                omitted.put("$schemaAnalysisOmittedElements", index - MAX_EXAMPLE_FIELDS);
                sampleArray.add(omitted);
            }
            if (sampleArray != null && depth >= MAX_EXAMPLE_DEPTH && index > 0) {
                sampleArray.clear();
                Map<String, Object> omitted = new TreeMap<>();
                omitted.put("$schemaAnalysisMaxDepthReached", true);
                sampleArray.add(omitted);
            }
            if (index == 0) {
                stat.emptyArrays++;
            }
            sample = sampleArray;
            if (capture) {
                addExample(stat, type, file, entity, sample);
            }
        } else {
            if (token == JsonToken.VALUE_NULL) {
                stat.nullCount++;
            }
            if (token == JsonToken.VALUE_STRING) {
                int length = parser.getTextLength();
                stat.minStringLength = Math.min(stat.minStringLength, length);
                stat.maxStringLength = Math.max(stat.maxStringLength, length);
                if (isLowCardinalityPath(path)) {
                    stat.recordValue(parser.getText(), MAX_DISTINCT_VALUES);
                }
            } else if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                stat.recordValue(parser.getText(), MAX_DISTINCT_VALUES);
            }
            if (capture) {
                sample = scalarValue(parser, token);
            }
            if (capture) {
                addExample(stat, type, file, entity, sample);
            }
        }
        return capture ? bounded(sample, depth) : null;
    }

    private static void observeObjectShape(Stat stat, Set<String> fields) {
        String signature = String.join("|", fields);
        stat.objectShapes.merge(signature, 1L, Long::sum);
        if (stat.objectShapes.size() > 20) {
            stat.objectShapes.remove(stat.objectShapes.lastKey());
        }
    }

    private static List<Map<String, Object>> structuralVariants(Domain domain) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : domain.stats.entrySet()) {
            Stat stat = entry.getValue();
            List<Map.Entry<String, Long>> common = stat.objectShapes.entrySet().stream()
                    .filter(e -> e.getValue() >= 2).sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                            .thenComparing(Map.Entry.comparingByKey())).toList();
            if (common.size() < 2) {
                continue;
            }
            boolean meaningfullyDifferent = false;
            for (int i = 0; i < common.size() && !meaningfullyDifferent; i++) {
                for (int j = i + 1; j < common.size(); j++) {
                    if (meaningfullyDifferent(common.get(i).getKey(), common.get(j).getKey()) >= 2) {
                        meaningfullyDifferent = true;
                        break;
                    }
                }
            }
            if (!meaningfullyDifferent) {
                continue;
            }
            Map<String, Object> variant = new TreeMap<>();
            variant.put("path", entry.getKey());
            variant.put("classification", "STRUCTURAL_VARIANT");
            variant.put("reason", "Repeated objects have materially different field sets after excluding one-field optionality.");
            List<Map<String, Object>> variantItems = new ArrayList<>();
            for (var shape : common.stream().limit(5).toList()) {
                Map<String, Object> shapeItem = new TreeMap<>();
                shapeItem.put("fields", splitShape(shape.getKey()));
                shapeItem.put("count", shape.getValue());
                variantItems.add(shapeItem);
            }
            variant.put("variants", variantItems);
            result.add(variant);
        }
        return result;
    }

    private static int meaningfullyDifferent(String a, String b) {
        Set<String> left = new TreeSet<>(splitShape(a)), right = new TreeSet<>(splitShape(b));
        Set<String> difference = new TreeSet<>(left);
        difference.removeAll(right);
        Set<String> reverse = new TreeSet<>(right);
        reverse.removeAll(left);
        difference.addAll(reverse);
        return difference.size();
    }

    private static List<String> splitShape(String s) {
        return s.isEmpty() ? List.of() : List.of(s.split("\\|", -1));
    }

    private static List<String> classifications(Domain domain, String path, Stat stat) {
        List<String> result = new ArrayList<>();
        if (isConflict(stat)) {
            result.add("POLYMORPHIC");
        }
        if (stat.missing > 0) {
            result.add("OPTIONAL");
        }
        if (isDynamicCandidate(stat)) {
            result.add("DYNAMIC_KEYS");
        }
        if (stat.arrayCount > 0 && arrayIsHeterogeneous(domain, path)) {
            result.add("HETEROGENEOUS_ARRAY");
        }
        if (hasStructuralVariants(stat)) {
            result.add("STRUCTURAL_VARIANT");
        }
        if (result.isEmpty()) {
            result.add("SIMPLE");
        }
        return result;
    }

    private static boolean arrayIsHeterogeneous(Domain domain, String path) {
        Stat elements = domain.stats.get(path + "[]");
        return elements != null && (isConflict(elements) || hasStructuralVariants(elements));
    }

    private static boolean hasStructuralVariants(Stat stat) {
        List<String> shapes = stat.objectShapes.entrySet().stream().filter(e -> e.getValue() >= 2)
                .map(Map.Entry::getKey).toList();
        if (shapes.size() < 2) {
            return false;
        }
        for (int i = 0; i < shapes.size(); i++) {
            for (int j = i + 1; j < shapes.size(); j++) {
                if (meaningfullyDifferent(shapes.get(i), shapes.get(j)) >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isConflict(Stat stat) {
        if (stat.types.size() < 2) {
            return false;
        }
        boolean hasOnlyNumericVariants = stat.types.keySet().stream().allMatch(t -> t.equals("integer") || t.equals("number"));
        return !hasOnlyNumericVariants;
    }

    private static boolean isDynamicCandidate(Stat stat) {
        return stat.dynamicObjectCount > 0;
    }

    private static String gameplayClassification(String path) {
        String lower = path.toLowerCase();
        List<String> segments = pathSegments(lower);
        if (path.indexOf('.') >= 0 && !segments.isEmpty()) {
            segments.remove(0);
        }
        if (segments.stream().anyMatch(METADATA_TOKENS::contains)) {
            return "SOURCE_METADATA";
        }
        if (segments.stream().anyMatch(CORE_TOKENS::contains)) {
            return "GAMEPLAY_CORE";
        }
        if (segments.stream().anyMatch(DESCRIPTION_TOKENS::contains)) {
            return "GAMEPLAY_DESCRIPTION";
        }
        if (segments.stream().anyMatch(PRESENTATION_TOKENS::contains)) {
            return "PRESENTATION";
        }
        return "UNKNOWN";
    }

    private static List<String> pathSegments(String path) {
        List<String> result = new ArrayList<>();
        for (String segment : path.split("[.\\[\\]]+")) {
            if (!segment.isBlank()) {
                result.add(segment);
            }
        }
        return result;
    }

    private static String classificationReason(String path, String classification) {
        return switch (classification) {
            case "SOURCE_METADATA" ->
                    "Path contains a source, import, page, or 5eTools-internal marker; retain until reviewed.";
            case "GAMEPLAY_CORE" -> "Path matches a combat or rules mechanic; verify semantics before modeling.";
            case "GAMEPLAY_DESCRIPTION" ->
                    "Rules-bearing descriptive text or entry structure may encode mechanics or inline tags.";
            case "PRESENTATION" -> "Likely display or identity information; it may still influence gameplay.";
            default -> "No confident rule matched.";
        };
    }

    private static Map<String, String> classificationDefinitions() {
        Map<String, String> definitions = new TreeMap<>();
        definitions.put("GAMEPLAY_CORE", "Combat and rules mechanics including AC, HP, speed, attacks, damage, saves, spells, defenses, conditions, recharge, and uses.");
        definitions.put("GAMEPLAY_DESCRIPTION", "Rule-bearing descriptive structures such as entries, actions, traits, reactions, and spell text; inline markup is retained.");
        definitions.put("PRESENTATION", "Names, display identity, size, alignment, creature type, and descriptive presentation.");
        definitions.put("SOURCE_METADATA", "Source references, pages, import metadata, and likely 5eTools-specific fields; advisory only.");
        definitions.put("UNKNOWN", "No confident rule matched; manual review needed.");
        return definitions;
    }

    private static Map<String, Object> classificationRules() {
        Map<String, Object> rules = new TreeMap<>();
        rules.put("matching", "Case-insensitive exact path segments; earlier categories take precedence in this order: SOURCE_METADATA, GAMEPLAY_CORE, GAMEPLAY_DESCRIPTION, PRESENTATION, UNKNOWN.");
        rules.put("SOURCE_METADATA", METADATA_TOKENS);
        rules.put("GAMEPLAY_CORE", CORE_TOKENS);
        rules.put("GAMEPLAY_DESCRIPTION", DESCRIPTION_TOKENS);
        rules.put("PRESENTATION", PRESENTATION_TOKENS);
        Map<String, String> propertyNames = new TreeMap<>();
        propertyNames.put("SOURCE_METADATA", "schema.classification.metadata");
        propertyNames.put("GAMEPLAY_CORE", "schema.classification.gameplayCore");
        propertyNames.put("GAMEPLAY_DESCRIPTION", "schema.classification.gameplayDescription");
        propertyNames.put("PRESENTATION", "schema.classification.presentation");
        rules.put("configurationProperties", propertyNames);
        return rules;
    }

    private static Set<String> configuredTokens(String property, String defaults) {
        String value = System.getProperty(property, defaults);
        TreeSet<String> result = new TreeSet<>();
        for (String token : value.split(",")) {
            if (!token.isBlank()) {
                result.add(token.trim().toLowerCase());
            }
        }
        return result;
    }

    private static String javaTypeSuggestion(Stat stat, String path) {
        if (isDynamicCandidate(stat)) {
            return "Map<String, T> with a typed value variant; inspect schema-dynamic-keys.json";
        }
        if (isConflict(stat)) {
            return "Dedicated polymorphic record/union with a custom Jackson deserializer";
        }
        if (stat.arrayCount > 0) {
            return "List<T> (inspect element types and structural variants)";
        }
        if (stat.types.containsKey("object")) {
            return "Dedicated Java record";
        }
        if (stat.types.containsKey("string")) {
            return "String";
        }
        if (stat.types.containsKey("integer")) {
            return "Integer or Long (range not inferred)";
        }
        if (stat.types.containsKey("number")) {
            return "BigDecimal or Double (precision requirements not inferred)";
        }
        if (stat.types.containsKey("boolean")) {
            return "Boolean";
        }
        if (stat.types.containsKey("null")) {
            return "Nullable type; observed non-null type needed";
        }
        return "Review structure";
    }

    private static boolean isLowCardinalityPath(String path) {
        String last = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
        return Set.of("type", "size", "alignment", "ability", "abilities", "damagetype", "damagetypes",
                "school", "rarity", "spellattack", "save", "skill", "creaturetype").contains(last);
    }

    private static String normalizePath(String path) {
        return path.replaceAll("(\\.entries\\[\\]){3,}", ".entries[].entries[]");
    }

    private static String type(JsonToken token) {
        if (token == JsonToken.START_OBJECT) {
            return "object";
        }
        if (token == JsonToken.START_ARRAY) {
            return "array";
        }
        if (token == JsonToken.VALUE_STRING) {
            return "string";
        }
        if (token == JsonToken.VALUE_NUMBER_INT) {
            return "integer";
        }
        if (token == JsonToken.VALUE_NUMBER_FLOAT) {
            return "number";
        }
        if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
            return "boolean";
        }
        return "null";
    }

    private static Object scalarValue(JsonParser parser, JsonToken token) throws IOException {
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }
        if (token == JsonToken.VALUE_TRUE) {
            return true;
        }
        if (token == JsonToken.VALUE_FALSE) {
            return false;
        }
        if (token == JsonToken.VALUE_NUMBER_INT) {
            return parser.getNumberValue();
        }
        if (token == JsonToken.VALUE_NUMBER_FLOAT) {
            return parser.getDecimalValue();
        }
        return truncate(parser.getText());
    }

    private static Object bounded(Object sample, int depth) {
        return depth > MAX_EXAMPLE_DEPTH ? "<max-depth>" : sample;
    }

    private static String truncate(String value) {
        return value != null && value.length() > MAX_EXAMPLE_CHARS ? value.substring(0, MAX_EXAMPLE_CHARS) + "...<truncated>" : value;
    }

    private static void recordType(Stat stat, String type) {
        stat.types.merge(type, 1L, Long::sum);
        stat.occurrences++;
    }

    private static void addExample(Stat stat, String type, String file, long entity, Object value) {
        List<Map<String, Object>> examples = stat.examples.computeIfAbsent(type, ignored -> new ArrayList<>());
        if (examples.size() >= EXAMPLES_PER_TYPE) {
            return;
        }
        Map<String, Object> example = new TreeMap<>();
        example.put("file", file);
        example.put("entity", entity);
        example.put("entityIndex", entity);
        example.put("value", value);
        examples.add(example);
        if (ScanContext.entityExamples != null) {
            ScanContext.entityExamples.add(example);
        }
    }

    private static void addDynamicExample(Stat stat, String file, long entity, Object value) {
        Map<String, Object> example = new TreeMap<>();
        example.put("file", file);
        example.put("entity", entity);
        example.put("entityIndex", entity);
        example.put("value", value);
        stat.dynamicExamples.add(example);
        if (ScanContext.entityExamples != null) {
            ScanContext.entityExamples.add(example);
        }
    }

    private static String location(Exception error) {
        if (error instanceof JsonProcessingException json && json.getLocation() != null) {
            return "line " + json.getLocation().getLineNr() + ", column " + json.getLocation().getColumnNr();
        }
        return "unknown location";
    }

    private static void writeJson(Path path, Object value) throws IOException {
        try (JsonGenerator generator = JSON.createGenerator(Files.newOutputStream(path))) {
            generator.useDefaultPrettyPrinter();
            writeValue(generator, value);
        }
    }

    private static void writeValue(JsonGenerator g, Object value) throws IOException {
        if (value instanceof Map<?, ?> map) {
            g.writeStartObject();
            for (var e : map.entrySet()) {
                g.writeFieldName(String.valueOf(e.getKey()));
                writeValue(g, e.getValue());
            }
            g.writeEndObject();
        } else if (value instanceof Iterable<?> items) {
            g.writeStartArray();
            for (Object item : items) {
                writeValue(g, item);
            }
            g.writeEndArray();
        } else if (value instanceof Integer n) {
            g.writeNumber(n);
        } else if (value instanceof Long n) {
            g.writeNumber(n);
        } else if (value instanceof BigInteger n) {
            g.writeNumber(n);
        } else if (value instanceof BigDecimal n) {
            g.writeNumber(n);
        } else if (value instanceof Number n) {
            g.writeNumber(n.doubleValue());
        } else if (value instanceof Boolean b) {
            g.writeBoolean(b);
        } else if (value == null) {
            g.writeNull();
        } else {
            g.writeString(value.toString());
        }
    }

    record Result(int scannedFiles, Map<String, Domain> domains, List<Map<String, Object>> failures) {
    }

    static final class ScanContext {
        static String currentPath = "";
        static String entityIdentifier;
        static List<Map<String, Object>> entityExamples;

        static void beginEntity() {
            entityIdentifier = null;
            entityExamples = new ArrayList<>();
        }

        static void finishEntity() {
            if (entityIdentifier != null) {
                for (Map<String, Object> example : entityExamples) {
                    example.put("entity", entityIdentifier);
                }
            }
            entityExamples = null;
        }
    }

    static final class Domain {
        final String name;
        final TreeSet<String> files = new TreeSet<>();
        final TreeMap<String, Stat> stats = new TreeMap<>();
        final TreeMap<String, TreeSet<String>> children = new TreeMap<>();
        final TreeMap<String, CollectionCount> collections = new TreeMap<>();
        final TreeMap<String, Long> objectInstances = new TreeMap<>();
        final List<Map<String, Object>> failures = new ArrayList<>();

        Domain(String name) {
            this.name = name;
        }

        long entityCount() {
            return collections.values().stream().mapToLong(c -> c.entities).sum();
        }

        long pathCount() {
            return stats.size();
        }

        Map<String, Long> collectionCounts() {
            TreeMap<String, Long> result = new TreeMap<>();
            collections.forEach((k, v) -> result.put(k, v.entities));
            return result;
        }

        static final class CollectionCount {
            long entities, arrays;
            final TreeSet<String> files = new TreeSet<>();
        }
    }

    static final class Stat {
        final TreeMap<String, Long> types = new TreeMap<>();
        final TreeMap<String, List<Map<String, Object>>> examples = new TreeMap<>();
        final TreeMap<String, Long> objectShapes = new TreeMap<>();
        final TreeSet<String> dynamicKeys = new TreeSet<>();
        final TreeSet<String> dynamicValueTypes = new TreeSet<>();
        final List<Map<String, Object>> dynamicExamples = new ArrayList<>();
        final TreeSet<String> lowCardinalityValues = new TreeSet<>();
        long occurrences, parentCount, missing, nullCount, objectCount, arrayCount, emptyArrays, emptyObjects;
        long minStringLength = Long.MAX_VALUE, maxStringLength, dynamicObjectCount;
        boolean lowCardinalityExceeded;

        void recordValue(String value, int max) {
            if (lowCardinalityValues.contains(value)) {
                return;
            }
            if (lowCardinalityValues.size() < max) {
                lowCardinalityValues.add(value);
            } else lowCardinalityExceeded = true;
        }
    }
}
