package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.charoption.CharacterCreationOptionSummaryDto;
import com.thedndpub.data.dto.charoption.CharacterOptionDefinition;
import com.thedndpub.data.dto.charoption.CharacterOptionImportRecord;
import com.thedndpub.data.dto.charoption.CharacterOptionMapper;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.EntryTableCellDte;
import com.thedndpub.data.dte.common.entry.InsetEntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.QuoteEntryDte;
import com.thedndpub.data.dte.common.entry.RollTableCellDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.StructuredTableCellDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.common.entry.TextTableCellDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.charoption.CharacterCreationOptionDte;
import com.thedndpub.data.dte.charoption.CharacterCreationOptionsFileDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Immutable normalized Character Creation Options catalog. */
@Component
public final class CharacterCreationOptionCatalog {
    private static final String RESOURCE_PATH = "data/charcreationoptions.json";
    private static final Pattern SOURCE_BEARING_BOOK_OR_DECK_TAG =
            Pattern.compile("\\{@(?:book|deck)\\s+[^|}]+\\|([^|}]+)");

    private final Map<String, CharacterOptionDefinition> definitionsById;
    private final Map<String, List<CharacterOptionDefinition>> definitionsByName;
    private final Map<String, CharacterOptionImportRecord> importsById;
    private final List<CharacterCreationOptionSummaryDto> summaries;

    @Autowired
    public CharacterCreationOptionCatalog(
            ObjectMapper objectMapper,
            CharacterOptionMapper mapper,
            SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public CharacterCreationOptionCatalog(
            CharacterCreationOptionsFileDte source,
            CharacterOptionMapper mapper,
            SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.charoption() == null || source.charoption().isEmpty()) {
            throw new IllegalStateException("Character Creation Option source contains no definitions");
        }

        Map<String, CharacterOptionDefinition> idIndex = new TreeMap<>();
        Map<String, CharacterOptionDefinition> identityIndex = new TreeMap<>();
        Map<String, List<CharacterOptionDefinition>> nameIndex = new TreeMap<>();
        Map<String, CharacterOptionImportRecord> importIndex = new TreeMap<>();
        List<CharacterCreationOptionSummaryDto> summaryList = new ArrayList<>(source.charoption().size());

        for (CharacterCreationOptionDte option : source.charoption()) {
            if (option == null) {
                throw new IllegalStateException("Character Creation Option source contains a null record");
            }
            requireSource(sourceRegistry, option.source(), "primary source");
            CharacterOptionImportRecord imported = mapper.mapRecord(option);
            CharacterOptionDefinition definition = imported.definition();
            validateReferencedSources(sourceRegistry, definition);
            validateMarkupSources(sourceRegistry, imported.provenance().entries());

            String id = definition.id().value();
            putUnique(idIndex, id, definition, "Character Creation Option ID");
            String identity = identity(definition);
            putUnique(identityIndex, identity, definition, "Character Creation Option authored identity");
            nameIndex.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            importIndex.put(id, imported);
            summaryList.add(CharacterCreationOptionSummaryDto.from(definition));
        }

        summaryList.sort(summaryOrder());
        definitionsById = Collections.unmodifiableMap(idIndex);
        definitionsByName = immutableNameIndex(nameIndex);
        importsById = Collections.unmodifiableMap(importIndex);
        summaries = List.copyOf(summaryList);
    }

