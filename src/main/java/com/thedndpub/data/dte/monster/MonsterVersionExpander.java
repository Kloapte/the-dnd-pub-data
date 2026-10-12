package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Expands 5eTools version definitions against already resolved base monsters.
 */
public final class MonsterVersionExpander {
    private static final Pattern VARIABLE = Pattern.compile("\\{\\{([^}]+)}}");
    private static final Set<String> COPIED_BASE_FIELDS_TO_REMOVE = Set.of("_versions", "hasToken", "hasFluff", "hasFluffImages");
    private static final List<String> SOURCE_ARRAYS = List.of("additionalSources", "otherSources", "referenceSources");

    private final ObjectMapper mapper;
    private final MonsterCopyResolver copyResolver;

    public MonsterVersionExpander(ObjectMapper mapper, MonsterCopyResolver copyResolver) {
        this.mapper = Objects.requireNonNull(mapper);
        this.copyResolver = Objects.requireNonNull(copyResolver);
    }

    /**
     * Returns all original monsters with source `_versions` moved to provenance, followed by generated versions.
     */
    public List<ResolvedMonster> expandAll(List<ResolvedMonster> resolvedMonsters) {
        Objects.requireNonNull(resolvedMonsters, "resolvedMonsters");
        List<ResolvedMonster> bases = new ArrayList<>(resolvedMonsters);
        bases.sort(Comparator.comparing(ResolvedMonster::identity).thenComparing(ResolvedMonster::location,
                Comparator.comparing(MonsterSourceLocation::filename).thenComparingInt(MonsterSourceLocation::arrayIndex)));

        Map<MonsterIdentity, ResolvedMonster> baseIndex = new TreeMap<>();
        for (ResolvedMonster base : bases) {
            if (baseIndex.putIfAbsent(base.identity(), base) != null) {
                throw new MonsterResolutionException("Duplicate resolved monster identity " + base.identity() + " at " + base.location());
            }
            if (base.content().has("_copy")) {
                throw new MonsterResolutionException(base.identity() + " at " + base.location() + " still has an active _copy instruction");
            }
        }

        List<ResolvedMonster> originals = new ArrayList<>(bases.size());
        List<ResolvedMonster> generated = new ArrayList<>();
        for (ResolvedMonster base : bases) {
            ObjectNode content = base.content();
            JsonNode versions = content.remove("_versions");
            List<JsonNode> sourceDefinitions = new ArrayList<>();
            if (versions != null && !versions.isNull()) {
                if (!versions.isArray()) {
                    throw versionError(base, -1, null, "$._versions must be an array");
                }
                versions.forEach(v -> sourceDefinitions.add(v.deepCopy()));
            }
            ResolutionProvenance provenance = base.provenance().withSourceVersions(sourceDefinitions);
            originals.add(new ResolvedMonster(base.identity(), base.location(), base.copyParent(), base.appliedTemplates(),
                    content, provenance));

            if (versions == null || versions.isNull()) {
                continue;
            }
            for (int versionIndex = 0; versionIndex < versions.size(); versionIndex++) {
                JsonNode definition = versions.get(versionIndex);
                if (!(definition instanceof ObjectNode object)) {
                    throw versionError(base, versionIndex, null, "$._versions[" + versionIndex + "] must be an object");
                }
                if (object.has("_abstract") || object.has("_implementations")) {
                    expandAbstract(base, object, versionIndex, generated);
                } else {
                    generated.add(expandConcrete(base, object, versionIndex, null, Map.of()));
                }
            }
        }

        generated.sort(Comparator.comparing((ResolvedMonster m) -> m.provenance().generatedVersion().containingMonster())
                .thenComparingInt(m -> m.provenance().generatedVersion().versionDefinitionIndex())
                .thenComparing(m -> Optional.ofNullable(m.provenance().generatedVersion().implementationIndex()).orElse(-1))
                .thenComparing(ResolvedMonster::identity));

        Set<MonsterIdentity> identities = new HashSet<>();
        for (ResolvedMonster original : originals) {
            identities.add(original.identity());
        }
        for (ResolvedMonster variant : generated) {
            if (!identities.add(variant.identity())) {
                MonsterVersionProvenance p = variant.provenance().generatedVersion();
                throw versionError(variant, p.versionDefinitionIndex(), p.implementationIndex(),
                        "generated identity collides with another catalog entry: " + variant.identity());
            }
        }

        List<ResolvedMonster> result = new ArrayList<>(originals.size() + generated.size());
        result.addAll(originals);
        result.addAll(generated);
        for (ResolvedMonster monster : result) {
            ObjectNode content = monster.content();
            if (content.has("_copy") || content.has("_versions")) {
                throw new MonsterResolutionException(monster.identity() + " at " + monster.location() + " has unresolved version/copy instructions");
            }
        }
        return List.copyOf(result);
    }

