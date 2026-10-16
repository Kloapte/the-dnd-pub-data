package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dto.classes.ClassAbilityRequirement;
import com.thedndpub.data.dto.classes.ClassAbilitySet;
import com.thedndpub.data.dto.classes.ClassAbilityThreshold;
import com.thedndpub.data.dto.classes.ClassArmorProficiencySet;
import com.thedndpub.data.dto.classes.ClassChoiceProgression;
import com.thedndpub.data.dto.classes.ClassEquipmentBranch;
import com.thedndpub.data.dto.classes.ClassEquipmentGroup;
import com.thedndpub.data.dto.classes.ClassEquipmentKind;
import com.thedndpub.data.dto.classes.ClassEquipmentOption;
import com.thedndpub.data.dto.classes.ClassExternalKind;
import com.thedndpub.data.dto.classes.ClassExternalReference;
import com.thedndpub.data.dto.classes.ClassHitDice;
import com.thedndpub.data.dto.classes.ClassLevelCount;
import com.thedndpub.data.dto.classes.ClassMulticlassOptions;
import com.thedndpub.data.dto.classes.ClassProficiencyGrant;
import com.thedndpub.data.dto.classes.ClassProficiencyOptions;
import com.thedndpub.data.dto.classes.ClassProgressionTable;
import com.thedndpub.data.dto.classes.ClassRequiredOption;
import com.thedndpub.data.dto.classes.ClassRequirementKind;
import com.thedndpub.data.dto.classes.ClassSkillChoice;
import com.thedndpub.data.dto.classes.ClassStartingOptions;
import com.thedndpub.data.dto.classes.ClassTableCell;
import com.thedndpub.data.dto.classes.ClassTableCellKind;
import com.thedndpub.data.dto.classes.ClassTableKind;
import com.thedndpub.data.dto.classes.ClassTableRow;
import com.thedndpub.data.dto.classes.ClassTableSubclassSelector;
import com.thedndpub.data.dto.classes.ClassToolProficiencySet;
import com.thedndpub.data.dto.classes.ClassWeaponProficiencySet;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dte.classes.ClassAbilityFlagsDte;
import com.thedndpub.data.dte.classes.ClassAbilityRequirementsDte;
import com.thedndpub.data.dte.classes.ClassEquipmentEntryDte;
import com.thedndpub.data.dte.classes.ClassEquipmentGroupDte;
import com.thedndpub.data.dte.classes.ClassFeatProgressionDte;
import com.thedndpub.data.dte.classes.ClassLevelProgressionDte;
import com.thedndpub.data.dte.classes.ClassMulticlassingDte;
import com.thedndpub.data.dte.classes.ClassNameChoiceDte;
import com.thedndpub.data.dte.classes.ClassOptionalProgressionDte;
import com.thedndpub.data.dte.classes.ClassProficienciesDte;
import com.thedndpub.data.dte.classes.ClassProficiencyDte;
import com.thedndpub.data.dte.classes.ClassProgressionTableDte;
import com.thedndpub.data.dte.classes.ClassStartingEquipmentDte;
import com.thedndpub.data.dte.classes.ClassTableCellDte;

import java.util.ArrayList;
import java.util.List;

import static com.thedndpub.data.importer.classes.ClassReferenceResolver.safe;

/** Converts explicit owned values; never interprets prose or computes character mechanics. */
final class ClassMechanicsNormalizer {
    private final ClassExternalReferenceResolver references;

    ClassMechanicsNormalizer(ClassExternalReferenceResolver references) {
        this.references = references;
    }

    List<ClassProgressionTable> tables(List<ClassProgressionTableDte> source) {
        List<ClassProgressionTable> tables = new ArrayList<>();
        for (ClassProgressionTableDte table : safe(source)) {
            if ((table.rows() == null) == (table.rowsSpellProgression() == null)) {
                throw new IllegalStateException("A class table requires exactly one row representation");
            }
            List<ClassTableRow> rows = new ArrayList<>();
            if (table.rows() != null) {
                for (var row : table.rows()) {
                    validateWidth(table, row.size());
                    rows.add(new ClassTableRow(rows.size() + 1, row.stream().map(ClassMechanicsNormalizer::cell).toList()));
                }
            } else {
                for (var row : table.rowsSpellProgression()) {
                    validateWidth(table, row.size());
                    rows.add(new ClassTableRow(rows.size() + 1, row.stream()
                            .map(value -> new ClassTableCell(ClassTableCellKind.NUMBER, value, null, List.of(), null))
                            .toList()));
                }
            }
            tables.add(new ClassProgressionTable(tables.size(), table.rows() == null
                    ? ClassTableKind.SPELL_SLOTS : ClassTableKind.LEVEL_PRESENTATION,
                    table.title(), table.colLabels(), rows, safe(table.subclasses()).stream()
                    .map(value -> new ClassTableSubclassSelector(value.name(), new SourceIdentity(value.source()))).toList()));
        }
        return List.copyOf(tables);
    }

