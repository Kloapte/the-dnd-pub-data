package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.mechanics.model.AmountExpression;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DamageOutcome;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.IntegerAmount;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SubjectReference;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.MarkupSyntaxException;
import com.thedndpub.data.mechanics.syntax.MarkupTag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reusable, conservative PHB save and damage clauses shared by inventory and selected production owners. */
final class PhbSaveDamageGeneralizer {
    private static final Pattern SAVE = Pattern.compile("(?i)\\b(?:must make (?:a )?|must succeed on (?:a )?)"
            + "(Strength|Dexterity|Constitution|Intelligence|Wisdom|Charisma) saving throw\\b");
    private static final Pattern SIMPLE_DICE = Pattern.compile("^([1-9][0-9]*)d([1-9][0-9]*)$");
    private static final Pattern DICE_PLUS_INTEGER = Pattern.compile("^([1-9][0-9]*d[1-9][0-9]*)\\s*\\+\\s*([0-9]+)$");

    private PhbSaveDamageGeneralizer() { }

    static MechanicsDocument extract(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        SpellMechanicsAssembly assembly = new SpellMechanicsAssembly(source, original, "phb-save-damage-generalized");
        MechanicRecord save = null;
        if (!spell.mechanics().savingThrows().isEmpty()) {
            save = assembly.attempt("saving-throw-requirement-general-v1", MechanicFamily.SAVING_THROW_REQUIREMENT,
                    () -> savingThrow(spell, source, assembly));
        }
        if (!spell.mechanics().damageInflicted().isEmpty()) {
            MechanicRecord saveDependency = save;
            assembly.attemptMany("save-damage-components-general-v1", MechanicFamily.DAMAGE,
                    () -> damage(spell, source, assembly, saveDependency));
        }
        if (!spell.entriesHigherLevel().isEmpty()) {
            if (spell.mechanics().damageInflicted().size() == 1) {
                assembly.attempt("spell-slot-scaling-v1", MechanicFamily.SPELL_SLOT_SCALING,
                        () -> slotScaling(spell, source, assembly));
            } else if (hasScalingText(spell)) {
                assembly.abstain("multi-component-damage-scaling", MechanicFamily.SPELL_SLOT_SCALING,
                        UnsupportedReason.AMBIGUOUS_INTERPRETATION, "/entriesHigherLevel");
            }
        }
        String root = source.source().jsonPointer();
        return assembly.finish(List.of(root + "/entries", root + "/entriesHigherLevel",
                        root + "/savingThrow", root + "/damageInflict"),
                List.of(root + "/entries", root + "/entriesHigherLevel"));
    }

