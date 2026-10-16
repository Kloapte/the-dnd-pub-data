package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.background.BackgroundCopyResolver;
import com.thedndpub.data.dto.background.BackgroundDefinition;
import com.thedndpub.data.dto.background.BackgroundId;
import com.thedndpub.data.dto.background.BackgroundImportProvenance;
import com.thedndpub.data.dto.background.BackgroundMapper;
import com.thedndpub.data.dto.background.BackgroundSummaryDto;
import com.thedndpub.data.dto.background.ResolvedBackgroundDte;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.InsetEntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.QuoteEntryDte;
import com.thedndpub.data.dte.common.entry.RollTableCellDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.StructuredTableCellDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.background.BackgroundDte;
import com.thedndpub.data.dte.background.BackgroundEquipmentEntryDte;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import com.thedndpub.data.dte.background.BackgroundsFileDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Component
public final class BackgroundCatalog {
    private static final String RESOURCE_PATH = "data/backgrounds.json";

    private final Map<String, BackgroundDefinition> byId;
    private final Map<String, List<BackgroundDefinition>> byName;
    private final Map<String, List<BackgroundDefinition>> bySource;
    private final Map<String, BackgroundImportProvenance> provenanceById;
    private final List<BackgroundSummaryDto> summaries;
    private final int rootCount;
    private final int copyCount;
    private final ReferenceStatistics referenceStatistics;

    @Autowired
    public BackgroundCatalog(
            ObjectMapper objectMapper,
            BackgroundMapper mapper,
            SourceRegistry sourceRegistry) {
        this(load(objectMapper), mapper, sourceRegistry);
    }

    public BackgroundCatalog(BackgroundsFileDte source, BackgroundMapper mapper, SourceRegistry sourceRegistry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(sourceRegistry, "sourceRegistry");
        if (source.background() == null || source.background().isEmpty()) {
            throw new IllegalStateException("Background source contains no definitions");
        }

        List<ResolvedBackgroundDte> expanded = new BackgroundCopyResolver().resolve(source);
        Map<String, BackgroundDefinition> idIndex = new TreeMap<>();
        Map<String, BackgroundDefinition> identityIndex = new TreeMap<>();
        Map<String, List<BackgroundDefinition>> nameIndex = new TreeMap<>();
        Map<String, List<BackgroundDefinition>> sourceIndex = new TreeMap<>();
        Map<String, BackgroundImportProvenance> provenanceIndex = new TreeMap<>();
        List<BackgroundSummaryDto> summaryList = new ArrayList<>();
        long resolvedItems = 0;
        long unresolvedItems = 0;
        long ambiguousItems = 0;
        long textEquipment = 0;
        int copies = 0;

        for (ResolvedBackgroundDte resolved : expanded) {
            BackgroundDte root = resolved.root();
            validateSourceReferences(root, sourceRegistry);
            resolved.entries().forEach(entry -> validateEntrySources(entry, sourceRegistry));
            BackgroundId parentId = resolved.copyLineage().isEmpty() ? null : resolved.copyLineage().getFirst();
            BackgroundDefinition definition = mapper.map(root, resolved.effective(), resolved.entries(), parentId);
            validateCrossReferences(definition, sourceRegistry);
            String normalizedName = normalize(definition.name());
            String normalizedSource = normalize(definition.source().identity().code());
            if (idIndex.putIfAbsent(definition.id().value(), definition) != null) {
                throw new IllegalStateException("Duplicate background ID: " + definition.id().value());
            }
            String identity = normalizedName + "\u0000" + normalizedSource;
            if (identityIndex.putIfAbsent(identity, definition) != null) {
                throw new IllegalStateException("Duplicate background name/source identity: " + identity);
            }
            if (parentId != null) {
                copies++;
            }
            nameIndex.computeIfAbsent(normalizedName, ignored -> new ArrayList<>()).add(definition);
            sourceIndex.computeIfAbsent(normalizedSource, ignored -> new ArrayList<>()).add(definition);
            provenanceIndex.put(definition.id().value(),
                    new BackgroundImportProvenance(root, resolved.copyLineage()));
            summaryList.add(mapper.toSummary(definition));
            for (var equipmentPackage : definition.startingEquipment()) {
                for (var option : equipmentPackage.options().values()) {
                    for (BackgroundEquipmentEntryDte entry : option) {
                        if (entry.item() != null) {
                            var mapped = mapper.toDto(definition).startingEquipment().stream()
                                    .flatMap(pkg -> pkg.options().values().stream()).flatMap(List::stream)
                                    .filter(dto -> dto.item() != null && dto.item().uid().equals(entry.item()))
                                    .findFirst().orElseThrow();
                            switch (mapped.item().resolution()) {
                                case RESOLVED -> resolvedItems++;
                                case UNRESOLVED -> unresolvedItems++;
                                case AMBIGUOUS -> ambiguousItems++;
                            }
                        } else if (entry.special() != null || entry.equipmentType() != null) {
                            textEquipment++;
                        }
                    }
                }
            }
        }

        byId = Collections.unmodifiableMap(idIndex);
        byName = immutableMultiMap(nameIndex);
        bySource = immutableMultiMap(sourceIndex);
        provenanceById = Collections.unmodifiableMap(provenanceIndex);
        summaries = summaryList.stream().sorted(BackgroundCatalog::compareSummaries).toList();
        rootCount = source.background().size();
        copyCount = copies;
        referenceStatistics = new ReferenceStatistics(resolvedItems, unresolvedItems, ambiguousItems, textEquipment);
    }

