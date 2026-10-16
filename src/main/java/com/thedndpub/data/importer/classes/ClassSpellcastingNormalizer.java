package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dto.classes.ClassAdditionalSpellGroup;
import com.thedndpub.data.dto.classes.ClassProgressionTable;
import com.thedndpub.data.dto.classes.ClassSpellFilterClause;
import com.thedndpub.data.dto.classes.ClassSpellFilterField;
import com.thedndpub.data.dto.classes.ClassSpellGate;
import com.thedndpub.data.dto.classes.ClassSpellGateKind;
import com.thedndpub.data.dto.classes.ClassSpellGrant;
import com.thedndpub.data.dto.classes.ClassSpellGrantCategory;
import com.thedndpub.data.dto.classes.ClassSpellLevelCount;
import com.thedndpub.data.dto.classes.ClassSpellReference;
import com.thedndpub.data.dto.classes.ClassSpellSelection;
import com.thedndpub.data.dto.classes.ClassSpellSelectionKind;
import com.thedndpub.data.dto.classes.ClassSpellUsage;
import com.thedndpub.data.dto.classes.ClassSpellUsageKind;
import com.thedndpub.data.dto.classes.ClassSpellcasting;
import com.thedndpub.data.dto.classes.ClassTableKind;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dte.classes.ClassAdditionalSpellsDte;
import com.thedndpub.data.dte.classes.ClassDte;
import com.thedndpub.data.dte.classes.ClassSpellLevelDte;
import com.thedndpub.data.dte.classes.ClassSpellSelectionDte;
import com.thedndpub.data.dte.classes.ClassSpellcastingSourceDte;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.thedndpub.data.importer.classes.ClassReferenceResolver.safe;

