package com.thedndpub.data.dto.classes;

import com.thedndpub.data.dto.source.SourceReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Explicit safe projection from internal normalized definitions to the public API. */
@Component
public final class ClassPublicMapper {
    public ClassSummaryDto summary(ClassDefinition value) {
        return new ClassSummaryDto(value.id().value(), value.name(), value.source(), page(value.source()),
                value.hitDice(), value.primaryAbilities(),
                value.spellcasting() == null ? null : value.spellcasting().ability(), sources(value.sources()));
    }

    public ClassDto detail(ClassDefinition value) {
        return new ClassDto(value.id().value(), value.name(), value.source(), value.declaredEdition(),
                value.sidekick(), value.hitDice(), value.savingThrows(), value.primaryAbilities(),
                safeStarting(value.startingOptions()), safeMulticlass(value.multiclassing()),
                value.features().stream().map(ClassPublicMapper::featureReference).toList(),
                value.subclasses().stream().map(SubclassId::value).toList(), safeTables(value.tables()),
                value.choices(), safeSpellcasting(value.spellcasting()), sources(value.sources()), List.of());
    }

    public SubclassSummaryDto summary(SubclassDefinition value) {
        return new SubclassSummaryDto(value.id().value(), value.name(), value.shortName(), value.source(),
                value.parentClassId().value(), sources(value.sources()));
    }

    public SubclassDto detail(SubclassDefinition value) {
        return new SubclassDto(value.id().value(), value.name(), value.shortName(), value.source(),
                value.parentClassId().value(), value.declaredEdition(),
                value.features().stream().map(ClassPublicMapper::featureReference).toList(),
                safeTables(value.tables()), value.choices(),
                safeSpellcasting(value.spellcasting()), sources(value.sources()), List.of());
    }

    public ClassFeatureSummaryDto summary(ClassFeatureDefinition value, Integer order) {
        return new ClassFeatureSummaryDto(value.id().value(), value.name(), value.source(),
                value.parentClassId().value(), value.level(), order, value.variant());
    }

    public SubclassFeatureSummaryDto summary(SubclassFeatureDefinition value, Integer order) {
        return new SubclassFeatureSummaryDto(value.id().value(), value.name(), value.source(),
                value.parentClassId().value(), value.parentSubclassId().value(), value.level(), order, value.variant());
    }

    public ClassFeatureDto detail(ClassFeatureDefinition value, Integer order) {
        return new ClassFeatureDto(value.id().value(), value.name(), value.source(), value.parentClassId().value(),
                value.level(), order, value.variant(), mechanics(value), sources(value.sources()), List.of());
    }

    public SubclassFeatureDto detail(SubclassFeatureDefinition value, Integer order) {
        return new SubclassFeatureDto(value.id().value(), value.name(), value.source(), value.parentClassId().value(),
                value.parentSubclassId().value(), value.level(), order, value.variant(), mechanics(value),
                sources(value.sources()), List.of());
    }

    private static ClassFeatureMechanicsDto mechanics(ClassFeatureDefinition value) {
        return mechanics(value.content(), value.consumes());
    }

    private static ClassFeatureMechanicsDto mechanics(SubclassFeatureDefinition value) {
        return mechanics(value.content(), value.consumes());
    }

    private static ClassFeatureMechanicsDto mechanics(List<ClassFeatureNode> nodes, ClassFeatureConsumption consume) {
        List<String> refs = new ArrayList<>();
        List<String> optional = new ArrayList<>();
        List<String> feats = new ArrayList<>();
        List<ClassFeatureChoiceDto> choices = new ArrayList<>();
        collect(nodes, refs, optional, feats, choices);
        if (refs.isEmpty() && optional.isEmpty() && feats.isEmpty() && choices.isEmpty() && consume == null) {
            return null;
        }
        return new ClassFeatureMechanicsDto(refs, optional, feats, choices, consume);
    }

