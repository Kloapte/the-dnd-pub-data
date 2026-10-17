package com.thedndpub.data.dto.background;

import com.thedndpub.data.dto.feat.FeatDefinition;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.background.BackgroundAbilityChoiceDte;
import com.thedndpub.data.dte.background.BackgroundDte;
import com.thedndpub.data.dte.background.BackgroundEquipmentEntryDte;
import com.thedndpub.data.dte.background.BackgroundEquipmentPackageDte;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.services.FeatCatalog;
import com.thedndpub.data.services.ItemCatalog;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Component
public final class BackgroundMapper {
    private final ItemCatalog itemCatalog;
    private final FeatCatalog featCatalog;

    public BackgroundMapper(
            ItemCatalog itemCatalog,
            FeatCatalog featCatalog) {
        this.itemCatalog = itemCatalog;
        this.featCatalog = featCatalog;
    }

    public BackgroundDefinition map(
            BackgroundDte root,
            BackgroundDte effective,
            List<EntryDte> resolvedEntries,
            BackgroundId parentId) {
        if (root.name() == null || root.source() == null) {
            throw new IllegalArgumentException("Background name and source are required");
        }
        List<SourceReference> additionalSources = new ArrayList<>();
        addSources(additionalSources, effective.additionalSources());
        List<String> reprints = effective.reprintedAs() == null ? List.of() : effective.reprintedAs();
        BackgroundDte normalizedRecord = withEntries(effective, resolvedEntries);
        return new BackgroundDefinition(
                idFor(root.name(), root.source()),
                root.name(),
                new SourceReference(new SourceIdentity(root.source()), root.page()),
                effective.edition(),
                list(effective.ability()),
                list(effective.skillProficiencies()),
                list(effective.toolProficiencies()),
                list(effective.languageProficiencies()),
                list(effective.skillToolLanguageProficiencies()),
                list(effective.feats()),
                list(effective.startingEquipment()),
                list(resolvedEntries),
                normalizedRecord,
                additionalSources,
                mapSources(effective.otherSources()),
                reprints,
                root,
                parentId);
    }

    public BackgroundDto toDto(BackgroundDefinition definition) {
        return new BackgroundDto(
                definition.id().value(), definition.name(), definition.source(), definition.edition(),
                mapAbilityChoices(definition.ability()),
                definition.skillProficiencies(), definition.toolProficiencies(),
                definition.languageProficiencies(), definition.skillToolLanguageProficiencies(), definition.feats(),
                mapFeatReferences(definition.feats()), mapEquipment(definition.startingEquipment()),
                featureMetadata(definition.entries()), characteristicTables(definition.entries()),
                definition.effectiveRecord().additionalSpells() == null ? List.of()
                        : definition.effectiveRecord().additionalSpells().stream()
                        .map(value -> new BackgroundAdditionalSpellDto(value.expanded())).toList(),
                definition.effectiveRecord().prerequisite() == null ? List.of()
                        : definition.effectiveRecord().prerequisite().stream()
                        .map(value -> new BackgroundPrerequisiteDto(value.campaign())).toList(),
                definition.additionalSources(), definition.otherSources(), mapReprints(definition.reprintedAs()),
                List.of());
    }

    public BackgroundSummaryDto toSummary(BackgroundDefinition definition) {
        List<String> skills = definition.skillProficiencies().stream()
                .flatMap(group -> group.grants().entrySet().stream())
                .flatMap(grant -> grant.getKey().equals("choose") && grant.getValue().choice() != null
                        ? grant.getValue().choice().from().stream()
                        : java.util.stream.Stream.of(grant.getKey()))
                .filter(value -> !value.startsWith("any"))
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
        return new BackgroundSummaryDto(definition.id().value(), definition.name(), definition.source(),
                definition.edition(), skills, mapFeatReferences(definition.feats()),
                mapAbilityChoices(definition.ability()));
    }

    public static BackgroundId idFor(String name, String source) {
        return new BackgroundId("background:" + slug(source) + ":" + slug(name));
    }