    private void expandAbstract(ResolvedMonster base, ObjectNode wrapper, int versionIndex, List<ResolvedMonster> output) {
        JsonNode abstractNode = wrapper.get("_abstract");
        JsonNode implementations = wrapper.get("_implementations");
        if (!(abstractNode instanceof ObjectNode abstractDefinition)) {
            throw versionError(base, versionIndex, null, "$._versions[" + versionIndex + "]._abstract must be an object");
        }
        if (!(implementations instanceof ArrayNode impls) || impls.isEmpty()) {
            throw versionError(base, versionIndex, null, "$._versions[" + versionIndex + "]._implementations must be a nonempty array");
        }
        for (int implementationIndex = 0; implementationIndex < impls.size(); implementationIndex++) {
            JsonNode implementation = impls.get(implementationIndex);
            if (!(implementation instanceof ObjectNode impl)) {
                throw versionError(base, versionIndex, implementationIndex,
                        "$._versions[" + versionIndex + "]._implementations[" + implementationIndex + "] must be an object");
            }
            Map<String, String> variables = readVariables(base, impl, versionIndex, implementationIndex);
            ObjectNode concrete = abstractDefinition.deepCopy();
            if (impl.has("_variables")) {
                substituteVariables(base, concrete, variables, versionIndex, implementationIndex);
            }
            impl.fields().forEachRemaining(entry -> {
                if (!entry.getKey().equals("_variables")) {
                    concrete.set(entry.getKey(), entry.getValue().deepCopy());
                }
            });
            output.add(expandConcrete(base, concrete, versionIndex, implementationIndex, variables));
        }
    }

    private Map<String, String> readVariables(ResolvedMonster base, ObjectNode impl, int versionIndex, int implementationIndex) {
        JsonNode variables = impl.get("_variables");
        if (variables == null) {
            return Map.of();
        }
        if (!variables.isObject()) {
            throw versionError(base, versionIndex, implementationIndex, "$._versions[" + versionIndex + "]._implementations[" + implementationIndex + "]._variables must be an object");
        }
        Map<String, String> result = new TreeMap<>();
        variables.fields().forEachRemaining(entry -> {
            if (!entry.getValue().isTextual()) {
                throw versionError(base, versionIndex, implementationIndex,
                        "$._versions[" + versionIndex + "]._implementations[" + implementationIndex + "]._variables." + entry.getKey() + " must be a string");
            }
            result.put(entry.getKey(), entry.getValue().asText());
        });
        return Map.copyOf(result);
    }

    private void substituteVariables(ResolvedMonster base, JsonNode node, Map<String, String> variables,
                                     int versionIndex, int implementationIndex) {
        if (node instanceof ObjectNode object) {
            List<String> names = new ArrayList<>();
            object.fieldNames().forEachRemaining(names::add);
            for (String name : names) {
                JsonNode value = object.get(name);
                if (value.isTextual()) {
                    object.put(name, substituteString(base, value.asText(), variables, versionIndex, implementationIndex, "$._versions[" + versionIndex + "]._abstract." + name));
                } else {
                    substituteVariables(base, value, variables, versionIndex, implementationIndex);
                }
            }
        } else if (node instanceof ArrayNode array) {
            for (int i = 0; i < array.size(); i++) {
                JsonNode value = array.get(i);
                if (value.isTextual()) {
                    array.set(i, TextNode.valueOf(substituteString(base, value.asText(), variables, versionIndex, implementationIndex,
                            "$._versions[" + versionIndex + "]._abstract[" + i + "]")));
                } else {
                    substituteVariables(base, value, variables, versionIndex, implementationIndex);
                }
            }
        }
    }