    public int size() {
        return byId.size();
    }

    public int rootCount() {
        return rootCount;
    }

    public int copyCount() {
        return copyCount;
    }

    public ReferenceStatistics referenceStatistics() {
        return referenceStatistics;
    }

    public List<BackgroundDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<BackgroundSummaryDto> summaries() {
        return summaries;
    }

    public BackgroundDefinition findById(String id) {
        return byId.get(id);
    }

    public List<BackgroundDefinition> findByName(String name) {
        return byName.getOrDefault(normalize(name), List.of());
    }

    public List<BackgroundDefinition> findBySource(String source) {
        return bySource.getOrDefault(normalize(source), List.of());
    }

    public BackgroundImportProvenance findImportProvenance(String id) {
        return provenanceById.get(id);
    }

    private static void validateSourceReferences(BackgroundDte background, SourceRegistry registry) {
        requireSource(registry, background.source());
        if (background.copy() != null) {
            requireSource(registry, background.copy().source());
        }
        if (background.additionalSources() != null) {
            background.additionalSources().forEach(reference -> requireSource(registry, reference.source()));
        }
        if (background.otherSources() != null) {
            background.otherSources().forEach(reference -> requireSource(registry, reference.source()));
        }
        if (background.reprintedAs() != null) {
            background.reprintedAs().forEach(reference -> requireUidSource(registry, reference));
        }
        if (background.additionalSpells() != null) {
            background.additionalSpells().stream()
                    .flatMap(spell -> spell.expanded().values().stream())
                    .flatMap(List::stream)
                    .filter(spell -> spell.contains("|"))
                    .forEach(spell -> requireSpellSource(registry, spell));
        }
        if (background.entries() != null) {
            background.entries().forEach(entry -> validateEntrySources(entry, registry));
        }
    }