    public List<CharacterOptionDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }

    public List<CharacterCreationOptionSummaryDto> summaries() {
        return summaries;
    }

    public CharacterOptionDefinition findById(String id) {
        return definitionsById.get(id);
    }

    public List<CharacterOptionDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return definitionsByName.getOrDefault(normalize(name), List.of());
    }

    public CharacterOptionImportRecord importById(String id) {
        return importsById.get(id);
    }

    public int size() {
        return definitionsById.size();
    }

    private static CharacterCreationOptionsFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(CharacterCreationOptionsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Character Creation Option resource " + RESOURCE_PATH,
                    exception);
        }
    }

    private static void validateReferencedSources(SourceRegistry registry, CharacterOptionDefinition definition) {
        for (var source : definition.otherSources()) {
            requireSource(registry, source.identity().code(), "secondary source");
        }
        for (var reprint : definition.reprintedAs()) {
            requireSource(registry, reprint.source().code(), "reprint target source");
        }
    }

    private static void validateMarkupSources(SourceRegistry registry, List<EntryDte> entries) {
        for (EntryDte entry : entries) {
            for (String text : entryText(entry)) {
                Matcher matcher = SOURCE_BEARING_BOOK_OR_DECK_TAG.matcher(text);
                while (matcher.find()) {
                    requireSource(registry, matcher.group(1).trim(), "inline book/deck reference source");
                }
            }
        }
    }

    private static List<String> entryText(EntryDte entry) {
        List<String> values = new ArrayList<>();
        if (entry instanceof TextEntryDte text) {
            values.add(text.text());
        } else if (entry instanceof NamedEntryDte named) {
            addIfPresent(values, named.entry());
            addChildText(values, named.entries());
        } else if (entry instanceof ListEntryDte list) {
            addChildText(values, list.items());
        } else if (entry instanceof ListItemEntryDte item) {
            addIfPresent(values, item.entry());
            addChildText(values, item.entries());
        } else if (entry instanceof TableEntryDte table) {
            if (table.rows() != null) {
                table.rows().stream().flatMap(List::stream).forEach(cell -> values.addAll(cellText(cell)));
            }
            if (table.footnotes() != null) {
                values.addAll(table.footnotes());
            }
        } else if (entry instanceof QuoteEntryDte quote) {
            addChildText(values, quote.entries());
        } else if (entry instanceof InsetEntryDte inset) {
            addChildText(values, inset.entries());
        } else if (entry instanceof SectionEntryDte section) {
            addChildText(values, section.entries());
        }
        return values;
    }

    private static List<String> cellText(EntryTableCellDte cell) {
        List<String> values = new ArrayList<>();
        if (cell instanceof TextTableCellDte text) {
            values.add(text.text());
        } else if (cell instanceof RollTableCellDte roll) {
            addChildText(values, roll.entry() == null ? List.of() : List.of(roll.entry()));
        } else if (cell instanceof StructuredTableCellDte structured) {
            addChildText(values, structured.entries());
        }
        return values;
    }

    private static void addChildText(List<String> target, List<EntryDte> children) {
        if (children != null) {
            children.forEach(child -> target.addAll(entryText(child)));
        }
    }

    private static void addIfPresent(List<String> target, String value) {
        if (value != null) {
            target.add(value);
        }
    }

    private static void requireSource(SourceRegistry registry, String code, String role) {
        if (code == null || registry.find(code) == null) {
            throw new IllegalStateException("Character Creation Option " + role
                    + " is missing from Source Registry: " + code);
        }
    }

    private static String identity(CharacterOptionDefinition definition) {
        String optionTypes = definition.optionTypes().stream()
                .map(type -> normalize(type.code()))
                .sorted()
                .reduce((left, right) -> left + "," + right)
                .orElse("");
        return normalize(definition.source().identity().code()) + "\u0000"
                + optionTypes + "\u0000" + normalize(definition.name());
    }

    private static void putUnique(
            Map<String, CharacterOptionDefinition> index,
            String key,
            CharacterOptionDefinition value,
            String identityType) {
        if (index.putIfAbsent(key, value) != null) {
            throw new IllegalStateException("Duplicate " + identityType + ": " + key);
        }
    }

    private static Map<String, List<CharacterOptionDefinition>> immutableNameIndex(
            Map<String, List<CharacterOptionDefinition>> values) {
        Map<String, List<CharacterOptionDefinition>> copy = new TreeMap<>();
        values.forEach((key, definitions) -> copy.put(key, definitions.stream()
                .sorted(Comparator.comparing(
                                (CharacterOptionDefinition definition) -> definition.source().identity().code(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(definition -> definition.id().value()))
                .toList()));
        return Collections.unmodifiableMap(copy);
    }

    private static Comparator<CharacterCreationOptionSummaryDto> summaryOrder() {
        return Comparator.comparing(CharacterCreationOptionSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value());
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
