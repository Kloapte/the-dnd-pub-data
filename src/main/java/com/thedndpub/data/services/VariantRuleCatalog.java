package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.variantrule.VariantRuleDefinition;
import com.thedndpub.data.dto.variantrule.VariantRuleMapper;
import com.thedndpub.data.dto.variantrule.VariantRuleSummaryDto;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.InlineEntryDte;
import com.thedndpub.data.dte.common.entry.InsetEntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.QuoteEntryDte;
import com.thedndpub.data.dte.common.entry.RewardInlineStatblockEntryDte;
import com.thedndpub.data.dte.common.entry.RollTableCellDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.StructuredTableCellDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dto.variantrule.VariantRuleId;
import com.thedndpub.data.dte.variantrule.VariantRuleDte;
import com.thedndpub.data.dte.variantrule.VariantRuleReprintTokenDte;
import com.thedndpub.data.dte.variantrule.VariantRuleReprintUidDte;
import com.thedndpub.data.dte.variantrule.VariantRulesFileDte;
import com.thedndpub.data.references.ReferenceUidParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

@Component
public final class VariantRuleCatalog {
    private static final String RESOURCE_PATH = "data/variantrules.json";
    private static final Set<String> RULE_TYPES = Set.of("C", "O", "V", "VO");
    private static final Set<String> ROOT_ENTRY_TYPES = Set.of("entries", "section");

    private final Map<String, VariantRuleDefinition> definitionsById;
    private final Map<String, List<VariantRuleDefinition>> definitionsByName;
    private final Map<String, List<VariantRuleDefinition>> definitionsBySource;
    private final List<VariantRuleSummaryDto> summaries;

    @Autowired
    public VariantRuleCatalog(
            ObjectMapper objectMapper,
            VariantRuleMapper mapper,
            SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public VariantRuleCatalog(
            VariantRulesFileDte source,
            VariantRuleMapper mapper,
            SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.variantrule().isEmpty()) {
            throw new IllegalStateException("Variant Rule source contains no definitions");
        }

        Map<String, VariantRuleDefinition> idIndex = new TreeMap<>();
        Map<String, VariantRuleDefinition> identityIndex = new TreeMap<>();
        Map<String, List<VariantRuleDefinition>> nameIndex = new TreeMap<>();
        Map<String, List<VariantRuleDefinition>> sourceIndex = new TreeMap<>();
        List<VariantRuleSummaryDto> summaryValues = new ArrayList<>(source.variantrule().size());

        for (VariantRuleDte authored : source.variantrule()) {
            validateAuthoredRecord(authored, sourceRegistry);
            VariantRuleDefinition definition = mapper.map(authored);
            String id = definition.id().value();
            String nameKey = normalize(definition.name());
            String sourceKey = normalize(definition.source().identity().code());
            String identity = nameKey + "\u0000" + sourceKey;

            if (idIndex.putIfAbsent(id, definition) != null) {
                throw new IllegalStateException("Duplicate Variant Rule ID: " + id);
            }
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate Variant Rule name/source identity: " + identity);
            }
            nameIndex.computeIfAbsent(nameKey, ignored -> new ArrayList<>()).add(definition);
            sourceIndex.computeIfAbsent(sourceKey, ignored -> new ArrayList<>()).add(definition);
            summaryValues.add(VariantRuleSummaryDto.from(definition));
        }