    private static void validateEntrySources(EntryDte entry, SourceRegistry registry) {
        if (entry instanceof NamedEntryDte named) {
            validateEntrySource(named.source(), registry);
            validateChildren(named.entries(), registry);
        } else if (entry instanceof InsetEntryDte inset) {
            validateEntrySource(inset.source(), registry);
            validateChildren(inset.entries(), registry);
        } else if (entry instanceof ListEntryDte list) {
            validateChildren(list.items(), registry);
        } else if (entry instanceof ListItemEntryDte item) {
            validateChildren(item.entries(), registry);
        } else if (entry instanceof QuoteEntryDte quote) {
            validateChildren(quote.entries(), registry);
        } else if (entry instanceof SectionEntryDte section) {
            validateChildren(section.entries(), registry);
        } else if (entry instanceof TableEntryDte table) {
            validateEntrySource(table.source(), registry);
            if (table.rows() != null) {
                table.rows().stream().flatMap(List::stream).forEach(cell -> {
                    if (cell instanceof RollTableCellDte roll && roll.entry() != null) {
                        validateEntrySources(roll.entry(), registry);
                    } else if (cell instanceof StructuredTableCellDte structured) {
                        validateChildren(structured.entries(), registry);
                    }
                });
            }
        }
    }

    private static void validateChildren(List<EntryDte> entries, SourceRegistry registry) {
        if (entries != null) {
            entries.forEach(entry -> validateEntrySources(entry, registry));
        }
    }

    private static void validateEntrySource(String source, SourceRegistry registry) {
        if (source != null) {
            requireSource(registry, source);
        }
    }

    private static void validateCrossReferences(BackgroundDefinition definition, SourceRegistry registry) {
        for (BackgroundGrantGroupDte group : definition.feats()) {
            group.grants().keySet().forEach(uid -> requireOptionalUidSource(registry, uid));
        }
        for (BackgroundGrantGroupDte group : definition.skillProficiencies()) {
            group.grants().keySet().forEach(uid -> requireOptionalUidSource(registry, uid));
        }
        for (BackgroundGrantGroupDte group : definition.languageProficiencies()) {
            group.grants().keySet().forEach(uid -> requireOptionalUidSource(registry, uid));
        }
        for (var equipmentPackage : definition.startingEquipment()) {
            equipmentPackage.options().values().stream().flatMap(List::stream)
                    .filter(entry -> entry.item() != null)
                    .forEach(entry -> requireOptionalUidSource(registry, entry.item()));
        }
    }

    private static void requireOptionalUidSource(SourceRegistry registry, String uid) {
        if (uid != null && uid.contains("|")) {
            requireUidSource(registry, uid);
        }
    }

    private static void requireSpellSource(SourceRegistry registry, String uid) {
        String[] parts = uid.split("\\|", -1);
        if (parts.length != 2) {
            throw new IllegalStateException("Invalid source-qualified background spell reference: " + uid);
        }
        String code = parts[1].split("#", 2)[0];
        requireSource(registry, code);
    }

    private static void requireUidSource(SourceRegistry registry, String uid) {
        String[] parts = uid.split("\\|", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalStateException("Invalid source-qualified background reference: " + uid);
        }
        requireSource(registry, parts[1]);
    }

    private static void requireSource(SourceRegistry registry, String source) {
        if (source == null || registry.find(source) == null) {
            throw new IllegalStateException("Background source code is missing from Source Registry: " + source);
        }
    }

    private static BackgroundsFileDte load(ObjectMapper objectMapper) {
        try (InputStream input = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            return objectMapper.readerFor(BackgroundsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load background resource " + RESOURCE_PATH, exception);
        }
    }

    private static Map<String, List<BackgroundDefinition>> immutableMultiMap(
            Map<String, List<BackgroundDefinition>> index) {
        Map<String, List<BackgroundDefinition>> immutable = new TreeMap<>();
        index.forEach((key, values) -> immutable.put(key, List.copyOf(values)));
        return Collections.unmodifiableMap(immutable);
    }

    private static int compareSummaries(BackgroundSummaryDto left, BackgroundSummaryDto right) {
        int name = String.CASE_INSENSITIVE_ORDER.compare(left.name(), right.name());
        return name != 0 ? name : String.CASE_INSENSITIVE_ORDER.compare(
                left.source().identity().code(), right.source().identity().code());
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT);
    }

    public record ReferenceStatistics(long resolvedItems, long unresolvedItems, long ambiguousItems, long textEquipment) {
    }
}