    static ClassTableCell cell(ClassTableCellDte source) {
        if (source.text() != null) {
            return new ClassTableCell(ClassTableCellKind.PRESENTATION_TEXT, null, source.text(), List.of(), null);
        }
        if (source.number() != null) {
            return new ClassTableCell(ClassTableCellKind.NUMBER, source.number(), null, List.of(), null);
        }
        if (source.type() == null) {
            throw new IllegalStateException("A structured class table cell requires a type");
        }
        ClassTableCellKind kind = switch (source.type()) {
            case "bonus" -> ClassTableCellKind.BONUS;
            case "bonusSpeed" -> ClassTableCellKind.SPEED_BONUS;
            case "dice" -> ClassTableCellKind.DICE;
            default -> throw new IllegalStateException("Unsupported class cell: " + source.type());
        };
        return new ClassTableCell(kind, source.value(), null, safe(source.toRoll()).stream()
                .map(value -> new ClassHitDice(value.number(), value.faces())).toList(), source.rollable());
    }

    List<ClassChoiceProgression> choices(
            List<ClassOptionalProgressionDte> optional,
            List<ClassFeatProgressionDte> feats) {
        List<ClassChoiceProgression> groups = new ArrayList<>();
        for (var group : safe(optional)) {
            List<ClassRequiredOption> required = new ArrayList<>();
            if (group.required() != null) {
                group.required().forEach((level, uids) -> uids.forEach(uid -> required.add(
                        new ClassRequiredOption(level, references.resolve(ClassExternalKind.OPTIONAL_FEATURE, uid)))));
            }
            groups.add(new ClassChoiceProgression(ClassExternalKind.OPTIONAL_FEATURE, group.name(),
                    group.featureType(), progression(group.progression()), required));
        }
        for (var group : safe(feats)) {
            groups.add(new ClassChoiceProgression(ClassExternalKind.FEAT, group.name(),
                    group.category(), progression(group.progression()), List.of()));
        }
        return List.copyOf(groups);
    }

    static List<ClassLevelCount> progression(ClassLevelProgressionDte source) {
        if (source == null) {
            return List.of();
        }
        if (source.dense() != null) {
            return levels(source.dense());
        }
        return source.milestones().entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(entry -> new ClassLevelCount(entry.getKey(), entry.getValue())).toList();
    }

    static List<ClassLevelCount> levels(List<Integer> source) {
        List<ClassLevelCount> values = new ArrayList<>();
        for (Integer value : safe(source)) {
            values.add(new ClassLevelCount(values.size() + 1, value));
        }
        return List.copyOf(values);
    }

    ClassStartingOptions starting(ClassProficienciesDte proficiencies, ClassStartingEquipmentDte equipment) {
        if (proficiencies == null && equipment == null) {
            return null;
        }
        List<ClassEquipmentGroup> groups = new ArrayList<>();
        for (ClassEquipmentGroupDte group : equipment == null ? List.<ClassEquipmentGroupDte>of() : safe(equipment.defaultData())) {
            boolean unconditional = group.branches().containsKey("_");
            if (unconditional && group.branches().size() != 1) {
                throw new IllegalStateException("Unconditional equipment cannot share a choice group");
            }
            List<ClassEquipmentBranch> branches = new ArrayList<>();
            group.branches().forEach((key, entries) -> branches.add(new ClassEquipmentBranch(key,
                    entries.stream().map(this::equipment).toList())));
            groups.add(new ClassEquipmentGroup(groups.size(), unconditional, branches));
        }
        return new ClassStartingOptions(equipment == null ? null : equipment.additionalFromBackground(),
                proficiencies(proficiencies), groups);
    }

