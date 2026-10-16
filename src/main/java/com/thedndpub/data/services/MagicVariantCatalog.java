package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.item.ItemVariantMatchData;
import com.thedndpub.data.dto.magicvariant.MagicVariantCatalogStatistics;
import com.thedndpub.data.dto.magicvariant.MagicVariantCopyResolver;
import com.thedndpub.data.dto.magicvariant.MagicVariantDto;
import com.thedndpub.data.dto.magicvariant.MagicVariantImportProvenance;
import com.thedndpub.data.dto.magicvariant.MagicVariantKind;
import com.thedndpub.data.dto.magicvariant.MagicVariantMapper;
import com.thedndpub.data.dto.magicvariant.MagicVariantMatcher;
import com.thedndpub.data.dto.magicvariant.MagicVariantSummaryDto;
import com.thedndpub.data.dto.magicvariant.MagicVariantTransformationDto;
import com.thedndpub.data.dto.magicvariant.ResolvedMagicVariantDte;
import com.thedndpub.data.dte.item.ItemSpellGrantDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantInheritanceDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantRequirementDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantsFileDte;
import com.thedndpub.data.dto.source.SourceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public final class MagicVariantCatalog {
    private static final String RESOURCE_PATH = "data/magicvariants.json";

    private final Map<String, MagicVariantDto> byId;
    private final Map<String, List<MagicVariantDto>> byName;
    private final List<MagicVariantSummaryDto> summaries;
    private final List<MagicVariantDto> definitions;
    private final Map<String, MagicVariantImportProvenance> importProvenance;
    private final Map<String, Map<String, List<String>>> linkedLootTables;
    private final MagicVariantCatalogStatistics statistics;

    @Autowired
    public MagicVariantCatalog(
            ObjectMapper objectMapper,
            SourceRegistry sourceRegistry,
            ItemCatalog itemCatalog,
            MagicVariantCopyResolver copyResolver,
            MagicVariantMapper mapper) {
        this(load(objectMapper, RESOURCE_PATH, MagicVariantsFileDte.class),
                sourceRegistry, itemCatalog, copyResolver, mapper);
    }

    public MagicVariantCatalog(
            MagicVariantsFileDte source,
            SourceRegistry sourceRegistry,
            ItemCatalog itemCatalog,
            MagicVariantCopyResolver copyResolver,
            MagicVariantMapper mapper) {
        if (source.magicvariant() == null || source.magicvariant().size() != 230) {
            throw new IllegalStateException("Magic variants resource must contain all 230 source records");
        }
        validateSources(source, sourceRegistry);
        List<ResolvedMagicVariantDte> resolved = copyResolver.resolve(source.magicvariant(), itemCatalog);

        Map<String, MagicVariantDto> idIndex = new TreeMap<>();
        Map<String, MagicVariantDto> identityIndex = new TreeMap<>();
        Map<String, List<MagicVariantDto>> nameIndex = new TreeMap<>();
        Map<String, MagicVariantImportProvenance> provenance = new TreeMap<>();
        List<MagicVariantSummaryDto> summaryList = new ArrayList<>();
        Map<String, List<MagicVariantDto>> eligibleByItem = new HashMap<>();
        Map<String, List<MagicVariantDto>> eligibleGenericByItem = new HashMap<>();
        long potentialGenericItemCombinations = 0;
        long potentialItemSpecificMatches = 0;
        int genericRulesWithoutEligibleItems = 0;
        int itemSpecificCount = 0;
        int genericCount = 0;
        int unresolvedBaseItemReferences = 0;
        Map<String, Integer> generatedIdentities = new HashMap<>();
        Map<String, Set<String>> generatedIdentityByItem = new HashMap<>();
        Set<String> existingItemIdentities = new HashSet<>();
        itemCatalog.variantMatchData().forEach(item -> existingItemIdentities.add(
                normalize(item.name()) + "\u0000" + normalize(item.source())));
        Set<String> generatedIdentitiesCollidingWithItems = new HashSet<>();
        long generatedItemCollisionCount = 0;

        for (ResolvedMagicVariantDte resolvedVariant : resolved) {
            MagicVariantDte record = resolvedVariant.source();
            List<ItemVariantMatchData> eligibleItems = eligibleItems(record, itemCatalog.variantMatchData());
            MagicVariantDto definition = mapper.map(resolvedVariant, eligibleItems.size());
            String sourceCode = definition.source().identity().code();
            String identity = normalize(definition.name()) + "\u0000" + normalize(sourceCode);
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate magic variant name/source identity: "
                        + definition.name() + "|" + sourceCode);
            }
            if (idIndex.putIfAbsent(definition.id(), definition) != null) {
                throw new IllegalStateException("Duplicate magic variant ID: " + definition.id());
            }
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(new MagicVariantSummaryDto(definition.id(), definition.name(), definition.source(),
                    definition.type(), definition.kind(), definition.eligibleItemCount()));
            provenance.put(definition.id(), new MagicVariantImportProvenance(
                    record, resolvedVariant, record.copy() != null));

            if (definition.kind() == MagicVariantKind.GENERIC_RULE) {
                genericCount++;
                potentialGenericItemCombinations += eligibleItems.size();
                if (eligibleItems.isEmpty()) {
                    genericRulesWithoutEligibleItems++;
                }
                for (ItemVariantMatchData item : eligibleItems) {
                    eligibleGenericByItem.computeIfAbsent(item.itemId(), ignored -> new ArrayList<>())
                            .add(definition);
                    eligibleByItem.computeIfAbsent(item.itemId(), ignored -> new ArrayList<>()).add(definition);
                    String generatedIdentity = generatedIdentity(definition, item);
                    generatedIdentities.merge(generatedIdentity, 1, Integer::sum);
                    generatedIdentityByItem.computeIfAbsent(item.itemId() + "\u0000" + generatedIdentity,
                            ignored -> new HashSet<>()).add(definition.id());
                    if (existingItemIdentities.contains(generatedIdentity)) {
                        generatedIdentitiesCollidingWithItems.add(generatedIdentity);
                        generatedItemCollisionCount++;
                    }
                }
            } else {
                itemSpecificCount++;
                potentialItemSpecificMatches += eligibleItems.size();
                if (!resolvedVariant.baseItemResolved()) {
                    unresolvedBaseItemReferences++;
                }
                for (ItemVariantMatchData item : eligibleItems) {
                    eligibleByItem.computeIfAbsent(item.itemId(), ignored -> new ArrayList<>()).add(definition);
                }
            }
        }

        summaryList.sort(Comparator.comparing(MagicVariantSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(MagicVariantSummaryDto::id));
        Map<String, List<MagicVariantDto>> immutableNames = new TreeMap<>();
        nameIndex.forEach((key, value) -> {
            value.sort(Comparator.comparing(MagicVariantDto::id));
            immutableNames.put(key, List.copyOf(value));
        });
        byId = Collections.unmodifiableMap(idIndex);
        byName = Collections.unmodifiableMap(immutableNames);
        summaries = List.copyOf(summaryList);
        definitions = summaryList.stream().map(summary -> idIndex.get(summary.id())).toList();
        importProvenance = Collections.unmodifiableMap(provenance);
        linkedLootTables = immutableLootTables(source.linkedLootTables());
        statistics = new MagicVariantCatalogStatistics(
                resolved.size(), genericCount, itemSpecificCount,
                potentialGenericItemCombinations, potentialItemSpecificMatches,
                duplicateIdentityGroups(generatedIdentities), duplicateIdentityOccurrences(generatedIdentities),
                generatedIdentitiesCollidingWithItems.size(), generatedItemCollisionCount,
                overlapPairs(eligibleGenericByItem), conflictPairs(generatedIdentityByItem),
                genericRulesWithoutEligibleItems, unresolvedBaseItemReferences);
    }

    public int size() {
        return byId.size();
    }

    public List<MagicVariantSummaryDto> summaries() {
        return summaries;
    }

    public List<MagicVariantDto> definitions() {
        return definitions;
    }

    public MagicVariantDto findById(String id) {
        return byId.get(id);
    }

    public List<MagicVariantDto> findByName(String name) {
        return byName.getOrDefault(normalize(name), List.of());
    }

    public List<MagicVariantDto> find(String name, String source, String type, MagicVariantKind kind) {
        String normalizedName = normalizeFilter(name);
        String normalizedSource = normalizeFilter(source);
        String normalizedType = normalizeFilter(type);
        return summaries.stream()
                .filter(summary -> normalizedName == null
                        || summary.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(summary -> normalizedSource == null
                        || summary.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .filter(summary -> normalizedType == null || summary.type() != null
                        && summary.type().toLowerCase(Locale.ROOT).equals(normalizedType))
                .filter(summary -> kind == null || summary.kind() == kind)
                .map(summary -> byId.get(summary.id()))
                .toList();
    }

    public List<MagicVariantDto> findByNameAndSource(String name, String source) {
        String normalizedSource = normalizeFilter(source);
        return findByName(name).stream()
                .filter(variant -> normalizedSource == null
                        || variant.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .toList();
    }

    public MagicVariantCatalogStatistics statistics() {
        return statistics;
    }

    public MagicVariantImportProvenance findImportProvenance(String id) {
        return importProvenance.get(id);
    }

    public int linkedLootTableEntryCount() {
        return linkedLootTables.values().stream().mapToInt(Map::size).sum();
    }

    private static List<ItemVariantMatchData> eligibleItems(
            MagicVariantDte variant,
            List<ItemVariantMatchData> items) {
        if (variant.requires() == null || variant.requires().isEmpty()) {
            return List.of();
        }
        return items.stream()
                .filter(item -> MagicVariantMatcher.matchesAnyRequirement(variant.requires(), item))
                .filter(item -> !MagicVariantMatcher.isExcluded(variant.excludes(), item))
                .toList();
    }

    private static String generatedIdentity(MagicVariantDto variant, ItemVariantMatchData item) {
        MagicVariantTransformationDto transform = variant.transformation();
        String name = item.name();
        if (transform != null && transform.nameRemove() != null && !transform.nameRemove().isEmpty()) {
            name = name.replace(transform.nameRemove(), "");
        }
        if (transform != null && transform.namePrefix() != null) {
            name = transform.namePrefix() + name;
        }
        if (transform != null && transform.nameSuffix() != null) {
            name += transform.nameSuffix();
        }
        String source = transform == null || transform.outputSource() == null
                ? item.source()
                : transform.outputSource();
        return normalize(name) + "\u0000" + normalize(source);
    }

    private static int duplicateIdentityGroups(Map<String, Integer> counts) {
        return (int) counts.values().stream().filter(count -> count > 1).count();
    }

    private static long overlapPairs(Map<String, List<MagicVariantDto>> variantsByItem) {
        return variantsByItem.values().stream()
                .mapToLong(variants -> (long) variants.size() * (variants.size() - 1) / 2)
                .sum();
    }

    private static long conflictPairs(Map<String, Set<String>> rulesByItemAndGeneratedIdentity) {
        return rulesByItemAndGeneratedIdentity.values().stream()
                .mapToLong(ruleIds -> (long) ruleIds.size() * (ruleIds.size() - 1) / 2)
                .sum();
    }

    private static long duplicateIdentityOccurrences(Map<String, Integer> counts) {
        return counts.values().stream().filter(count -> count > 1).mapToLong(count -> count - 1L).sum();
    }

    private static void validateSources(MagicVariantsFileDte source, SourceRegistry registry) {
        for (MagicVariantDte variant : source.magicvariant()) {
            validateSource(MagicVariantCopyResolver.sourceCode(variant), registry,
                    "magic variant '" + variant.name() + "'");
            validateQualifiedCode(variant.type(), registry, "type of '" + variant.name() + "'");
            validateSources(variant.referenceSources(), registry, "references of '" + variant.name() + "'");
            validateRequirements(variant.requires(), registry, variant.name());
            validateRequirement(variant.excludes(), registry, variant.name());
            validateInheritance(variant.inherits(), registry, variant.name());
            if (variant.copy() != null) {
                validateSource(variant.copy().source(), registry, "copy target of '" + variant.name() + "'");
            }
        }
        if (source.linkedLootTables() != null) {
            source.linkedLootTables().keySet().forEach(code -> validateSource(code, registry, "linked loot tables"));
        }
    }

    private static void validateRequirements(
            List<MagicVariantRequirementDte> requirements,
            SourceRegistry registry,
            String variantName) {
        if (requirements != null) {
            requirements.forEach(requirement -> validateRequirement(requirement, registry, variantName));
        }
    }

    private static void validateRequirement(
            MagicVariantRequirementDte requirement,
            SourceRegistry registry,
            String variantName) {
        if (requirement == null) {
            return;
        }
        validateSource(requirement.source(), registry, "applicability rule of '" + variantName + "'");
        validateQualifiedCode(requirement.type(), registry, "required type of '" + variantName + "'");
        validateQualifiedCodes(requirement.property() == null ? null : requirement.property().values(),
                registry, "required property of '" + variantName + "'");
    }

    private static void validateInheritance(
            MagicVariantInheritanceDte inherits,
            SourceRegistry registry,
            String variantName) {
        if (inherits == null) {
            return;
        }
        validateSource(inherits.source(), registry, "output source of '" + variantName + "'");
        if (inherits.otherSources() != null) {
            inherits.otherSources().forEach(reference -> validateSource(
                    reference.source(), registry, "secondary source of '" + variantName + "'"));
        }
        validateQualifiedCodes(inherits.reprintedAs(), registry, "reprint target of '" + variantName + "'");
        validateSpellSources(inherits.attachedSpells(), registry, variantName);
    }

    private static void validateSpellSources(
            com.thedndpub.data.dte.item.ItemAttachedSpellsDte spells,
            SourceRegistry registry,
            String variantName) {
        if (spells == null) {
            return;
        }
        List<String> references = new ArrayList<>();
        addAll(references, spells.direct());
        addAll(references, spells.other());
        addAll(references, spells.ritual());
        addSpellGrant(references, spells.will());
        addSpellGrant(references, spells.daily());
        addSpellGrant(references, spells.rest());
        addSpellGrant(references, spells.charges());
        addSpellGrant(references, spells.limited());
        validateQualifiedCodes(references, registry, "spell reference of '" + variantName + "'");
    }

    private static void addSpellGrant(List<String> references, ItemSpellGrantDte grant) {
        if (grant != null) {
            addAll(references, grant.direct());
            if (grant.byUses() != null) {
                grant.byUses().values().forEach(values -> addAll(references, values));
            }
        }
    }

    private static void addAll(List<String> target, List<String> values) {
        if (values != null) {
            target.addAll(values);
        }
    }

    private static void validateSources(List<String> sources, SourceRegistry registry, String context) {
        if (sources != null) {
            sources.forEach(code -> validateSource(code, registry, context));
        }
    }

    private static void validateQualifiedCodes(List<String> values, SourceRegistry registry, String context) {
        if (values != null) {
            values.forEach(value -> validateQualifiedCode(value, registry, context));
        }
    }

    private static void validateQualifiedCode(String value, SourceRegistry registry, String context) {
        if (value == null) {
            return;
        }
        int separator = value.lastIndexOf('|');
        if (separator >= 0 && separator < value.length() - 1) {
            validateSource(value.substring(separator + 1), registry, context);
        }
    }

    private static void validateSource(String source, SourceRegistry registry, String context) {
        if (source != null && registry.find(source) == null) {
            throw new IllegalStateException("Unknown source code '" + source + "' in " + context);
        }
    }

    private static Map<String, Map<String, List<String>>> immutableLootTables(
            Map<String, Map<String, List<String>>> source) {
        if (source == null) {
            return Map.of();
        }
        Map<String, Map<String, List<String>>> outer = new TreeMap<>();
        source.forEach((book, items) -> {
            Map<String, List<String>> itemTables = new TreeMap<>();
            items.forEach((name, tables) -> itemTables.put(name, List.copyOf(tables)));
            outer.put(book, Collections.unmodifiableMap(itemTables));
        });
        return Collections.unmodifiableMap(outer);
    }

    private static String normalizeFilter(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private static <T> T load(ObjectMapper objectMapper, String resourcePath, Class<T> type) {
        ObjectMapper strictMapper = objectMapper.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        try (InputStream input = new ClassPathResource(resourcePath).getInputStream()) {
            return strictMapper.readValue(input, type);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load magic variant resource '" + resourcePath + "'", exception);
        }
    }
}