    private List<BackgroundFeatReferenceDto> mapFeatReferences(List<BackgroundGrantGroupDte> groups) {
        List<BackgroundFeatReferenceDto> references = new ArrayList<>();
        for (BackgroundGrantGroupDte group : groups) {
            for (String uid : group.grants().keySet()) {
                if (uid.equals("anyFromCategory")) {
                    continue;
                }
                String[] parts = uid.split("\\|", -1);
                String[] selectionParts = parts[0].split(";", 2);
                String name = selectionParts[0].trim();
                String selection = selectionParts.length == 2 ? selectionParts[1].trim() : null;
                String source = parts.length > 1 ? parts[1].trim() : null;
                List<FeatDefinition> matches = featCatalog.findByName(name).stream()
                        .filter(feat -> source == null || feat.source().identity().code().equalsIgnoreCase(source))
                        .toList();
                String featId = matches.size() == 1 ? matches.getFirst().id().value() : null;
                String resolution = matches.size() == 1 ? "RESOLVED" : matches.isEmpty() ? "UNRESOLVED" : "AMBIGUOUS";
                references.add(new BackgroundFeatReferenceDto(uid, name, source, selection, featId, resolution));
            }
        }
        return references.stream().distinct()
                .sorted(Comparator.comparing(BackgroundFeatReferenceDto::uid, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private List<BackgroundEquipmentPackageDto> mapEquipment(List<BackgroundEquipmentPackageDte> packages) {
        List<BackgroundEquipmentPackageDto> mapped = new ArrayList<>();
        for (BackgroundEquipmentPackageDte equipmentPackage : packages) {
            Map<String, List<BackgroundEquipmentEntryDto>> options = new TreeMap<>();
            equipmentPackage.options().forEach((option, entries) -> options.put(option,
                    entries.stream().map(this::mapEquipmentEntry).toList()));
            mapped.add(new BackgroundEquipmentPackageDto(options));
        }
        return List.copyOf(mapped);
    }

    private BackgroundEquipmentEntryDto mapEquipmentEntry(BackgroundEquipmentEntryDte entry) {
        if (entry.item() != null) {
            return new BackgroundEquipmentEntryDto("ITEM", itemReference(entry.item()), null, null,
                    entry.displayName(), entry.quantity(), null, entry.containsValue(), entry.worthValue());
        }
        if (entry.value() != null) {
            return new BackgroundEquipmentEntryDto("CURRENCY", null, null, null,
                    entry.displayName(), entry.quantity(), entry.value(), null, null);
        }
        if (entry.equipmentType() != null) {
            return new BackgroundEquipmentEntryDto("EQUIPMENT_TYPE", null, null, entry.equipmentType(),
                    entry.displayName(), entry.quantity(), null, null, entry.worthValue());
        }
        return new BackgroundEquipmentEntryDto("TEXT", null, entry.special(), null,
                entry.displayName(), entry.quantity(), null, entry.containsValue(), entry.worthValue());
    }

    private BackgroundItemReferenceDto itemReference(String uid) {
        String[] parts = uid.split("\\|", -1);
        String name = parts[0].trim();
        String source = parts.length == 2 ? parts[1].trim() : null;
        List<com.thedndpub.data.dto.item.ItemDefinition> matches = itemCatalog.findByName(name).stream()
                .filter(item -> source == null || item.source().identity().code().equalsIgnoreCase(source))
                .toList();
        if (matches.size() == 1) {
            return new BackgroundItemReferenceDto(uid, name, source, matches.getFirst().id(),
                    BackgroundItemReferenceDto.Resolution.RESOLVED);
        }
        BackgroundItemReferenceDto.Resolution resolution = matches.isEmpty()
                ? BackgroundItemReferenceDto.Resolution.UNRESOLVED
                : BackgroundItemReferenceDto.Resolution.AMBIGUOUS;
        return new BackgroundItemReferenceDto(uid, name, source, null, resolution);
    }

    private static List<BackgroundAbilityChoiceDto> mapAbilityChoices(List<BackgroundAbilityChoiceDte> choices) {
        return choices.stream()
                .map(choice -> new BackgroundAbilityChoiceDto(
                        choice.choose().weighted().from(), choice.choose().weighted().weights()))
                .toList();
    }

    private static List<BackgroundFeatureMetadataDto> featureMetadata(List<EntryDte> entries) {
        List<BackgroundFeatureMetadataDto> features = new ArrayList<>();
        collectFeatures(entries, features);
        return features.stream().distinct()
                .sorted(Comparator.comparing(BackgroundFeatureMetadataDto::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private static void collectFeatures(List<EntryDte> entries, List<BackgroundFeatureMetadataDto> features) {
        for (EntryDte entry : entries) {
            if (entry instanceof NamedEntryDte named) {
                if (named.data() != null && Boolean.TRUE.equals(named.data().isFeature())) {
                    features.add(new BackgroundFeatureMetadataDto(named.name(), named.type()));
                }
                if (named.entries() != null) {
                    collectFeatures(named.entries(), features);
                }
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.ListEntryDte list) {
                collectFeatures(list.items(), features);
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.SectionEntryDte section
                    && section.entries() != null) {
                collectFeatures(section.entries(), features);
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.ListItemEntryDte item
                    && item.entries() != null) {
                collectFeatures(item.entries(), features);
            } else if (entry instanceof TableEntryDte) {
                // Table cells are characteristics and are not feature descriptions.
            }
        }
    }

    private static List<BackgroundCharacteristicTableDto> characteristicTables(List<EntryDte> entries) {
        List<BackgroundCharacteristicTableDto> tables = new ArrayList<>();
        collectTables(entries, tables);
        return List.copyOf(tables);
    }

    private static void collectTables(List<EntryDte> entries, List<BackgroundCharacteristicTableDto> tables) {
        for (EntryDte entry : entries) {
            if (entry instanceof TableEntryDte table) {
                tables.add(new BackgroundCharacteristicTableDto(table.caption(), table.colLabels(),
                        table.rows().size()));
            } else if (entry instanceof NamedEntryDte named && named.entries() != null) {
                collectTables(named.entries(), tables);
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.ListEntryDte list) {
                collectTables(list.items(), tables);
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.SectionEntryDte section
                    && section.entries() != null) {
                collectTables(section.entries(), tables);
            } else if (entry instanceof com.thedndpub.data.dte.common.entry.ListItemEntryDte item
                    && item.entries() != null) {
                collectTables(item.entries(), tables);
            }
        }
    }

    private static BackgroundDte withEntries(BackgroundDte source, List<EntryDte> entries) {
        return new BackgroundDte(source.name(), source.source(), source.page(), source.edition(), source.ability(),
                source.skillProficiencies(), source.toolProficiencies(), source.languageProficiencies(),
                source.feats(), source.startingEquipment(), source.skillToolLanguageProficiencies(),
                source.prerequisite(), source.additionalSpells(), entries, source.reprintedAs(),
                source.additionalSources(), source.otherSources(), source.fromFeature(), source.hasFluff(),
                source.hasFluffImages(), source.srd(), source.srd52(), source.basicRules(),
                source.basicRules2024(), source.copy());
    }

    private static void addSources(List<SourceReference> output,
                                   List<com.thedndpub.data.dte.background.BackgroundSourceReferenceDte> values) {
        if (values != null) {
            values.forEach(value -> output.add(new SourceReference(value.source(), value.page())));
        }
    }

    private static List<SourceReference> mapSources(
            List<com.thedndpub.data.dte.background.BackgroundSourceReferenceDte> values) {
        List<SourceReference> output = new ArrayList<>();
        addSources(output, values);
        return List.copyOf(output);
    }

    private static List<BackgroundReprintDto> mapReprints(List<String> references) {
        return references.stream().map(reference -> {
            String[] parts = reference.split("\\|", -1);
            return new BackgroundReprintDto(parts[0], parts[1]);
        }).toList();
    }

    private static <T> List<T> list(List<T> values) {
        return values == null ? List.of() : values;
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        return normalized;
    }
}