    private ClassEquipmentOption equipment(ClassEquipmentEntryDte source) {
        int alternatives = (source.item() == null ? 0 : 1) + (source.value() == null ? 0 : 1)
                + (source.equipmentType() == null ? 0 : 1) + (source.equipmentTypes() == null ? 0 : 1)
                + (source.special() == null ? 0 : 1);
        if (alternatives != 1) {
            throw new IllegalStateException("Equipment entry requires one item, category, currency or special form");
        }
        if (source.item() != null) {
            ClassExternalReference reference = references.resolve(ClassExternalKind.ITEM, source.item());
            ClassEquipmentKind kind = reference.kind() == ClassExternalKind.ITEM_GROUP
                    ? ClassEquipmentKind.ITEM_GROUP : ClassEquipmentKind.ITEM;
            return new ClassEquipmentOption(kind, reference, List.of(), source.quantity(), null, null);
        }
        if (source.value() != null) {
            return new ClassEquipmentOption(ClassEquipmentKind.CURRENCY, null, List.of(),
                    source.quantity(), source.value(), null);
        }
        if (source.special() != null) {
            return new ClassEquipmentOption(ClassEquipmentKind.SPECIAL, null, List.of(),
                    source.quantity(), null, source.special());
        }
        boolean multiple = source.equipmentTypes() != null;
        return new ClassEquipmentOption(multiple ? ClassEquipmentKind.CATEGORY_ALTERNATIVES : ClassEquipmentKind.CATEGORY,
                null, multiple ? source.equipmentTypes() : List.of(source.equipmentType()), source.quantity(), null, null);
    }

    ClassProficiencyOptions proficiencies(ClassProficienciesDte source) {
        if (source == null) {
            return null;
        }
        List<ClassSkillChoice> skills = safe(source.skills()).stream().map(skill -> {
            ClassNameChoiceDte choose = skill.choose();
            return new ClassSkillChoice(skill.any(), choose == null ? null : choose.count(),
                    choose == null ? List.of() : safe(choose.from()).stream()
                            .map(name -> references.resolve(ClassExternalKind.SKILL, name)).toList());
        }).toList();
        return new ClassProficiencyOptions(skills, grants(source.weapons()), grants(source.armor()),
                safe(source.armorProficiencies()).stream()
                        .map(value -> new ClassArmorProficiencySet(value.light(), value.medium(), value.heavy(), value.shield()))
                        .toList(),
                safe(source.toolProficiencies()).stream()
                        .map(value -> new ClassToolProficiencySet(value.thievesTools(), value.tinkersTools(), value.herbalismKit(),
                                value.anyArtisansTool(), value.anyMusicalInstrument())).toList(),
                safe(source.weaponProficiencies()).stream()
                        .map(value -> new ClassWeaponProficiencySet(value.simple(),
                                value.all() == null ? null : value.all().fromFilter())).toList());
    }

    ClassMulticlassOptions multiclass(ClassMulticlassingDte source) {
        return source == null ? null : new ClassMulticlassOptions(
                requirements(source.requirements(), ClassRequirementKind.ALL_OF), proficiencies(source.proficienciesGained()));
    }

    private static ClassAbilityRequirement requirements(ClassAbilityRequirementsDte source, ClassRequirementKind kind) {
        if (source == null) {
            return null;
        }
        List<ClassAbilityThreshold> thresholds = new ArrayList<>();
        threshold(thresholds, "str", source.str());
        threshold(thresholds, "dex", source.dex());
        threshold(thresholds, "con", source.con());
        threshold(thresholds, "int", source.intelligence());
        threshold(thresholds, "wis", source.wis());
        threshold(thresholds, "cha", source.cha());
        List<ClassAbilityRequirement> alternatives = safe(source.or()).stream()
                .map(value -> requirements(value, ClassRequirementKind.ANY_OF)).toList();
        return new ClassAbilityRequirement(kind, thresholds, alternatives);
    }

    static ClassAbilitySet abilities(ClassAbilityFlagsDte value) {
        List<String> abilities = new ArrayList<>();
        if (Boolean.TRUE.equals(value.str())) {
            abilities.add("str");
        }
        if (Boolean.TRUE.equals(value.dex())) {
            abilities.add("dex");
        }
        if (Boolean.TRUE.equals(value.con())) {
            abilities.add("con");
        }
        if (Boolean.TRUE.equals(value.intelligence())) {
            abilities.add("int");
        }
        if (Boolean.TRUE.equals(value.wis())) {
            abilities.add("wis");
        }
        if (Boolean.TRUE.equals(value.cha())) {
            abilities.add("cha");
        }
        return new ClassAbilitySet(abilities);
    }

    private static List<ClassProficiencyGrant> grants(List<ClassProficiencyDte> source) {
        return safe(source).stream().map(value -> new ClassProficiencyGrant(
                value.proficiency(), value.full(), value.optional())).toList();
    }

    private static void threshold(List<ClassAbilityThreshold> values, String ability, Integer minimum) {
        if (minimum != null) {
            values.add(new ClassAbilityThreshold(ability, minimum));
        }
    }

    private static void validateWidth(ClassProgressionTableDte table, int width) {
        if (table.colLabels() == null || table.colLabels().size() != width) {
            throw new IllegalStateException("Class table column/row width mismatch");
        }
    }
}