/** Preserves spell declarations and selectors without requiring a Spell Catalog or evaluating rules. */
public final class ClassSpellcastingNormalizer {
    public ClassSpellcasting normalize(ClassSpellcastingSourceDte source, List<ClassProgressionTable> tables) {
        ClassDte classSource = source instanceof ClassDte value ? value : null;
        String expression = classSource == null ? null : classSource.preparedSpells();
        List<ClassAdditionalSpellGroup> groups = groups(source.additionalSpells());
        if (source.casterProgression() == null && source.spellcastingAbility() == null
                && source.cantripProgression() == null && source.spellsKnownProgression() == null
                && source.preparedSpellsProgression() == null && source.preparedSpellsChange() == null
                && expression == null && groups.isEmpty()) {
            return null;
        }
        if (expression != null && !expression.matches("<\\$level\\$>(?: / 2)? \\+ <\\$(?:int|wis|cha)_mod\\$>")) {
            throw new IllegalStateException("Unsupported prepared-spell source expression: " + expression);
        }
        List<ClassSpellLevelCount> fixed = new ArrayList<>();
        if (classSource != null && classSource.spellsKnownProgressionFixedByLevel() != null) {
            classSource.spellsKnownProgressionFixedByLevel().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()).forEach(level -> level.getValue().entrySet().stream()
                            .sorted(Map.Entry.comparingByKey()).forEach(spellLevel -> fixed.add(
                                    new ClassSpellLevelCount(level.getKey(), spellLevel.getKey(), spellLevel.getValue()))));
        }
        return new ClassSpellcasting(source.casterProgression(), source.spellcastingAbility(),
                ClassMechanicsNormalizer.levels(source.cantripProgression()),
                ClassMechanicsNormalizer.levels(source.spellsKnownProgression()),
                ClassMechanicsNormalizer.levels(source.preparedSpellsProgression()), expression, source.preparedSpellsChange(),
                classSource == null ? List.of() : ClassMechanicsNormalizer.levels(classSource.spellsKnownProgressionFixed()),
                classSource == null ? null : classSource.spellsKnownProgressionFixedAllowLowerLevel(), fixed,
                tables.stream().filter(table -> table.kind() == ClassTableKind.SPELL_SLOTS).toList(),
                "pact".equals(source.casterProgression()) ? tables : List.of(), groups);
    }

    public List<ClassAdditionalSpellGroup> groups(List<ClassAdditionalSpellsDte> source) {
        List<ClassAdditionalSpellGroup> groups = new ArrayList<>();
        for (ClassAdditionalSpellsDte group : safe(source)) {
            List<ClassSpellGrant> grants = new ArrayList<>();
            grants(grants, ClassSpellGrantCategory.KNOWN, group.known());
            grants(grants, ClassSpellGrantCategory.PREPARED, group.prepared());
            grants(grants, ClassSpellGrantCategory.EXPANDED, group.expanded());
            grants(grants, ClassSpellGrantCategory.INNATE, group.innate());
            groups.add(new ClassAdditionalSpellGroup(group.name(), group.ability(), group.resourceName(), grants));
        }
        return List.copyOf(groups);
    }

    private static void grants(
            List<ClassSpellGrant> grants,
            ClassSpellGrantCategory category,
            Map<String, ClassSpellLevelDte> levels) {
        if (levels == null) {
            return;
        }
        for (var level : levels.entrySet()) {
            ClassSpellGate gate = gate(level.getKey());
            ClassSpellLevelDte value = level.getValue();
            add(grants, category, gate, new ClassSpellUsage(ClassSpellUsageKind.UNSPECIFIED, null, null, null), value.spells());
            add(grants, category, gate, new ClassSpellUsage(ClassSpellUsageKind.UNSPECIFIED, null, null, null), value.unrestricted());
            add(grants, category, gate, new ClassSpellUsage(ClassSpellUsageKind.RITUAL, null, null, null), value.ritual());
            usage(grants, category, gate, ClassSpellUsageKind.DAILY, value.daily());
            usage(grants, category, gate, ClassSpellUsageKind.REST, value.rest());
            usage(grants, category, gate, ClassSpellUsageKind.RESOURCE, value.resource());
        }
    }

    private static void usage(
            List<ClassSpellGrant> grants,
            ClassSpellGrantCategory category,
            ClassSpellGate gate,
            ClassSpellUsageKind kind,
            Map<String, List<ClassSpellSelectionDte>> source) {
        if (source != null) {
            source.forEach((key, values) -> add(grants, category, gate, usage(kind, key), values));
        }
    }

    private static ClassSpellUsage usage(ClassSpellUsageKind kind, String key) {
        if (key.matches("[0-9]+e?")) {
            boolean each = key.endsWith("e");
            String count = each ? key.substring(0, key.length() - 1) : key;
            return new ClassSpellUsage(kind, Integer.parseInt(count), null, each);
        }
        if (Set.of("str", "dex", "con", "int", "wis", "cha").contains(key)) {
            return new ClassSpellUsage(kind, null, key, null);
        }
        throw new IllegalStateException("Unsupported spell usage token: " + key);
    }

    private static void add(
            List<ClassSpellGrant> grants,
            ClassSpellGrantCategory category,
            ClassSpellGate gate,
            ClassSpellUsage usage,
            List<ClassSpellSelectionDte> selections) {
        if (selections != null) {
            grants.add(new ClassSpellGrant(category, gate, usage, selections.stream()
                    .map(ClassSpellcastingNormalizer::selection).toList()));
        }
    }

    private static ClassSpellGate gate(String key) {
        if (key.equals("_")) {
            return new ClassSpellGate(ClassSpellGateKind.UNSPECIFIED, null);
        }
        if (key.matches("[1-9][0-9]*")) {
            return new ClassSpellGate(ClassSpellGateKind.CLASS_LEVEL, Integer.valueOf(key));
        }
        if (key.matches("s[1-9][0-9]*")) {
            return new ClassSpellGate(ClassSpellGateKind.SPELL_LEVEL, Integer.valueOf(key.substring(1)));
        }
        throw new IllegalStateException("Unsupported spell level gate: " + key);
    }

    private static ClassSpellSelection selection(ClassSpellSelectionDte source) {
        if (source.spell() != null) {
            return new ClassSpellSelection(ClassSpellSelectionKind.SPELL, reference(source.spell()),
                    List.of(), List.of(), false, null);
        }
        if (source.choose() != null) {
            var choose = source.choose();
            if (choose.count() != null && source.count() != null && !choose.count().equals(source.count())) {
                throw new IllegalStateException("Contradictory nested spell choice counts");
            }
            Integer count = choose.count() == null ? source.count() : choose.count();
            List<ClassSpellReference> from = safe(choose.from()).stream()
                    .map(ClassSpellcastingNormalizer::reference).toList();
            boolean unrestricted = "".equals(choose.filter());
            return new ClassSpellSelection(ClassSpellSelectionKind.CHOICE, null, from,
                    filters(choose.filter()), unrestricted, count);
        }
        return new ClassSpellSelection(ClassSpellSelectionKind.ALL, null, List.of(),
                filters(source.all()), "".equals(source.all()), source.count());
    }

    public static ClassSpellReference reference(String uid) {
        int qualifier = uid.indexOf('#');
        String configuration = qualifier < 0 ? null : uid.substring(qualifier + 1);
        String[] parts = (qualifier < 0 ? uid : uid.substring(0, qualifier)).split("\\|", -1);
        if (parts.length > 2 || parts[0].isBlank()) {
            throw new IllegalStateException("Invalid spell reference: " + uid);
        }
        SourceIdentity source = parts.length == 2 && !parts[1].isEmpty() ? new SourceIdentity(parts[1]) : null;
        return new ClassSpellReference(parts[0], source, configuration);
    }

    private static List<ClassSpellFilterClause> filters(String expression) {
        if (expression == null || expression.isEmpty()) {
            return List.of();
        }
        List<ClassSpellFilterClause> clauses = new ArrayList<>();
        for (String expressionPart : expression.split("\\|", -1)) {
            String[] parts = expressionPart.split("=", -1);
            if (parts.length != 2 || parts[1].isEmpty()) {
                throw new IllegalStateException("Invalid spell filter: " + expressionPart);
            }
            ClassSpellFilterField field = ClassSpellFilterField.valueOf(parts[0].toUpperCase(Locale.ROOT));
            List<String> values = List.of(parts[1].split(";", -1));
            clauses.add(new ClassSpellFilterClause(field,
                    field == ClassSpellFilterField.LEVEL ? values.stream().map(Integer::valueOf).toList() : List.of(),
                    field == ClassSpellFilterField.CLASS ? values : List.of(),
                    field == ClassSpellFilterField.SCHOOL ? values : List.of(),
                    field == ClassSpellFilterField.SOURCE ? values.stream().map(SourceIdentity::new).toList() : List.of()));
        }
        return List.copyOf(clauses);
    }
}
