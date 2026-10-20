package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.ConditionChangeMechanic;
import com.thedndpub.data.mechanics.model.ConditionChoiceMechanic;
import com.thedndpub.data.mechanics.model.ConditionEndsOnSaveSuccess;
import com.thedndpub.data.mechanics.model.ConditionEndsWithSpell;
import com.thedndpub.data.mechanics.model.ConditionName;
import com.thedndpub.data.mechanics.model.ConditionOnSaveOutcome;
import com.thedndpub.data.mechanics.model.ConditionOperation;
import com.thedndpub.data.mechanics.model.CreatureTypeRestriction;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicContextBinding;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.RepeatSaveTiming;
import com.thedndpub.data.mechanics.model.RequiresConcentration;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SubjectReference;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.UnsupportedReason;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Package-private rules for PHB condition applications, removals, choices and dependencies. */
final class PhbConditionMechanicsRules {
    private static final String BODY = PhbSpellMechanicsExtractor.BODY;
    private static final Pattern CONDITION_TAG = Pattern.compile(
            "(?i)\\{@condition\\s+([a-z]+)(?:\\|[^}]*)?}");
    private static final Pattern EXCLUSIVE_CONDITION_CHOICE = Pattern.compile(
            "(?i)either\\s+\\{@condition\\s+([a-z]+)(?:\\|[^}]*)?}\\s+or\\s+"
                    + "\\{@condition\\s+([a-z]+)(?:\\|[^}]*)?}\\s*\\(your choice\\)");
    MechanicsDocument extractConditionRemovals(SpellDefinition spell,
                                                       SpellMechanicsEvidenceSource source,
                                                       MechanicsDocument original) {
        var assembly = new SpellMechanicsAssembly(source, original, "phb-condition-removal-patterns");
        List<LocatedText> entries = locatedEntries(spell.entries(), "/entries", source);
        Set<String> emitted = new HashSet<>();
        Set<String> assessed = new HashSet<>();
        LocatedPhrase spellTarget = genericConditionTarget(entries);
        ConditionRemovalChoice removalChoice = conditionRemovalChoice(entries);
        MechanicRevisionReference choiceGroup = null;
        if (removalChoice != null) {
            MechanicRecord choice = assembly.attempt(removalChoice.slot(), MechanicFamily.CONDITION, () -> {
                var associations = new ArrayList<MechanicEvidenceAssociation>();
                associations.add(MechanicEvidenceAssociation.of(
                        source.textEvidence(removalChoice.selectionPointer(), removalChoice.selectionPhrase()),
                        "/payload/choicesToSelect", EvidenceRole.QUALIFIER));
                for (int index = 0; index < removalChoice.conditions().size(); index++) {
                    String raw = removalChoice.conditions().get(index);
                    Matcher tag = Pattern.compile("(?i)\\{@condition\\s+" + Pattern.quote(raw)
                            + "(?:\\|[^}]*)?}").matcher(removalChoice.alternativesText());
                    if (tag.find()) {
                        associations.add(MechanicEvidenceAssociation.of(
                                source.textEvidence(removalChoice.alternativesPointer(), tag.group()),
                                "/payload/alternatives/" + index, EvidenceRole.VALUE));
                    }
                }
                return assembly.claim(removalChoice.slot(), "condition-removal-exclusive-choice-v1",
                        new ConditionChoiceMechanic(ConditionOperation.REMOVE,
                                removalChoice.conditions().stream().map(this::conditionName).toList(), 1),
                        PhbSpellMechanicsExtractor.unknownContext(), associations, false, MechanicsCompleteness.PARTIAL);
            });
            if (choice != null) {
                choiceGroup = choice.revision().reference(choice.id());
            }
        }
        final MechanicRevisionReference removalChoiceReference = choiceGroup;
        if (removalChoice != null && removalChoice.unmodeledReason() != null) {
            assembly.abstain(removalChoice.unmodeledReason(), MechanicFamily.CONDITION,
                    UnsupportedReason.FAMILY_NOT_IMPLEMENTED, removalChoice.selectionPointer());
        }
        for (LocatedText entry : entries) {
            Matcher matcher = CONDITION_TAG.matcher(entry.text());
            while (matcher.find()) {
                String raw = matcher.group(1).toLowerCase(java.util.Locale.ROOT);
                ConditionName condition = conditionName(raw);
                if (!assessed.add(raw)) {
                    continue;
                }
                if (condition == null) {
                    assembly.abstain("condition-reference-" + raw, MechanicFamily.CONDITION,
                            UnsupportedReason.UNSUPPORTED_SYNTAX, entry.pointer());
                    continue;
                }
                LocatedPhrase removal = explicitConditionRemoval(entry.text(), raw);
                if (removal == null) {
                    assembly.abstain("condition-reference-" + raw, MechanicFamily.CONDITION,
                            UnsupportedReason.AMBIGUOUS_INTERPRETATION, entry.pointer());
                    continue;
                }
                String key = raw;
                assembly.attempt("condition-remove-" + key, MechanicFamily.CONDITION, () -> {
                    TextMechanicsEvidence evidence = source.textEvidence(entry.pointer(), removal.phrase());
                    var associations = new ArrayList<MechanicEvidenceAssociation>();
                    Matcher tagMatcher = CONDITION_TAG.matcher(entry.text());
                    while (tagMatcher.find()) {
                        if (tagMatcher.group(1).equalsIgnoreCase(key)) {
                            TextMechanicsEvidence conditionEvidence = source.textEvidence(entry.pointer(),
                                    tagMatcher.group());
                            associations.add(MechanicEvidenceAssociation.of(conditionEvidence,
                                    "/payload", EvidenceRole.EFFECT_ROLE));
                            break;
                        }
                    }
                    associations.add(MechanicEvidenceAssociation.of(evidence, "/payload/qualifiers",
                            EvidenceRole.QUALIFIER));
                    if (removalChoiceReference != null && removalChoice.conditions().contains(key)) {
                        associations.add(MechanicEvidenceAssociation.of(
                                source.textEvidence(removalChoice.alternativesPointer(),
                                        removalChoice.alternativesText()),
                                "/payload/choiceGroup", EvidenceRole.REFERENCE));
                    }
                    if (spellTarget != null) {
                        associations.add(MechanicEvidenceAssociation.of(
                                source.textEvidence(spellTarget.pointer(), spellTarget.phrase()), "/context/subject",
                                EvidenceRole.QUALIFIER));
                    }
                    return assembly.claim("condition-remove-" + key, "condition-explicit-removal-v1",
                            new ConditionChangeMechanic(ConditionOperation.REMOVE, condition,
                            List.of(removal.phrase()), removalChoiceReference != null
                                    && removalChoice.conditions().contains(key) ? removalChoiceReference : null),
                            new MechanicContext(spellTarget == null
                                    ? new UnknownSubject(UnknownValueReason.NOT_INTERPRETED)
                                    : new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE), List.of()),
                            associations, false,
                            MechanicsCompleteness.PARTIAL);
                });
            }
        }
        return assembly.finish(List.of(source.source().jsonPointer() + "/entries"),
                List.of(source.source().jsonPointer() + "/entriesHigherLevel"));
    }

    private ConditionRemovalChoice conditionRemovalChoice(List<LocatedText> entries) {
        for (LocatedText entry : entries) {
            String lower = entry.text().toLowerCase(java.util.Locale.ROOT);
            if (lower.contains("end either one disease or one condition afflicting it")) {
                Matcher tags = CONDITION_TAG.matcher(entry.text());
                List<String> conditions = new ArrayList<>();
                while (tags.find()) {
                    String raw = tags.group(1).toLowerCase(java.util.Locale.ROOT);
                    if (conditionName(raw) != null && !conditions.contains(raw)) {
                        conditions.add(raw);
                    }
                }
                if (conditions.size() >= 2) {
                    return new ConditionRemovalChoice("condition-remove-choice-" + String.join("-", conditions),
                            entry.pointer(), entry.text(), entry.pointer(), entry.text(), conditions,
                            "condition-removal-unmodeled-disease-option");
                }
            }
            Matcher effect = Pattern.compile("(?i)one effect that \\{@condition\\s+([^}|]+)(?:\\|[^}]*)?}"
                    + "\\s+or\\s+\\{@condition\\s+([^}|]+)(?:\\|[^}]*)?}").matcher(entry.text());
            LocatedText selection = entries.stream().filter(candidate -> candidate.text().toLowerCase(
                    java.util.Locale.ROOT).contains("one of the following effects on the target")).findFirst()
                    .orElse(null);
            if (effect.find() && selection != null) {
                String first = effect.group(1).toLowerCase(java.util.Locale.ROOT);
                String second = effect.group(2).toLowerCase(java.util.Locale.ROOT);
                if (conditionName(first) != null && conditionName(second) != null) {
                    return new ConditionRemovalChoice("condition-remove-choice-" + first + "-" + second,
                            selection.pointer(), selection.text(), entry.pointer(), entry.text(),
                            List.of(first, second),
                            "condition-removal-unmodeled-alternatives");
                }
            }
        }
        return null;
    }

    private LocatedPhrase explicitConditionRemoval(String text, String condition) {
        Pattern tag = Pattern.compile("(?i)\\{@condition\\s+" + Pattern.quote(condition)
                + "(?:\\|[^}]*)?}");
        Matcher matcher = tag.matcher(text);
        while (matcher.find()) {
            String sentence = sentenceContaining(text, matcher.group());
            String lower = sentence.toLowerCase(java.util.Locale.ROOT);
            boolean ending = lower.matches("(?s).*(?:condition\\s+ends|ends?\\s+the\\s+condition).*")
                    || lower.matches("(?s).*end(?:s|ed)?\\s+(?:either\\s+)?(?:one of the following effects on the target|one condition afflicting it).*")
                    || lower.matches("(?s).*one effect that\\s+.*");
            if (ending) {
                return new LocatedPhrase("", sentence);
            }
        }
        String lower = text.toLowerCase(java.util.Locale.ROOT);
        if (lower.contains("end either one disease or one condition afflicting it")
                && lower.contains("the condition can be") && tag.matcher(text).find()) {
            return new LocatedPhrase("", text.trim());
        }
        return null;
    }

    static boolean containsConditionTag(List<EntryDte> entries) {
        for (EntryDte entry : entries) {
            if (entry instanceof TextEntryDte text && CONDITION_TAG.matcher(text.text()).find()) {
                return true;
            }
            if (entry instanceof SectionEntryDte section && containsConditionTag(section.entries())) {
                return true;
            }
            if (entry instanceof com.thedndpub.data.dte.common.entry.ListEntryDte list
                    && containsConditionTag(list.items())) {
                return true;
            }
            if (entry instanceof NamedEntryDte named && containsConditionTag(named.entries())) {
                return true;
            }
        }
        return false;
    }

    private LocatedPhrase genericConditionTarget(List<LocatedText> entries) {
        for (LocatedText entry : entries) {
            String lower = entry.text().toLowerCase(java.util.Locale.ROOT);
            for (String phrase : List.of("a creature you touch", "the creature you touch", "you touch a creature",
                    "the target", "a creature of your choice")) {
                int index = lower.indexOf(phrase);
                if (index >= 0) {
                    return new LocatedPhrase(entry.pointer(), entry.text().substring(index, index + phrase.length()));
                }
            }
        }
        return null;
    }

    MechanicsDocument extractStructuredConditionImmunities(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        String spellId = spell.id().value();
        var assembly = new SpellMechanicsAssembly(source, original, "structured-condition-immunity-v1");
        if (spellId.equals("spell:xphb:freedom-of-movement")) {
            assembly.abstain("condition-immunity-source-restriction", MechanicFamily.CONDITION,
                    UnsupportedReason.AMBIGUOUS_INTERPRETATION, "/entries/0");
            return finishConditionImmunityAssessment(spell, source, assembly);
        }
        if (spellId.equals("spell:xphb:heroes-feast")) {
            assembly.abstain("condition-immunity-beneficiary-scope", MechanicFamily.CONDITION,
                    UnsupportedReason.AMBIGUOUS_INTERPRETATION, "/entries/0");
            return finishConditionImmunityAssessment(spell, source, assembly);
        }
        if (spellId.equals("spell:tce:tasha-s-otherworldly-guise")) {
            assembly.abstain("condition-immunity-planar-choice", MechanicFamily.CONDITION,
                    UnsupportedReason.AMBIGUOUS_INTERPRETATION, "/entries/1/items/1");
            return finishConditionImmunityAssessment(spell, source, assembly);
        }
        if (spellId.equals("spell:au:iron-body")) {
            assembly.abstain("condition-immunity-existing-condition-removal", MechanicFamily.CONDITION,
                    UnsupportedReason.FAMILY_NOT_IMPLEMENTED, "/entries/0");
        }

        List<LocatedText> entries = locatedEntries(spell.entries(), "/entries", source);
        LocatedPhrase target = willingCreatureTarget(entries);
        LocatedPhrase durationText = spellDurationPhrase(entries);
        LocatedPhrase stateRestriction = spellId.equals("spell:xphb:gaseous-form")
                ? firstPhrase(entries, Pattern.compile("(?i)while in this form")) : null;
        for (int index = 0; index < spell.mechanics().conditionImmunities().size(); index++) {
            String raw = spell.mechanics().conditionImmunities().get(index)
                    .toLowerCase(java.util.Locale.ROOT);
            int conditionIndex = index;
            assembly.attempt("condition-grant-immunity-" + raw, MechanicFamily.CONDITION, () -> {
                ConditionName condition = conditionName(raw);
                if (condition == null) {
                    throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, "/conditionImmune/"
                            + conditionIndex);
                }
                if (target == null || durationText == null || spell.durations().size() != 1
                        || !"timed".equals(spell.durations().getFirst().type())) {
                    throw new SpellPatternMismatch(UnsupportedReason.AMBIGUOUS_INTERPRETATION, "/entries/0");
                }
                LocatedPhrase immunityText = explicitConditionImmunity(entries, raw);
                if (immunityText == null) {
                    throw new SpellPatternMismatch(UnsupportedReason.AMBIGUOUS_INTERPRETATION, "/entries/0");
                }
                if (spellId.equals("spell:xphb:gaseous-form") && stateRestriction == null) {
                    throw new SpellPatternMismatch(UnsupportedReason.AMBIGUOUS_INTERPRETATION, "/entries/0");
                }

                var fieldEvidence = source.fieldEvidence("/conditionImmune/" + conditionIndex);
                var targetEvidence = source.textEvidence(target.pointer(), target.phrase());
                var durationEvidence = source.fieldEvidence("/duration/0");
                var durationTextEvidence = source.textEvidence(durationText.pointer(), durationText.phrase());
                var bindings = new ArrayList<MechanicContextBinding>();
                var associations = new ArrayList<MechanicEvidenceAssociation>();
                associations.add(MechanicEvidenceAssociation.of(targetEvidence, "/context/subject",
                        EvidenceRole.QUALIFIER));
                bindings.add(new AppliesForDuration(new DeclaredSpellDurationReference(spell.id(), 0,
                        source.source().revision()), List.of(durationEvidence.id(), durationTextEvidence.id())));
                associations.add(MechanicEvidenceAssociation.of(durationEvidence, "/context/bindings/0",
                        EvidenceRole.QUALIFIER));
                associations.add(MechanicEvidenceAssociation.of(durationTextEvidence, "/context/bindings/0",
                        EvidenceRole.QUALIFIER));
                if (Boolean.TRUE.equals(spell.durations().getFirst().concentration())) {
                    var concentrationEvidence = source.fieldEvidence("/duration/0/concentration");
                    int bindingIndex = bindings.size();
                    bindings.add(new RequiresConcentration(List.of(concentrationEvidence.id())));
                    associations.add(MechanicEvidenceAssociation.of(concentrationEvidence,
                            "/context/bindings/" + bindingIndex, EvidenceRole.QUALIFIER));
                }

                var qualifiers = new ArrayList<String>();
                qualifiers.add(target.phrase());
                qualifiers.add(immunityText.phrase());
                associations.add(MechanicEvidenceAssociation.of(targetEvidence, "/payload/qualifiers",
                        EvidenceRole.QUALIFIER));
                associations.add(MechanicEvidenceAssociation.of(fieldEvidence, "/payload",
                        EvidenceRole.EFFECT_ROLE));
                associations.add(MechanicEvidenceAssociation.of(fieldEvidence, "/payload/condition",
                        EvidenceRole.VALUE));
                associations.add(MechanicEvidenceAssociation.of(
                        source.textEvidence(immunityText.pointer(), immunityText.phrase()),
                        "/payload/qualifiers", EvidenceRole.QUALIFIER));
                if (stateRestriction != null) {
                    qualifiers.add(stateRestriction.phrase());
                    associations.add(MechanicEvidenceAssociation.of(
                            source.textEvidence(stateRestriction.pointer(), stateRestriction.phrase()),
                            "/payload/qualifiers", EvidenceRole.QUALIFIER));
                }
                return assembly.claim("condition-grant-immunity-" + raw,
                        "structured-condition-immunity-v1",
                        new ConditionChangeMechanic(ConditionOperation.GRANT_IMMUNITY, condition, qualifiers),
                        new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE), bindings),
                        associations, true, MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
            });
        }
        return finishConditionImmunityAssessment(spell, source, assembly);
    }

    private MechanicsDocument finishConditionImmunityAssessment(
            SpellDefinition spell, SpellMechanicsEvidenceSource source, SpellMechanicsAssembly assembly) {
        String root = source.source().jsonPointer();
        return assembly.finish(List.of(root + "/conditionImmune", root + "/range", root + "/duration",
                        root + "/entries"),
                List.of(root + "/entriesHigherLevel"));
    }

    private LocatedPhrase willingCreatureTarget(List<LocatedText> entries) {
        Pattern targetPattern = Pattern.compile("(?i)(?:one|a) willing creature you touch|you touch a willing creature");
        for (LocatedText entry : entries) {
            Matcher matcher = targetPattern.matcher(entry.text());
            if (matcher.find()) {
                return new LocatedPhrase(entry.pointer(), matcher.group());
            }
        }
        return null;
    }

    private LocatedPhrase spellDurationPhrase(List<LocatedText> entries) {
        return firstPhrase(entries, Pattern.compile("(?i)for the duration|until the spell ends"));
    }

    private LocatedPhrase explicitConditionImmunity(List<LocatedText> entries, String condition) {
        Matcher matcher;
        Pattern conditionTag = Pattern.compile("(?i)\\{@condition\\s+" + Pattern.quote(condition)
                + "(?:\\|[^}]*)?}");
        for (LocatedText entry : entries) {
            matcher = conditionTag.matcher(entry.text());
            while (matcher.find()) {
                String sentence = sentenceContaining(entry.text(), matcher.group());
                if (Pattern.compile("(?i)\\b(?:immune|immunity)\\b").matcher(sentence).find()) {
                    return new LocatedPhrase(entry.pointer(), sentence);
                }
            }
        }
        return null;
    }

    private LocatedPhrase firstPhrase(List<LocatedText> entries, Pattern pattern) {
        for (LocatedText entry : entries) {
            Matcher matcher = pattern.matcher(entry.text());
            if (matcher.find()) {
                return new LocatedPhrase(entry.pointer(), matcher.group());
            }
        }
        return null;
    }

    void extractConditions(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                   SpellMechanicsAssembly assembly) {
        ConditionTarget target = conditionTarget(spell, source);
        MechanicRecord savingThrow = conditionSavingThrow(spell, source, assembly, target);
        MechanicRevisionReference savingThrowReference = savingThrow == null
                ? null : savingThrow.revision().reference(savingThrow.id());
        Map<ConditionName, MechanicRevisionReference> choiceGroups = conditionChoice(
                spell, source, assembly, target, savingThrowReference);
        for (String raw : spell.mechanics().conditionsInflicted()) {
            ConditionName condition = conditionName(raw);
            MechanicRevisionReference choiceGroup = choiceGroups.get(condition);
            EffectContext effectContext = choiceGroup == null
                    ? conditionEffectContext(spell, source, target, condition, savingThrowReference, false)
                    : null;
            conditionClaim(raw, ConditionOperation.APPLY, spell, source, assembly, "/conditionInflict",
                    choiceGroup, effectContext);
        }
        for (String raw : spell.mechanics().conditionImmunities()) {
            conditionClaim(raw, ConditionOperation.GRANT_IMMUNITY, spell, source, assembly,
                    "/conditionImmune", null, null);
        }
        preserveComplexConditionEndings(spell, source, assembly);
    }

    private void preserveComplexConditionEndings(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                                SpellMechanicsAssembly assembly) {
        String id = spell.id().value();
        String pointer = switch (id) {
            case "spell:phb:fear", "spell:phb:hypnotic-pattern" -> "/entries/1";
            default -> null;
        };
        if (pointer == null) {
            return;
        }
        try {
            assembly.unsupported("condition-ending-rules", MechanicFamily.CONDITION,
                    List.of(source.textEvidence(pointer, null).id()));
        } catch (SpellPatternMismatch mismatch) {
            assembly.abstain("condition-ending-rules", MechanicFamily.CONDITION,
                    mismatch.reason(), mismatch.relativePointer());
        }
    }

    private Map<ConditionName, MechanicRevisionReference> conditionChoice(
            SpellDefinition spell, SpellMechanicsEvidenceSource source, SpellMechanicsAssembly assembly,
            ConditionTarget target, MechanicRevisionReference savingThrow) {
        var candidates = new ArrayList<ConditionChoiceCandidate>();
        for (int index = 0; index < spell.entries().size(); index++) {
            String pointer = "/entries/" + index;
            try {
                String text = source.text(pointer).text();
                Matcher matcher = EXCLUSIVE_CONDITION_CHOICE.matcher(text);
                while (matcher.find()) {
                    String first = matcher.group(1).toLowerCase(java.util.Locale.ROOT);
                    String second = matcher.group(2).toLowerCase(java.util.Locale.ROOT);
                    if (!spell.mechanics().conditionsInflicted().contains(first)
                            || !spell.mechanics().conditionsInflicted().contains(second)) {
                        continue;
                    }
                    candidates.add(new ConditionChoiceCandidate(pointer,
                            text, first, second));
                }
            } catch (SpellPatternMismatch ignored) {
                // Nested entries need a dedicated source path before they can define a choice group.
            }
        }
        if (candidates.size() != 1) {
            return Map.of();
        }
        ConditionChoiceCandidate candidate = candidates.getFirst();
        ConditionName first = conditionName(candidate.first());
        ConditionName second = conditionName(candidate.second());
        if (first == null || second == null || first == second) {
            assembly.abstain("condition-choice-unsupported", MechanicFamily.CONDITION,
                    UnsupportedReason.UNSUPPORTED_SYNTAX, candidate.pointer());
            return Map.of();
        }
        MechanicRecord choice = assembly.attempt("condition-choice-" + candidate.first() + "-" + candidate.second(),
                MechanicFamily.CONDITION, () -> {
                    EffectContext effectContext = conditionEffectContext(spell, source, target, first,
                            savingThrow, true);
                    var firstField = source.fieldEvidence("/conditionInflict/"
                            + spell.mechanics().conditionsInflicted().indexOf(candidate.first()));
                    var secondField = source.fieldEvidence("/conditionInflict/"
                            + spell.mechanics().conditionsInflicted().indexOf(candidate.second()));
                    var choiceEvidence = source.textEvidence(candidate.pointer(), candidate.sentence());
                    return assembly.claim("condition-choice-" + candidate.first() + "-" + candidate.second(),
                            "condition-exclusive-choice-v1",
                            new ConditionChoiceMechanic(ConditionOperation.APPLY, List.of(first, second), 1),
                            effectContext.context(), combineAssociations(List.of(
                                    MechanicEvidenceAssociation.of(choiceEvidence, "/payload/choicesToSelect",
                                            EvidenceRole.QUALIFIER),
                                    MechanicEvidenceAssociation.of(firstField, "/payload/alternatives/0",
                                            EvidenceRole.VALUE),
                                    MechanicEvidenceAssociation.of(secondField, "/payload/alternatives/1",
                                            EvidenceRole.VALUE)), effectContext.associations()), false,
                            MechanicsCompleteness.PARTIAL);
                });
        if (choice == null) {
            return Map.of();
        }
        MechanicRevisionReference reference = choice.revision().reference(choice.id());
        return Map.of(first, reference, second, reference);
    }

    private ConditionName conditionName(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ConditionName.valueOf(value.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void conditionClaim(String raw, ConditionOperation operation, SpellDefinition spell,
                                SpellMechanicsEvidenceSource source, SpellMechanicsAssembly assembly, String field,
                                MechanicRevisionReference choiceGroup, EffectContext effectContext) {
        String operationId = operation.name().toLowerCase(java.util.Locale.ROOT).replace('_', '-');
        assembly.attempt("condition-" + operationId + "-" + raw, MechanicFamily.CONDITION,
                () -> conditionRecord(raw, operation, spell, source, assembly, field, choiceGroup, effectContext));
    }

    private MechanicRecord conditionRecord(String raw, ConditionOperation operation, SpellDefinition spell,
                                           SpellMechanicsEvidenceSource source, SpellMechanicsAssembly assembly,
                                           String field, MechanicRevisionReference choiceGroup,
                                           EffectContext effectContext) {
        String operationId = operation.name().toLowerCase(java.util.Locale.ROOT).replace('_', '-');
        ConditionName condition;
        try {
            condition = ConditionName.valueOf(raw.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, field);
        }
        var fieldEvidence = source.fieldEvidence(field + "/" +
                (operation == ConditionOperation.APPLY ? spell.mechanics().conditionsInflicted().indexOf(raw)
                        : spell.mechanics().conditionImmunities().indexOf(raw)));
        var qualifiers = new java.util.ArrayList<String>();
        var associations = new java.util.ArrayList<MechanicEvidenceAssociation>();
        TextMechanicsEvidence choiceEvidence = null;
        associations.add(MechanicEvidenceAssociation.of(fieldEvidence, "/payload", EvidenceRole.EFFECT_ROLE));
        String conditionTag = "{@condition " + raw;
        for (int index = 0; index < spell.entries().size(); index++) {
            String pointer = "/entries/" + index;
            try {
                String authoredText = source.text(pointer).text();
                int tagPosition = authoredText.toLowerCase(java.util.Locale.ROOT)
                        .indexOf(conditionTag.toLowerCase(java.util.Locale.ROOT));
                if (tagPosition < 0) {
                    continue;
                }
                int start = Math.max(authoredText.lastIndexOf('.', tagPosition),
                        authoredText.lastIndexOf(';', tagPosition)) + 1;
                int end = authoredText.indexOf('.', tagPosition);
                if (end < 0) {
                    end = authoredText.length();
                }
                String phrase = authoredText.substring(start, end).trim();
                if (!phrase.isEmpty() && !qualifiers.contains(phrase)) {
                    var qualifierEvidence = source.textEvidence(pointer, phrase);
                    if (choiceGroup != null) {
                        choiceEvidence = qualifierEvidence;
                    }
                    qualifiers.add(phrase);
                    associations.add(MechanicEvidenceAssociation.of(qualifierEvidence, "/payload/qualifiers",
                            EvidenceRole.QUALIFIER));
                }
            } catch (SpellPatternMismatch ignored) {
                // Nested entries require a dedicated evidence path and are not flattened here.
            }
        }
        if (choiceGroup != null && choiceEvidence != null) {
            associations.add(MechanicEvidenceAssociation.of(choiceEvidence, "/payload/choiceGroup",
                    EvidenceRole.REFERENCE));
        }
        if (spell.id().value().equals("spell:phb:fear") && condition == ConditionName.FRIGHTENED) {
            String endingText = source.text("/entries/1").text();
            if (!qualifiers.contains(endingText)) {
                var endingEvidence = source.textEvidence("/entries/1", endingText);
                qualifiers.add(endingText);
                associations.add(MechanicEvidenceAssociation.of(endingEvidence, "/payload/qualifiers",
                        EvidenceRole.QUALIFIER));
            }
        }
        if (effectContext != null) {
            associations.addAll(effectContext.associations());
        }
        return assembly.claim("condition-" + operationId + "-" + raw,
                effectContext == null ? "condition-explicit-metadata-v1" : "condition-dependency-context-v1",
                new ConditionChangeMechanic(operation, condition, qualifiers, choiceGroup),
                effectContext == null ? PhbSpellMechanicsExtractor.unknownContext() : effectContext.context(), associations,
                true, MechanicsCompleteness.PARTIAL);
    }

    private MechanicRecord conditionSavingThrow(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                                SpellMechanicsAssembly assembly, ConditionTarget target) {
        if (spell.mechanics().conditionsInflicted().isEmpty() || spell.mechanics().savingThrows().isEmpty()) {
            return null;
        }
        if (spell.mechanics().savingThrows().size() != 1) {
            assembly.abstain("condition-save-dependency", MechanicFamily.SAVING_THROW_REQUIREMENT,
                    UnsupportedReason.AMBIGUOUS_INTERPRETATION, "/savingThrow");
            return null;
        }
        Ability saveAbility = PhbSpellMechanicsExtractor.ability(spell.mechanics().savingThrows().getFirst());
        List<LocatedText> entries = locatedEntries(spell.entries(), "/entries", source);
        boolean hasFailedCondition = spell.mechanics().conditionsInflicted().stream()
                .anyMatch(condition -> failedConditionEvidence(entries, condition) != null);
        if (!hasFailedCondition) {
            assembly.abstain("condition-save-dependency", MechanicFamily.SAVING_THROW_REQUIREMENT,
                    UnsupportedReason.AMBIGUOUS_INTERPRETATION, "/entries/0");
            return null;
        }
        LocatedText saveText = entries.stream()
                .filter(value -> value.text().contains(saveAbility.name().charAt(0)
                        + saveAbility.name().substring(1).toLowerCase(java.util.Locale.ROOT) + " saving throw"))
                .findFirst().orElse(null);
        if (saveText == null) {
            assembly.abstain("condition-save-dependency", MechanicFamily.SAVING_THROW_REQUIREMENT,
                    UnsupportedReason.PROVENANCE_UNAVAILABLE, "/entries/0");
            return null;
        }
        return assembly.attempt("condition-saving-throw-v1", MechanicFamily.SAVING_THROW_REQUIREMENT, () ->
                assembly.claim("condition-saving-throw", "condition-saving-throw-v1",
                        new SavingThrowRequirementMechanic(new FixedAbility(saveAbility),
                                new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)),
                        new MechanicContext(target == null ? new UnknownSubject(UnknownValueReason.NOT_INTERPRETED)
                                : target.subject(), List.of()), combineAssociations(List.of(
                                MechanicEvidenceAssociation.of(source.textEvidence(saveText.pointer(), saveText.text()),
                                        "/payload", EvidenceRole.EFFECT_ROLE),
                                MechanicEvidenceAssociation.of(source.fieldEvidence("/savingThrow/0"),
                                        "/payload/ability", EvidenceRole.VALUE)),
                                targetAssociations(source, target)), false, MechanicsCompleteness.PARTIAL));
    }

    private ConditionTarget conditionTarget(SpellDefinition spell, SpellMechanicsEvidenceSource source) {
        List<LocatedText> entries = locatedEntries(spell.entries(), "/entries", source);
        for (LocatedText entry : entries) {
            String lower = entry.text().toLowerCase(java.util.Locale.ROOT);
            if (lower.contains("you or a creature you touch")) {
                return targetFrom(entry, "You or a creature you touch", TargetScope.CASTER_OR_TOUCHED_CREATURE, null,
                        spell, source);
            }
            if (lower.contains("each creature in a 30-foot cone")
                    || lower.contains("each creature in the area who sees the pattern")) {
                String phrase = lower.contains("each creature in a 30-foot cone")
                        ? "Each creature in a 30-foot cone" : "Each creature in the area who sees the pattern";
                return targetFrom(entry, phrase, TargetScope.AREA_AFFECTED_CREATURES, null, spell, source);
            }
            if (lower.contains("choose a humanoid that you can see")) {
                return targetFrom(entry, "Choose a humanoid that you can see", TargetScope.SINGLE_TARGET_CREATURE,
                        CreatureTypeRestriction.HUMANOID, spell, source);
            }
            if (lower.contains("choose one creature that you can see")) {
                return targetFrom(entry, "Choose one creature that you can see", TargetScope.SINGLE_TARGET_CREATURE,
                        null, spell, source);
        }
            if (lower.contains("each creature")) {
                String phrase = sentencePrefix(entry.text(), "each creature");
                return targetFrom(entry, phrase, TargetScope.AREA_AFFECTED_CREATURES, null, spell, source);
            }
            if (lower.contains("you touch a creature")) {
                return targetFrom(entry, "You touch a creature", TargetScope.CASTER_OR_TOUCHED_CREATURE, null,
                        spell, source);
            }
            if (lower.contains("choose a creature")) {
                String phrase = sentencePrefix(entry.text(), "choose a creature");
                return targetFrom(entry, phrase, TargetScope.SINGLE_TARGET_CREATURE, null, spell, source);
            }
            if (lower.contains("a creature of your choice")) {
                String phrase = sentencePrefix(entry.text(), "a creature of your choice");
                return targetFrom(entry, phrase, TargetScope.SINGLE_TARGET_CREATURE, null, spell, source);
            }
        }
        return ConditionTarget.unknown();
    }

    private String sentencePrefix(String text, String phrase) {
        int index = text.toLowerCase(java.util.Locale.ROOT).indexOf(phrase);
        if (index < 0) {
            return phrase;
        }
        int end = text.indexOf('.', index);
        return text.substring(index, end < 0 ? text.length() : end + 1).trim();
    }

    private ConditionTarget targetFrom(LocatedText entry, String phrase, TargetScope scope,
                                       CreatureTypeRestriction restriction, SpellDefinition spell,
                                       SpellMechanicsEvidenceSource source) {
        var evidence = new ArrayList<LocatedPhrase>();
        evidence.add(new LocatedPhrase(entry.pointer(), sentenceContaining(entry.text(), phrase)));
        TargetScope targetScope = scope;
        for (LocatedText higherLevel : locatedEntries(spell.entriesHigherLevel(), "/entriesHigherLevel", source)) {
            Matcher additionalTargets = Pattern.compile("(?i)target one additional (?:creature|humanoid)")
                    .matcher(higherLevel.text());
            if (additionalTargets.find()) {
                targetScope = TargetScope.MULTIPLE_TARGET_CREATURES;
                evidence.add(new LocatedPhrase(higherLevel.pointer(),
                        sentenceContaining(higherLevel.text(), additionalTargets.group())));
            }
        }
        return new ConditionTarget(new SpellTargetSubject(targetScope, restriction), List.copyOf(evidence));
    }

    private EffectContext conditionEffectContext(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                                 ConditionTarget target, ConditionName condition,
                                                 MechanicRevisionReference savingThrow, boolean choiceGroup) {
        SubjectReference subject = target == null ? new UnknownSubject(UnknownValueReason.NOT_INTERPRETED)
                : target.subject();
        var bindings = new ArrayList<MechanicContextBinding>();
        var associations = new ArrayList<MechanicEvidenceAssociation>();
        if (target != null) {
            associations.addAll(targetAssociations(source, target));
        }

        LocatedPhrase failed = choiceGroup
                ? failedConditionEvidence(locatedEntries(spell.entries(), "/entries", source),
                        condition.name().toLowerCase(java.util.Locale.ROOT))
                : directFailedConditionEvidence(spell, source, condition);
        if (savingThrow != null && failed != null) {
            TextMechanicsEvidence evidence = source.textEvidence(failed.pointer(), failed.phrase());
            int index = bindings.size();
            bindings.add(new ConditionOnSaveOutcome(savingThrow, SaveOutcome.FAILURE, List.of(evidence.id())));
            associations.add(MechanicEvidenceAssociation.of(evidence, "/context/bindings/" + index,
                    EvidenceRole.QUALIFIER));
        }

        if (spell.durations().size() == 1 && "timed".equals(spell.durations().getFirst().type())) {
            var duration = new DeclaredSpellDurationReference(spell.id(), 0, source.source().revision());
            int index = bindings.size();
            var fieldEvidence = source.fieldEvidence("/duration/0");
            bindings.add(new AppliesForDuration(duration, List.of(fieldEvidence.id())));
            associations.add(MechanicEvidenceAssociation.of(fieldEvidence, "/context/bindings/" + index,
                    EvidenceRole.QUALIFIER));
        }
        if (spell.durations().size() == 1 && Boolean.TRUE.equals(spell.durations().getFirst().concentration())) {
            var concentrationEvidence = source.fieldEvidence("/duration/0/concentration");
            int index = bindings.size();
            bindings.add(new RequiresConcentration(List.of(concentrationEvidence.id())));
            associations.add(MechanicEvidenceAssociation.of(concentrationEvidence, "/context/bindings/" + index,
                    EvidenceRole.QUALIFIER));
        }

        if (savingThrow != null) {
            LocatedPhrase ending = successfulRepeatSave(spell, source);
            if (ending != null) {
                TextMechanicsEvidence evidence = source.textEvidence(ending.pointer(), ending.phrase());
                int index = bindings.size();
                bindings.add(new ConditionEndsOnSaveSuccess(savingThrow,
                        RepeatSaveTiming.END_OF_EACH_TARGET_TURN, List.of(evidence.id())));
                associations.add(MechanicEvidenceAssociation.of(evidence, "/context/bindings/" + index,
                        EvidenceRole.QUALIFIER));
            }
        }
        if (spell.id().value().equals("spell:phb:greater-invisibility")) {
            LocatedPhrase ending = firstPhrase(spell, source, Pattern.compile("(?i)until the spell ends"));
            if (ending != null) {
                TextMechanicsEvidence evidence = source.textEvidence(ending.pointer(), ending.phrase());
                int index = bindings.size();
                bindings.add(new ConditionEndsWithSpell(List.of(evidence.id())));
                associations.add(MechanicEvidenceAssociation.of(evidence, "/context/bindings/" + index,
                        EvidenceRole.QUALIFIER));
            }
        }
        return new EffectContext(new MechanicContext(subject, bindings), List.copyOf(associations));
    }

    private LocatedPhrase directFailedConditionEvidence(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                                        ConditionName condition) {
        return failedConditionEvidence(locatedEntries(spell.entries(), "/entries", source),
                condition.name().toLowerCase(java.util.Locale.ROOT));
    }

    private LocatedPhrase failedConditionEvidence(List<LocatedText> entries, String condition) {
        Pattern conditionTag = Pattern.compile("(?i)\\{@condition\\s+" + Pattern.quote(condition)
                + "(?:\\|[^}]*)?}");
        Pattern failedSave = Pattern.compile("(?i)(?:on a failed save|if it fails|must succeed on a .*? saving throw or)");
        for (LocatedText entry : entries) {
            Matcher conditionMatcher = conditionTag.matcher(entry.text());
            while (conditionMatcher.find()) {
                String sentence = sentenceContaining(entry.text(), conditionMatcher.group());
                if (failedSave.matcher(sentence).find()) {
                    return new LocatedPhrase(entry.pointer(), sentence);
                }
            }
        }
        return null;
    }

    private LocatedPhrase successfulRepeatSave(SpellDefinition spell, SpellMechanicsEvidenceSource source) {
        if (spell.mechanics().savingThrows().size() != 1) {
            return null;
        }
        String ability = spell.mechanics().savingThrows().getFirst().toLowerCase(java.util.Locale.ROOT);
        for (LocatedText entry : locatedEntries(spell.entries(), "/entries", source)) {
            String lower = entry.text().toLowerCase(java.util.Locale.ROOT);
            String startText = "at the end of each of its turns,";
            int start = lower.indexOf(startText);
            if (start < 0 || lower.indexOf(startText, start + startText.length()) >= 0) {
                continue;
            }
            int saveStart = lower.indexOf(" can make ", start);
            int saveEnd = lower.indexOf(" saving throw.", saveStart);
            int successStart = lower.indexOf(" on a success,", saveEnd);
            int ending = successStart < 0 ? -1 : lower.indexOf("spell ends", successStart);
            int end = ending < 0 ? -1 : lower.indexOf('.', ending);
            if (saveStart < 0 || saveEnd < 0 || successStart < 0 || end < 0
                    || !lower.substring(saveStart, saveEnd).contains(ability)) {
                continue;
            }
            return new LocatedPhrase(entry.pointer(), entry.text().substring(start, end + 1));
        }
        return null;
    }

    private LocatedPhrase firstPhrase(SpellDefinition spell, SpellMechanicsEvidenceSource source, Pattern pattern) {
        for (LocatedText entry : locatedEntries(spell.entries(), "/entries", source)) {
            Matcher matcher = pattern.matcher(entry.text());
            if (matcher.find()) {
                return new LocatedPhrase(entry.pointer(), matcher.group());
            }
        }
        return null;
    }

    private List<LocatedText> locatedEntries(List<EntryDte> entries, String pointer,
                                             SpellMechanicsEvidenceSource source) {
        var result = new ArrayList<LocatedText>();
        for (int index = 0; index < entries.size(); index++) {
            collectLocatedEntry(entries.get(index), pointer + "/" + index, source, result);
        }
        return List.copyOf(result);
    }

    private void collectLocatedEntry(EntryDte entry, String pointer, SpellMechanicsEvidenceSource source,
                                     List<LocatedText> result) {
        if (entry instanceof TextEntryDte) {
            result.add(new LocatedText(pointer, source.text(pointer).text()));
        } else if (entry instanceof SectionEntryDte section) {
            for (int index = 0; index < section.entries().size(); index++) {
                collectLocatedEntry(section.entries().get(index), pointer + "/entries/" + index, source, result);
            }
        } else if (entry instanceof NamedEntryDte named) {
            for (int index = 0; index < named.entries().size(); index++) {
                collectLocatedEntry(named.entries().get(index), pointer + "/entries/" + index, source, result);
            }
        } else if (entry instanceof com.thedndpub.data.dte.common.entry.ListEntryDte list) {
            for (int index = 0; index < list.items().size(); index++) {
                collectLocatedEntry(list.items().get(index), pointer + "/items/" + index, source, result);
            }
        }
    }

    private String sentenceContaining(String text, String phrase) {
        int index = text.indexOf(phrase);
        PhbSpellMechanicsExtractor.require(index >= 0, BODY);
        int start = Math.max(text.lastIndexOf('.', index), text.lastIndexOf(';', index)) + 1;
        int end = text.indexOf('.', index + phrase.length());
        return text.substring(start, end < 0 ? text.length() : end + 1).trim();
    }

    private List<MechanicEvidenceAssociation> targetAssociations(SpellMechanicsEvidenceSource source,
                                                                 ConditionTarget target) {
        var associations = new ArrayList<MechanicEvidenceAssociation>();
        if (target != null) {
            for (LocatedPhrase phrase : target.evidence()) {
                associations.add(MechanicEvidenceAssociation.of(source.textEvidence(phrase.pointer(), phrase.phrase()),
                        "/context/subject", EvidenceRole.QUALIFIER));
            }
        }
        return List.copyOf(associations);
    }

    private List<MechanicEvidenceAssociation> combineAssociations(List<MechanicEvidenceAssociation> first,
                                                                  List<MechanicEvidenceAssociation> second) {
        var result = new ArrayList<>(first);
        result.addAll(second);
        return List.copyOf(result);
    }

    private record LocatedText(String pointer, String text) {
    }

    private record LocatedPhrase(String pointer, String phrase) {
    }

    private record ConditionTarget(SpellTargetSubject subject, List<LocatedPhrase> evidence) {
        private static ConditionTarget unknown() {
            return null;
        }
    }

    private record EffectContext(MechanicContext context, List<MechanicEvidenceAssociation> associations) {
    }

    private record ConditionChoiceCandidate(String pointer, String sentence, String first, String second) {
    }

    private record ConditionRemovalChoice(String slot, String selectionPointer, String selectionPhrase,
                                          String alternativesPointer, String alternativesText,
                                          List<String> conditions, String unmodeledReason) {
        private ConditionRemovalChoice {
            conditions = List.copyOf(conditions);
        }
    }

}