    private static void collect(List<ClassFeatureNode> nodes, List<String> refs, List<String> optional,
            List<String> feats, List<ClassFeatureChoiceDto> choices) {
        for (ClassFeatureNode node : nodes) {
            if (node instanceof ClassFeatureReferenceNode ref) {
                refs.add(ref.reference().targetId());
            } else if (node instanceof ClassExternalFeatureNode ext) {
                if (ext.reference().kind() == ClassExternalKind.OPTIONAL_FEATURE
                        && ext.reference().targetId() != null) {
                    optional.add(ext.reference().targetId());
                }
                if (ext.reference().kind() == ClassExternalKind.FEAT && ext.reference().targetId() != null) {
                    feats.add(ext.reference().targetId());
                }
            } else if (node instanceof ClassFeatureChoice choice) {
                ClassFeatureChoiceDto mapped = mapChoice(choice);
                if (mapped.count() != null || mapped.style() != null
                        || !mapped.options().isEmpty() || !mapped.groups().isEmpty()) {
                    choices.add(mapped);
                }
                collectReferences(choice.options(), refs, optional, feats);
            } else if (node instanceof ClassFeatureSection section) {
                collect(section.entries(), refs, optional, feats, choices);
                collect(section.items(), refs, optional, feats, choices);
            }
        }
    }

    private static ClassFeatureChoiceDto mapChoice(ClassFeatureChoice choice) {
        List<ClassChoiceReferenceDto> options = new ArrayList<>();
        List<ClassFeatureChoiceDto> groups = new ArrayList<>();
        mapChoiceNodes(choice.options(), options, groups);
        return new ClassFeatureChoiceDto(choice.count(), choice.style(), options, groups);
    }

    private static void mapChoiceNodes(List<ClassFeatureNode> nodes, List<ClassChoiceReferenceDto> options,
            List<ClassFeatureChoiceDto> groups) {
        for (ClassFeatureNode node : nodes) {
            if (node instanceof ClassFeatureReferenceNode ref) {
                options.add(new ClassChoiceReferenceDto(ref.reference().kind().name(),
                        ref.reference().identity().name(), ref.reference().identity().source(),
                        ref.reference().targetId()));
            } else if (node instanceof ClassExternalFeatureNode ext && ext.reference().targetId() != null) {
                options.add(new ClassChoiceReferenceDto(ext.reference().kind().name(), ext.reference().name(),
                        ext.reference().source(), ext.reference().targetId()));
            } else if (node instanceof ClassExternalFeatureNode ext) {
                options.add(new ClassChoiceReferenceDto(ext.reference().kind().name(), ext.reference().name(),
                        ext.reference().source(), null));
            } else if (node instanceof ClassFeatureChoice nested) {
                ClassFeatureChoiceDto mapped = mapChoice(nested);
                if (mapped.count() != null || mapped.style() != null
                        || !mapped.options().isEmpty() || !mapped.groups().isEmpty()) {
                    groups.add(mapped);
                }
            } else if (node instanceof ClassFeatureSection section) {
                mapChoiceNodes(section.entries(), options, groups);
                mapChoiceNodes(section.items(), options, groups);
            }
        }
    }

    private static void collectReferences(List<ClassFeatureNode> nodes, List<String> refs,
            List<String> optional, List<String> feats) {
        for (ClassFeatureNode node : nodes) {
            if (node instanceof ClassFeatureReferenceNode ref) {
                refs.add(ref.reference().targetId());
            } else if (node instanceof ClassExternalFeatureNode ext) {
                if (ext.reference().kind() == ClassExternalKind.OPTIONAL_FEATURE
                        && ext.reference().targetId() != null) {
                    optional.add(ext.reference().targetId());
                }
                if (ext.reference().kind() == ClassExternalKind.FEAT && ext.reference().targetId() != null) {
                    feats.add(ext.reference().targetId());
                }
            } else if (node instanceof ClassFeatureChoice choice) {
                collectReferences(choice.options(), refs, optional, feats);
            } else if (node instanceof ClassFeatureSection section) {
                collectReferences(section.entries(), refs, optional, feats);
                collectReferences(section.items(), refs, optional, feats);
            }
        }
    }

    private static ClassFeatureReferenceDto featureReference(ClassFeatureUnlock unlock) {
        return new ClassFeatureReferenceDto(unlock.feature().targetId(), unlock.level(), unlock.order(),
                unlock.gainSubclassFeature(), unlock.gainSubclassFeatureHasContent(), unlock.tableDisplayName());
    }

    private static List<ClassProgressionTableDto> safeTables(List<ClassProgressionTable> tables) {
        return tables.stream().map(table -> new ClassProgressionTableDto(
                table.order(), table.kind(), safeLabel(table.title()), safeColumns(table.columns()),
                table.rows().stream().map(ClassPublicMapper::safeRow).toList(),
                table.subclasses())).toList();
    }