        summaryValues.sort(Comparator.comparing(VariantRuleSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value()));
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableDefinitionLists(nameIndex);
        definitionsBySource = immutableDefinitionLists(sourceIndex);
        summaries = List.copyOf(summaryValues);
    }

    public List<VariantRuleDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public List<VariantRuleSummaryDto> summaries() {
        return summaries;
    }

    public VariantRuleDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<VariantRuleDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public List<VariantRuleDefinition> findBySource(String source) {
        if (source == null || source.isBlank()) {
            return List.of();
        }
        return definitionsBySource.getOrDefault(normalize(source), List.of());
    }

    public int size() {
        return definitionsById.size();
    }

    private static VariantRulesFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(VariantRulesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Variant Rules resource " + RESOURCE_PATH, exception);
        }
    }

    private static void validateAuthoredRecord(VariantRuleDte rule, SourceRegistry registry) {
        if (rule == null || rule.name() == null || rule.name().isBlank()
                || rule.source() == null || rule.source().isBlank() || rule.page() == null) {
            throw new IllegalStateException("Variant Rule requires authored name, source, and page");
        }
        requireSource(registry, rule.source());
        if (rule.ruleType() != null && !RULE_TYPES.contains(rule.ruleType())) {
            throw new IllegalStateException("Unknown authored Variant Rule classification: " + rule.ruleType());
        }
        if (rule.type() != null && !ROOT_ENTRY_TYPES.contains(rule.type())) {
            throw new IllegalStateException("Unknown root Variant Rule entry type: " + rule.type());
        }
        if (rule.additionalSources() != null) {
            rule.additionalSources().forEach(reference -> requireSource(registry, reference.source()));
        }
        if (rule.reprintedAs() != null) {
            rule.reprintedAs().forEach(reference -> validateReprintSource(reference, registry));
        }
        validateEntrySources(rule.entries(), registry);
    }

    private static void validateReprintSource(
            com.thedndpub.data.dte.variantrule.VariantRuleReprintDte reference,
            SourceRegistry registry) {
        String token = reference instanceof VariantRuleReprintTokenDte text
                ? text.token()
                : ((VariantRuleReprintUidDte) reference).uid();
        String source = ReferenceUidParser.parse(token).source();
        if (source != null) {
            requireSource(registry, source);
        }
    }

    private static void validateEntrySources(List<EntryDte> entries, SourceRegistry registry) {
        if (entries == null) {
            return;
        }
        for (EntryDte entry : entries) {
            if (entry instanceof NamedEntryDte named) {
                requireOptionalSource(registry, named.source());
                validateEntrySources(named.entries(), registry);
            } else if (entry instanceof ListEntryDte list) {
                validateEntrySources(list.items(), registry);
            } else if (entry instanceof ListItemEntryDte item) {
                validateEntrySources(item.entries(), registry);
            } else if (entry instanceof TableEntryDte table) {
                requireOptionalSource(registry, table.source());
                if (table.rows() != null) {
                    for (var row : table.rows()) {
                        for (var cell : row) {
                            if (cell instanceof StructuredTableCellDte structured) {
                                validateEntrySources(structured.entries(), registry);
                            } else if (cell instanceof RollTableCellDte roll && roll.entry() != null) {
                                validateEntrySources(List.of(roll.entry()), registry);
                            }
                        }
                    }
                }
            } else if (entry instanceof QuoteEntryDte quote) {
                validateEntrySources(quote.entries(), registry);
            } else if (entry instanceof InsetEntryDte inset) {
                requireOptionalSource(registry, inset.source());
                validateEntrySources(inset.entries(), registry);
            } else if (entry instanceof SectionEntryDte section) {
                validateEntrySources(section.entries(), registry);
            } else if (entry instanceof InlineEntryDte inline) {
                validateEntrySources(inline.entries(), registry);
            } else if (entry instanceof RewardInlineStatblockEntryDte statblock
                    && statblock.statblockData() != null) {
                requireOptionalSource(registry, statblock.statblockData().source());
            }
        }
    }

    private static void requireOptionalSource(SourceRegistry registry, String source) {
        if (source != null) {
            requireSource(registry, source);
        }
    }

    private static void requireSource(SourceRegistry registry, String source) {
        if (source == null || registry.find(source) == null) {
            throw new IllegalStateException("Variant Rule refers to an unknown authored source identity: " + source);
        }
    }

    private static Map<String, List<VariantRuleDefinition>> immutableDefinitionLists(
            Map<String, List<VariantRuleDefinition>> values) {
        Map<String, List<VariantRuleDefinition>> result = new TreeMap<>();
        values.forEach((key, definitions) -> result.put(key, definitions.stream()
                .sorted(Comparator.comparing(
                                (VariantRuleDefinition value) -> value.source().identity().code(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(value -> value.id().value()))
                .toList()));
        return Collections.unmodifiableMap(result);
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