    private String substituteString(ResolvedMonster base, String input, Map<String, String> variables,
                                    int versionIndex, int implementationIndex, String path) {
        Matcher matcher = VARIABLE.matcher(input);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = variables.get(key);
            if (replacement == null) {
                throw versionError(base, versionIndex, implementationIndex, path + " contains unresolved placeholder {{" + key + "}}");
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private ResolvedMonster expandConcrete(ResolvedMonster base, ObjectNode rawVersion, int versionIndex,
                                           Integer implementationIndex, Map<String, String> variables) {
        ObjectNode version = rawVersion.deepCopy();
        if (version.has("_copy")) {
            throw versionError(base, versionIndex, implementationIndex, "version definition must not contain its own _copy");
        }
        String name = requiredText(base, version, "name", versionIndex, implementationIndex);
        String source = requiredText(base, version, "source", versionIndex, implementationIndex);
        MonsterIdentity identity = MonsterIdentity.from(name, source);

        JsonNode modifier = version.remove("_mod");
        JsonNode templateRefs = version.remove("_templates");
        JsonNode preserve = version.remove("_preserve");
        version.remove("_versions");

        ObjectNode copy = mapper.createObjectNode().put("name", base.identity().name()).put("source", base.identity().source());
        if (modifier != null && !modifier.isNull()) {
            if (!modifier.isObject()) {
                throw versionError(base, versionIndex, implementationIndex, "$._versions[" + versionIndex + "]._mod must be an object");
            }
            copy.set("_mod", modifier.deepCopy());
        }
        if (templateRefs != null && !templateRefs.isNull()) {
            if (!templateRefs.isArray()) {
                throw versionError(base, versionIndex, implementationIndex, "$._versions[" + versionIndex + "]._templates must be an array");
            }
            copy.set("_templates", templateRefs.deepCopy());
        }
        if (preserve != null && !preserve.isNull()) {
            if (!preserve.isObject()) {
                throw versionError(base, versionIndex, implementationIndex, "$._versions[" + versionIndex + "]._preserve must be an object");
            }
            copy.set("_preserve", preserve.deepCopy());
        } else {
            copy.putObject("_preserve").put("*", true);
        }
        version.set("_copy", copy);

        ObjectNode parentContent = base.content();
        COPIED_BASE_FIELDS_TO_REMOVE.forEach(parentContent::remove);
        for (String property : SOURCE_ARRAYS) {
            filterVersionSource(parentContent, property, source, base, versionIndex, implementationIndex);
        }

        MonsterDte parentDte = emptyDte();
        MonsterDte versionDte = new MonsterDte(null, null, null, null, null, null,
                new MonsterCopyReferenceDte(base.identity().name(), base.identity().source(), null, null, readTemplateReferences(templateRefs, base, versionIndex, implementationIndex)), null);
        MonsterSourceRecord parentRecord = new MonsterSourceRecord(base.identity(), base.location(), parentDte, parentContent);
        MonsterSourceRecord versionRecord = new MonsterSourceRecord(identity, base.location(), versionDte, version);

        List<ResolvedMonster> copyResolved;
        try {
            copyResolved = copyResolver.resolveAll(List.of(parentRecord, versionRecord));
        } catch (MonsterResolutionException e) {
            throw versionError(base, versionIndex, implementationIndex, "modifier/template resolution failed: " + e.getMessage(), e);
        }
        ResolvedMonster resolved = copyResolved.stream().filter(m -> m.identity().equals(identity)).findFirst()
                .orElseThrow(() -> versionError(base, versionIndex, implementationIndex, "copy resolver did not return generated identity " + identity));
        ObjectNode finalContent = resolved.content();
        for (String instruction : List.of("_copy", "_versions", "_mod", "_templates", "_preserve")) {
            if (finalContent.has(instruction)) {
                throw versionError(base, versionIndex, implementationIndex, "resolved version retains active instruction " + instruction);
            }
        }

        // These modes describe work performed for this generated version. The
        // containing monster's already-executed modes remain on its own provenance.
        Set<String> modes = new TreeSet<>(resolved.provenance().appliedModifierModes());
        List<MonsterIdentity> allTemplates = new ArrayList<>(base.appliedTemplates());
        for (MonsterIdentity template : resolved.appliedTemplates()) {
            if (!allTemplates.contains(template)) {
                allTemplates.add(template);
            }
        }
        MonsterVersionProvenance versionProvenance = new MonsterVersionProvenance(base.identity(), versionIndex,
                implementationIndex, variables, base.copyParent(), rawVersion);
        ResolutionProvenance provenance = resolved.provenance().forGeneratedVersion(modes, versionProvenance);
        return new ResolvedMonster(identity, base.location(), base.identity(), allTemplates, finalContent, provenance);
    }

    private List<MonsterTemplateReferenceDte> readTemplateReferences(JsonNode refs, ResolvedMonster base,
                                                                     int versionIndex, Integer implementationIndex) {
        if (refs == null || refs.isNull()) {
            return null;
        }
        List<MonsterTemplateReferenceDte> result = new ArrayList<>();
        for (int i = 0; i < refs.size(); i++) {
            JsonNode ref = refs.get(i);
            if (!ref.path("name").isTextual() || !ref.path("source").isTextual()) {
                throw versionError(base, versionIndex, implementationIndex, "$._versions[" + versionIndex + "]._templates[" + i + "] requires name and source");
            }
            result.add(new MonsterTemplateReferenceDte(ref.path("name").asText(), ref.path("source").asText()));
        }
        return List.copyOf(result);
    }

    private void filterVersionSource(ObjectNode base, String property, String versionSource,
                                     ResolvedMonster source, int versionIndex, Integer implementationIndex) {
        JsonNode entries = base.get(property);
        if (entries == null || entries.isNull()) {
            return;
        }
        if (!entries.isArray()) {
            throw versionError(source, versionIndex, implementationIndex, "$." + property + " must be an array when present");
        }
        ArrayNode filtered = mapper.createArrayNode();
        for (JsonNode entry : entries) {
            // Upstream only removes object references whose source equals the generated version's source;
            // scalar and otherwise shaped metadata entries are retained unchanged.
            if (!entry.isObject() || !entry.path("source").asText().equals(versionSource)) {
                filtered.add(entry.deepCopy());
            }
        }
        if (filtered.isEmpty()) {
            base.remove(property);
        } else {
            base.set(property, filtered);
        }
    }

    private String requiredText(ResolvedMonster base, ObjectNode value, String key, int versionIndex, Integer implementationIndex) {
        JsonNode property = value.get(key);
        if (property == null || !property.isTextual() || property.asText().isBlank()) {
            throw versionError(base, versionIndex, implementationIndex, "$._versions[" + versionIndex + "]." + key + " must be a nonblank string");
        }
        return property.asText();
    }

    private MonsterDte emptyDte() {
        return new MonsterDte(null, null, null, null, null, null, null, null);
    }

    private MonsterResolutionException versionError(ResolvedMonster base, int versionIndex, Integer implementationIndex, String message) {
        return versionError(base, versionIndex, implementationIndex, message, null);
    }

    private MonsterResolutionException versionError(ResolvedMonster base, int versionIndex, Integer implementationIndex,
                                                    String message, Throwable cause) {
        String index = versionIndex < 0 ? "" : " version[" + versionIndex + "]";
        String implementation = implementationIndex == null ? "" : " implementation[" + implementationIndex + "]";
        String details = base.identity() + " at " + base.location() + index + implementation + ": " + message;
        return cause == null ? new MonsterResolutionException(details) : new MonsterResolutionException(details, cause);
    }
}