    private static ClassProgressionTableRowDto safeRow(ClassTableRow row) {
        List<ClassTableCell> cells = new ArrayList<>();
        List<ClassPositionedTableCellDto> positionedCells = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < row.cells().size(); columnIndex++) {
            ClassTableCell cell = row.cells().get(columnIndex);
            if (cell.kind() == ClassTableCellKind.PRESENTATION_TEXT) {
                continue;
            }
            cells.add(cell);
            positionedCells.add(new ClassPositionedTableCellDto(
                    columnIndex, cell.kind(), cell.number(), cell.dice(), cell.rollable()));
        }
        return new ClassProgressionTableRowDto(row.level(), cells, positionedCells);
    }

    private static List<String> safeColumns(List<String> columns) {
        List<String> result = new ArrayList<>(columns.size());
        for (int index = 0; index < columns.size(); index++) {
            String original = columns.get(index);
            String label = original.contains("{@") || original.contains("<") ? null : safeLabel(original);
            result.add(label == null ? "Column " + (index + 1) : label);
        }
        return List.copyOf(result);
    }

    private static String safeLabel(String value) {
        if (value == null || value.contains("<")) {
            return null;
        }
        int start = value.indexOf("{@");
        int end = value.indexOf('}', start + 2);
        if (start < 0 || end < 0) {
            return value.contains("{@") ? null : value;
        }
        String macro = value.substring(start + 2, end);
        int separator = macro.indexOf(' ');
        if (separator < 0) {
            return null;
        }
        String tag = macro.substring(0, separator);
        String[] parts = macro.substring(separator + 1).split("\\|", -1);
        String display = tag.equals("filter") || parts.length < 3 || parts[2].isBlank()
                ? parts[0] : parts[2];
        return value.substring(0, start) + display + value.substring(end + 1);
    }

    private static ClassSpellcastingDto safeSpellcasting(ClassSpellcasting value) {
        if (value == null) {
            return null;
        }
        return new ClassSpellcastingDto(value.casterProgression(), value.ability(), value.cantrips(), value.known(),
                value.prepared(), value.preparedExpression(), value.preparedChange(), value.fixedKnown(),
                value.fixedAllowLowerLevel(), value.fixedByLevel(), safeTables(value.slotTables()),
                safeTables(value.pactTables()), value.additionalSpells());
    }

    private static ClassStartingOptions safeStarting(ClassStartingOptions value) {
        if (value == null) {
            return null;
        }
        ClassProficiencyOptions proficiencies = safeProficiencies(value.proficiencies());
        if (value.additionalFromBackground() == null && proficiencies == null && value.equipment().isEmpty()) {
            return null;
        }
        return new ClassStartingOptions(value.additionalFromBackground(), proficiencies, value.equipment());
    }

    private static ClassMulticlassOptions safeMulticlass(ClassMulticlassOptions value) {
        if (value == null) {
            return null;
        }
        ClassProficiencyOptions proficiencies = safeProficiencies(value.proficienciesGained());
        return value.requirements() == null && proficiencies == null
                ? null : new ClassMulticlassOptions(value.requirements(), proficiencies);
    }

    private static ClassProficiencyOptions safeProficiencies(ClassProficiencyOptions value) {
        if (value == null) {
            return null;
        }
        if (value.skills().isEmpty() && value.weapons().isEmpty() && value.armor().isEmpty()
                && value.armorPackages().isEmpty() && value.toolPackages().isEmpty()
                && value.weaponPackages().isEmpty()) {
            return null;
        }
        return new ClassProficiencyOptions(value.skills(), safeGrants(value.weapons()), safeGrants(value.armor()),
                value.armorPackages(), value.toolPackages(), value.weaponPackages());
    }

    private static List<ClassProficiencyGrant> safeGrants(List<ClassProficiencyGrant> values) {
        return values.stream().map(value -> new ClassProficiencyGrant(
                safeLabel(value.proficiency()), null, value.optional())).toList();
    }

    private static ClassSourceMetadataDto sources(ClassSourceRelationships value) {
        if (value == null) {
            return null;
        }
        List<ClassReprintDto> reprints = value.reprints().stream()
                .filter(reference -> reference.identity() != null)
                .map(reference -> new ClassReprintDto(reference.kind(), reference.targetId(),
                        reference.identity().name(), new SourceReference(reference.identity().source(), null)))
                .sorted(Comparator.comparing(ClassReprintDto::id)).toList();
        if (value.otherSources().isEmpty() && reprints.isEmpty()) {
            return null;
        }
        return new ClassSourceMetadataDto(value.otherSources(), reprints);
    }

    private static Integer page(SourceReference source) {
        return source == null ? null : source.page();
    }
}