    private static MechanicRecord savingThrow(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        require(spell.mechanics().savingThrows().size() == 1, "/savingThrow");
        LocatedSave save = locateSave(spell, source);
        Ability ability = parseAbility(spell.mechanics().savingThrows().getFirst());
        require(save.ability() == ability, "/savingThrow/0");
        LocatedTarget target = locateTarget(source.text(save.pointer()).text(), save.phrase());
        SubjectReference subject = target == null
                ? new UnknownSubject(UnknownValueReason.NOT_INTERPRETED)
                : target.subject();
        var associations = new ArrayList<MechanicEvidenceAssociation>();
        associations.add(MechanicEvidenceAssociation.of(source.textEvidence(save.pointer(), save.phrase()),
                "/payload", EvidenceRole.EFFECT_ROLE));
        associations.add(MechanicEvidenceAssociation.of(source.fieldEvidence("/savingThrow/0"),
                "/payload/ability", EvidenceRole.VALUE));
        if (target != null) {
            associations.add(MechanicEvidenceAssociation.of(source.textEvidence(save.pointer(), target.phrase()),
                    "/context/subject", EvidenceRole.QUALIFIER));
        } else {
            assembly.abstain("saving-throw-target", MechanicFamily.SAVING_THROW_REQUIREMENT,
                    UnsupportedReason.FAMILY_NOT_IMPLEMENTED, save.pointer());
        }
        return assembly.claim("saving-throw", "saving-throw-requirement-general-v1",
                new com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic(new FixedAbility(ability),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)),
                new MechanicContext(subject, List.of()), associations, false, MechanicsCompleteness.PARTIAL);
    }

    private static List<MechanicRecord> damage(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            MechanicRecord save) {
        LocatedSave locatedSave = spell.mechanics().savingThrows().isEmpty() ? null : locateSave(spell, source);
        if (locatedSave != null) {
            require(save != null, "/savingThrow");
        }
        List<DamageComponent> components = locateComponents(spell, source, locatedSave);
        require(!components.isEmpty(), "/damageInflict");
        List<DamageOutcomeBinding> outcomes = locatedSave == null ? List.of()
                : saveOutcomes(source, components.getFirst(), locatedSave);
        SubjectReference subject = save == null ? new UnknownSubject(UnknownValueReason.NOT_INTERPRETED)
                : save.context().subject();
        List<MechanicRecord> records = new ArrayList<>();
        for (DamageComponent component : components) {
            String slot = components.size() == 1 ? "damage" : "damage-" + component.damageType();
            var associations = new ArrayList<MechanicEvidenceAssociation>();
            String phrase = component.tag().originalText() + " " + component.damageType() + " damage";
            TextMechanicsEvidence damageEvidence = source.textEvidence(component.pointer(), phrase);
            associations.add(MechanicEvidenceAssociation.of(damageEvidence, "/payload", EvidenceRole.EFFECT_ROLE));
            var bindings = new ArrayList<com.thedndpub.data.mechanics.model.MechanicContextBinding>();
            for (DamageOutcomeBinding outcome : outcomes) {
                bindings.add(new AppliesOnSaveOutcome(save.revision().reference(save.id()), outcome.outcome(),
                        outcome.damageOutcome(), List.of(outcome.evidence().id())));
                associations.add(MechanicEvidenceAssociation.of(outcome.evidence(),
                        "/context/bindings/" + (bindings.size() - 1), EvidenceRole.QUALIFIER));
            }
            records.add(assembly.claim(slot, "save-damage-components-general-v1",
                    new DamageMechanic(component.amount(), new FixedDamageType(component.damageType())),
                    new MechanicContext(subject, bindings), associations, false, MechanicsCompleteness.PARTIAL));
        }
        if (locatedSave == null && !spell.mechanics().spellAttacks().isEmpty()) {
            assembly.abstain("damage-attack-trigger", MechanicFamily.DAMAGE,
                    UnsupportedReason.FAMILY_NOT_IMPLEMENTED, components.getFirst().pointer());
        }
        return List.copyOf(records);
    }

    private static LocatedSave locateSave(SpellDefinition spell, SpellMechanicsEvidenceSource source) {
        List<LocatedSave> matches = new ArrayList<>();
        for (int index = 0; index < spell.entries().size(); index++) {
            if (!(spell.entries().get(index) instanceof TextEntryDte)) continue;
            String pointer = "/entries/" + index;
            String text = source.text(pointer).text();
            Matcher matcher = SAVE.matcher(text);
            while (matcher.find()) {
                matches.add(new LocatedSave(pointer, matcher.group(), parseAbility(matcher.group(1))));
            }
        }
        require(matches.size() == 1, "/entries");
        return matches.getFirst();
    }

    private static LocatedTarget locateTarget(String text, String savePhrase) {
        int saveAt = text.indexOf(savePhrase);
        if (saveAt < 0) return null;
        int sentenceStart = text.lastIndexOf('.', saveAt) + 1;
        String prefix = text.substring(sentenceStart, saveAt);
        Matcher multiple = Pattern.compile("(?i)\\b(?:each|every) (?:creature|target|humanoid)\\b")
                .matcher(prefix);
        if (multiple.find()) {
            TargetScope scope = Pattern.compile("(?i)\\b(?:in|within)\\b").matcher(prefix).find()
                    ? TargetScope.AREA_AFFECTED_CREATURES : TargetScope.MULTIPLE_TARGET_CREATURES;
            return new LocatedTarget(new SpellTargetSubject(scope), multiple.group());
        }
        Matcher single = Pattern.compile("(?i)\\b(?:a|the) (?:target|creature|humanoid)\\b")
                .matcher(prefix);
        if (single.find()) {
            return new LocatedTarget(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE), single.group());
        }
        Matcher selected = Pattern.compile("(?i)\\bchoose one creature\\b").matcher(prefix);
        if (selected.find()) {
            return new LocatedTarget(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE), selected.group());
        }
        return null;
    }

    private static List<DamageComponent> locateComponents(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            LocatedSave save) {
        List<List<DamageComponent>> candidates = new ArrayList<>();
        for (int index = 0; index < spell.entries().size(); index++) {
            if (!(spell.entries().get(index) instanceof TextEntryDte)) continue;
            String pointer = "/entries/" + index;
            if (save != null && !pointer.equals(save.pointer())) continue;
            String text = source.text(pointer).text();
            try {
                List<MarkupTag> tags = new BalancedMarkupParser().parse(source.text(pointer)).tags().stream()
                        .filter(tag -> tag.name().equals("damage") && tag.arguments().size() == 1
                                && tag.children().isEmpty()).toList();
                if (tags.isEmpty()) continue;
                List<DamageComponent> components = new ArrayList<>();
                for (int tagIndex = 0; tagIndex < tags.size(); tagIndex++) {
                    MarkupTag tag = tags.get(tagIndex);
                    int suffixEnd = tagIndex + 1 < tags.size()
                            ? tags.get(tagIndex + 1).evidence().span().startInclusive() : text.length();
                    String suffix = text.substring(tag.evidence().span().endExclusive(), suffixEnd);
                    String damageType = spell.mechanics().damageInflicted().stream()
                            .sorted(Comparator.comparingInt(String::length).reversed())
                            .filter(type -> Pattern.compile("(?i)^\\s*" + Pattern.quote(type)
                                    + "\\s+damage\\b").matcher(suffix).find())
                            .findFirst().orElse(null);
                    if (damageType == null) {
                        components.clear();
                        break;
                    }
                    components.add(new DamageComponent(pointer, tag, damageType,
                            amount(tag.arguments().getFirst(), pointer)));
                }
                List<String> foundTypes = components.stream().map(DamageComponent::damageType).toList();
                List<String> expectedTypes = spell.mechanics().damageInflicted();
                if (components.size() == expectedTypes.size()
                        && new HashSet<>(foundTypes).equals(new HashSet<>(expectedTypes))
                        && foundTypes.size() == new HashSet<>(foundTypes).size()) {
                    candidates.add(List.copyOf(components));
                }
            } catch (MarkupSyntaxException exception) {
                // Keep malformed markup visible as an unsupported family through the caller's bounded attempt.
            }
        }
        require(candidates.size() == 1, "/entries");
        return candidates.getFirst();
    }

    private static List<DamageOutcomeBinding> saveOutcomes(
            SpellMechanicsEvidenceSource source,
            DamageComponent component,
            LocatedSave save) {
        String text = source.text(component.pointer()).text();
        String ability = save.ability().name().toLowerCase(Locale.ROOT);
        Pattern failed = Pattern.compile("(?i)on a failed save|must succeed on (?:a )?"
                + Pattern.quote(ability) + " saving throw or take");
        List<String> failureMatches = matches(failed, text);
        require(failureMatches.size() == 1, component.pointer());
        List<DamageOutcomeBinding> outcomes = new ArrayList<>();
        outcomes.add(new DamageOutcomeBinding(SaveOutcome.FAILURE, DamageOutcome.FULL_DAMAGE,
                source.textEvidence(component.pointer(), failureMatches.getFirst())));

        Pattern half = Pattern.compile("(?i)(?:or\\s+)?half(?: as much)? damage on a successful (?:save|one)"
                + "|on a successful (?:save|one),[^.]*?takes half(?: as much)? damage");
        Pattern none = Pattern.compile("(?i)(?:or\\s+)?no damage on a successful (?:save|one)"
                + "|on a successful (?:save|one),[^.]*?takes no damage");
        List<String> halfMatches = matches(half, text);
        List<String> noDamageMatches = matches(none, text);
        require(halfMatches.size() + noDamageMatches.size() <= 1, component.pointer());
        if (!halfMatches.isEmpty()) {
            outcomes.add(new DamageOutcomeBinding(SaveOutcome.SUCCESS, DamageOutcome.HALF_DAMAGE,
                    source.textEvidence(component.pointer(), halfMatches.getFirst())));
        } else if (!noDamageMatches.isEmpty()) {
            outcomes.add(new DamageOutcomeBinding(SaveOutcome.SUCCESS, DamageOutcome.NO_DAMAGE,
                    source.textEvidence(component.pointer(), noDamageMatches.getFirst())));
        }
        return List.copyOf(outcomes);
    }

    private static List<String> matches(Pattern pattern, String text) {
        List<String> found = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) found.add(matcher.group());
        return found;
    }

    private static AmountExpression amount(String expression, String pointer) {
        Matcher simple = SIMPLE_DICE.matcher(expression);
        if (simple.matches()) return dice(simple.group(), pointer);
        Matcher sum = DICE_PLUS_INTEGER.matcher(expression);
        require(sum.matches(), pointer);
        try {
            return new SumAmount(List.of(dice(sum.group(1), pointer),
                    new IntegerAmount(Integer.parseInt(sum.group(2)))));
        } catch (NumberFormatException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, pointer);
        }
    }

    private static DiceAmount dice(String expression, String pointer) {
        Matcher matcher = SIMPLE_DICE.matcher(expression);
        require(matcher.matches(), pointer);
        try {
            return new DiceAmount(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), 0);
        } catch (NumberFormatException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, pointer);
        }
    }

    private static Ability parseAbility(String authored) {
        try {
            return Ability.valueOf(authored.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, "/savingThrow/0");
        }
    }

    private static boolean hasScalingText(SpellDefinition spell) {
        return spell.entriesHigherLevel().stream().anyMatch(entry ->
                entry instanceof TextEntryDte text && text.text().toLowerCase(Locale.ROOT).contains("damage increases"));
    }

    private static MechanicRecord slotScaling(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        return new PhbSpellMechanicsExtractor().generalizedSlotScaling(spell, source, assembly);
    }

    private static void require(boolean condition, String pointer) {
        if (!condition) throw new SpellPatternMismatch(UnsupportedReason.NO_SUPPORTED_PATTERN, pointer);
    }

    private record LocatedSave(String pointer, String phrase, Ability ability) { }

    private record LocatedTarget(SpellTargetSubject subject, String phrase) { }

    private record DamageComponent(String pointer, MarkupTag tag, String damageType, AmountExpression amount) { }

    private record DamageOutcomeBinding(SaveOutcome outcome, DamageOutcome damageOutcome,
                                        TextMechanicsEvidence evidence) { }
}
