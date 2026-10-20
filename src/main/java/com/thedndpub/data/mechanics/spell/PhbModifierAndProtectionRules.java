package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.ApplicabilityCondition;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.AppliesWhen;
import com.thedndpub.data.mechanics.model.ArmorClassModifierMechanic;
import com.thedndpub.data.mechanics.model.DamageApplicability;
import com.thedndpub.data.mechanics.model.DamageProtectionOperation;
import com.thedndpub.data.mechanics.model.DamageResistanceMechanic;
import com.thedndpub.data.mechanics.model.DamageTypeChoice;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.IntegerAmount;
import com.thedndpub.data.mechanics.model.IntegerModifier;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicContextBinding;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.ModifiedStatistic;
import com.thedndpub.data.mechanics.model.ModifierOnSaveOutcome;
import com.thedndpub.data.mechanics.model.ModifierOperation;
import com.thedndpub.data.mechanics.model.RequiresConcentration;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.StatisticModifierMechanic;
import com.thedndpub.data.mechanics.model.SubjectReference;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownValueReason;

import java.util.ArrayList;
import java.util.List;

/** Package-private rules for PHB modifiers, damage resistance and protection. */
final class PhbModifierAndProtectionRules {
    private static final String BODY = PhbSpellMechanicsExtractor.BODY;
    void extractModifierAndProtection(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                               SpellMechanicsAssembly assembly) {
        switch (spell.id().value()) {
            case "spell:phb:shield-of-faith" -> assembly.attempt("shield-of-faith-ac-v1",
                    MechanicFamily.ARMOR_CLASS_MODIFIER,
                    () -> shieldOfFaith(spell, source, assembly));
            case "spell:phb:mage-armor" -> assembly.attempt("mage-armor-base-ac-v1", MechanicFamily.MODIFIER,
                    () -> mageArmor(spell, source, assembly));
            case "spell:phb:barkskin" -> assembly.attempt("barkskin-ac-minimum-v1", MechanicFamily.MODIFIER,
                    () -> barkskin(spell, source, assembly));
            case "spell:phb:longstrider" -> assembly.attempt("longstrider-speed-v1", MechanicFamily.MODIFIER,
                    () -> longstrider(spell, source, assembly));
            case "spell:phb:bless" -> assembly.attempt("bless-roll-bonus-v1", MechanicFamily.MODIFIER,
                    () -> bless(spell, source, assembly));
            case "spell:phb:bane" -> bane(spell, source, assembly);
            case "spell:phb:protection-from-energy" -> assembly.attempt("protection-from-energy-resistance-v1",
                    MechanicFamily.DAMAGE_RESISTANCE,
                    () -> protectionFromEnergy(spell, source, assembly));
            case "spell:phb:stoneskin" -> stoneskin(spell, source, assembly);
            default -> throw new IllegalStateException("Unsupported modifier owner " + spell.id().value());
        }
    }

    private MechanicRecord shieldOfFaith(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                         SpellMechanicsAssembly assembly) {
        String body = "A shimmering field appears and surrounds a creature of your choice within range, "
                + "granting it a +2 bonus to AC for the duration.";
        MechanicContext context = modifierContext(spell, source,
                new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
                List.of(), List.of(), null);
        return modifierClaim(spell, source, assembly, "shield-of-faith-ac", "shield-of-faith-ac-bonus-v1",
                new ArmorClassModifierMechanic(new IntegerModifier(2, "+2")), body, context,
                List.of(),
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
    }

    private MechanicRecord mageArmor(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                     SpellMechanicsAssembly assembly) {
        String body = "You touch a willing creature who isn't wearing armor, and a protective magical force "
                + "surrounds it until the spell ends. The target's base AC becomes 13 + its Dexterity modifier. "
                + "The spell ends if the target dons armor or if you dismiss the spell as an action.";
        var conditions = List.of(
                new ApplicabilityEvidence(ApplicabilityCondition.TARGET_IS_WILLING, "willing creature"),
                new ApplicabilityEvidence(ApplicabilityCondition.TARGET_NOT_WEARING_ARMOR, "isn't wearing armor"));
        MechanicContext context = modifierContext(spell, source,
                new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
                conditions, List.of(), null);
        var payload = new StatisticModifierMechanic(List.of(ModifiedStatistic.BASE_ARMOR_CLASS),
                ModifierOperation.SET_BASE,
                new SumAmount(List.of(new IntegerAmount(13), new SymbolicAmount(AmountSymbol.DEXTERITY_MODIFIER))));
        TextMechanicsEvidence bodyEvidence = source.textEvidence(BODY, body);
        MechanicRecord claim = modifierClaim(spell, source, assembly, "mage-armor-base-ac",
                "mage-armor-base-ac-replacement-v1", payload, body, context, List.of(),
                MechanicsCompleteness.PARTIAL);
        assembly.unsupported("mage-armor-conditional-endings", MechanicFamily.MODIFIER,
                List.of(bodyEvidence.id()));
        return claim;
    }

    private MechanicRecord barkskin(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                    SpellMechanicsAssembly assembly) {
        String body = "You touch a willing creature. Until the spell ends, the target's skin has a rough, "
                + "bark-like appearance, and the target's AC can't be less than 16, regardless of what kind of "
                + "armor it is wearing.";
        var conditions = List.of(new ApplicabilityEvidence(ApplicabilityCondition.TARGET_IS_WILLING,
                "willing creature"));
        MechanicContext context = modifierContext(spell, source,
                new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
                conditions, List.of(), null);
        return modifierClaim(spell, source, assembly, "barkskin-ac-minimum", "barkskin-ac-floor-v1",
                new StatisticModifierMechanic(List.of(ModifiedStatistic.ARMOR_CLASS), ModifierOperation.MINIMUM,
                        new IntegerAmount(16)), body, context, List.of(), MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
    }

    private MechanicRecord longstrider(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                       SpellMechanicsAssembly assembly) {
        String body = "You touch a creature. The target's speed increases by 10 feet until the spell ends.";
        MechanicContext context = modifierContext(spell, source,
                new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
                List.of(), List.of(), null);
        return modifierClaim(spell, source, assembly, "longstrider-speed", "longstrider-speed-increase-v1",
                new StatisticModifierMechanic(List.of(ModifiedStatistic.MOVEMENT_SPEED), ModifierOperation.ADD,
                        new IntegerAmount(10)), body, context, List.of(), MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
    }

    private MechanicRecord bless(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                 SpellMechanicsAssembly assembly) {
        String body = "You bless up to three creatures of your choice within range. Whenever a target makes an "
                + "attack roll or a saving throw before the spell ends, the target can roll a {@dice d4} and add "
                + "the number rolled to the attack roll or saving throw.";
        var when = List.of(new ApplicabilityEvidence(ApplicabilityCondition.TARGET_MAKES_ROLL_BEFORE_SPELL_ENDS,
                "Whenever a target makes an attack roll or a saving throw before the spell ends"));
        MechanicContext context = modifierContext(spell, source,
                new SpellTargetSubject(TargetScope.MULTIPLE_TARGET_CREATURES, null, 3),
                when, List.of(), null);
        return modifierClaim(spell, source, assembly, "bless-roll-bonus", "bless-roll-d4-bonus-v1",
                new StatisticModifierMechanic(List.of(ModifiedStatistic.ATTACK_ROLL, ModifiedStatistic.SAVING_THROW),
                        ModifierOperation.ADD, new DiceAmount(1, 4, 0)), body, context, List.of(),
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
    }

    private void bane(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                      SpellMechanicsAssembly assembly) {
        String body = "Up to three creatures of your choice that you can see within range must make Charisma "
                + "saving throws. Whenever a target that fails this saving throw makes an attack roll or a saving "
                + "throw before the spell ends, the target must roll a {@dice d4} and subtract the number rolled "
                + "from the attack roll or saving throw.";
        MechanicRecord save = assembly.attempt("bane-charisma-save-v1", MechanicFamily.SAVING_THROW_REQUIREMENT,
                () -> baneSavingThrow(spell, source, assembly, body));
        MechanicRevisionReference saveReference = save == null ? null : save.revision().reference(save.id());
        var when = List.of(new ApplicabilityEvidence(ApplicabilityCondition.TARGET_MAKES_ROLL_BEFORE_SPELL_ENDS,
                "makes an attack roll or a saving throw before the spell ends"));
        var subject = new SpellTargetSubject(TargetScope.MULTIPLE_TARGET_CREATURES, null, 3);
        MechanicContext context = modifierContext(spell, source, subject,
                when, saveReference == null ? List.of() : List.of("target that fails this saving throw"),
                saveReference);
        assembly.attempt("bane-roll-penalty-v1", MechanicFamily.MODIFIER, () ->
                modifierClaim(spell, source, assembly, "bane-roll-penalty", "bane-roll-d4-penalty-v1",
                        new StatisticModifierMechanic(List.of(ModifiedStatistic.ATTACK_ROLL,
                                ModifiedStatistic.SAVING_THROW), ModifierOperation.SUBTRACT,
                                new DiceAmount(1, 4, 0)), body, context, List.of(),
                        MechanicsCompleteness.COMPLETE_WITHIN_CLAIM));
    }

    private MechanicRecord baneSavingThrow(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                           SpellMechanicsAssembly assembly, String expectedBody) {
        exactBody(spell, source, expectedBody);
        PhbSpellMechanicsExtractor.require(spell.mechanics().savingThrows().equals(List.of("charisma")), "/savingThrow");
        TextMechanicsEvidence evidence = source.textEvidence(BODY, "must make Charisma saving throws");
        var abilityEvidence = source.fieldEvidence("/savingThrow/0");
        var targetEvidence = source.textEvidence(BODY, "Up to three creatures of your choice that you can see within range");
        return assembly.claim("bane-charisma-save", "bane-saving-throw-requirement-v1",
                new SavingThrowRequirementMechanic(new FixedAbility(Ability.CHARISMA),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)),
                new MechanicContext(new SpellTargetSubject(TargetScope.MULTIPLE_TARGET_CREATURES, null, 3), List.of()),
                List.of(PhbSpellMechanicsExtractor.association(evidence, "/payload", EvidenceRole.EFFECT_ROLE),
                        PhbSpellMechanicsExtractor.association(abilityEvidence, "/payload/ability", EvidenceRole.VALUE),
                        PhbSpellMechanicsExtractor.association(targetEvidence, "/context/subject", EvidenceRole.QUALIFIER)), false,
                MechanicsCompleteness.PARTIAL);
    }

    private MechanicRecord protectionFromEnergy(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                                  SpellMechanicsAssembly assembly) {
        String body = "For the duration, the willing creature you touch has resistance to one damage type of your "
                + "choice: acid, cold, fire, lightning, or thunder.";
        var conditions = List.of(new ApplicabilityEvidence(ApplicabilityCondition.TARGET_IS_WILLING,
                "willing creature"));
        MechanicContext context = modifierContext(spell, source,
                new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
                conditions, List.of(), null);
        return modifierClaim(spell, source, assembly, "protection-from-energy-resistance",
                "protection-from-energy-selected-resistance-v1",
                new DamageResistanceMechanic(DamageProtectionOperation.RESISTANCE,
                        new DamageTypeChoice(List.of("acid", "cold", "fire", "lightning", "thunder"), 1),
                        DamageApplicability.ANY_DAMAGE), body, context, List.of(),
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
    }

    private void stoneskin(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                           SpellMechanicsAssembly assembly) {
        String body = "This spell turns the flesh of a willing creature you touch as hard as stone. Until the spell "
                + "ends, the target has resistance to nonmagical bludgeoning, piercing, and slashing damage.";
        var conditions = List.of(new ApplicabilityEvidence(ApplicabilityCondition.TARGET_IS_WILLING,
                "willing creature"));
        MechanicContext context = modifierContext(spell, source,
                new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
                conditions, List.of(), null);
        String damageClause = "resistance to nonmagical bludgeoning, piercing, and slashing damage";
        for (String type : List.of("bludgeoning", "piercing", "slashing")) {
            assembly.attempt("stoneskin-resistance-" + type + "-v1", MechanicFamily.DAMAGE_RESISTANCE, () -> {
                exactBody(spell, source, body);
                TextMechanicsEvidence evidence = source.textEvidence(BODY, damageClause);
                return modifierClaim(spell, source, assembly, "stoneskin-resistance-" + type,
                        "stoneskin-nonmagical-resistance-v1",
                        new DamageResistanceMechanic(DamageProtectionOperation.RESISTANCE,
                                new FixedDamageType(type), DamageApplicability.NONMAGICAL_DAMAGE_ONLY), body, context,
                        List.of(PhbSpellMechanicsExtractor.association(evidence, "/payload", EvidenceRole.EFFECT_ROLE)),
                        MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
            });
        }
    }

    private MechanicContext modifierContext(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                            SubjectReference subject,
                                            List<ApplicabilityEvidence> applicability,
                                            List<String> savePhrase, MechanicRevisionReference save) {
        var bindings = new ArrayList<MechanicContextBinding>();
        if (spell.durations().size() == 1 && "timed".equals(spell.durations().getFirst().type())) {
            var duration = new DeclaredSpellDurationReference(spell.id(), 0, source.source().revision());
            var evidence = source.fieldEvidence("/duration/0");
            bindings.add(new AppliesForDuration(duration, List.of(evidence.id())));
        }
        if (spell.durations().size() == 1 && Boolean.TRUE.equals(spell.durations().getFirst().concentration())) {
            var evidence = source.fieldEvidence("/duration/0/concentration");
            bindings.add(new RequiresConcentration(List.of(evidence.id())));
        }
        if (save != null) {
            var evidence = source.textEvidence(BODY, savePhrase.getFirst());
            bindings.add(new ModifierOnSaveOutcome(save, SaveOutcome.FAILURE, List.of(evidence.id())));
        }
        for (ApplicabilityEvidence condition : applicability) {
            var evidence = source.textEvidence(BODY, condition.phrase());
            bindings.add(new AppliesWhen(condition.condition(), List.of(evidence.id())));
        }
        // The target phrase is separately associated by modifierClaim, while bindings retain their own evidence.
        return new MechanicContext(subject, bindings);
    }

    private MechanicRecord modifierClaim(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                         SpellMechanicsAssembly assembly, String slot, String pattern,
                                         com.thedndpub.data.mechanics.model.MechanicPayload payload,
                                         String body, MechanicContext context,
                                         List<MechanicEvidenceAssociation> extraAssociations,
                                         MechanicsCompleteness completeness) {
        exactBody(spell, source, body);
        TextMechanicsEvidence effectEvidence = source.textEvidence(BODY, body);
        var associations = new ArrayList<MechanicEvidenceAssociation>();
        associations.add(PhbSpellMechanicsExtractor.association(effectEvidence, "/payload", EvidenceRole.EFFECT_ROLE));
        associations.addAll(extraAssociations);
        for (int index = 0; index < context.bindings().size(); index++) {
            for (String id : bindingEvidenceIds(context.bindings().get(index))) {
                var evidence = source.evidence().stream().filter(item -> item.id().equals(id)).findFirst()
                        .orElseThrow(() -> new IllegalStateException("Missing binding evidence " + id));
                associations.add(PhbSpellMechanicsExtractor.association(evidence, "/context/bindings/" + index, EvidenceRole.QUALIFIER));
            }
        }
        if (context.subject() instanceof SpellTargetSubject) {
            TextMechanicsEvidence targetEvidence = source.textEvidence(BODY, subjectPhrase(spell.id().value()));
            associations.add(PhbSpellMechanicsExtractor.association(targetEvidence, "/context/subject", EvidenceRole.QUALIFIER));
        }
        return assembly.claim(slot, pattern, payload, context, associations, false, completeness);
    }

    private String subjectPhrase(String id) {
        return switch (id) {
            case "spell:phb:shield-of-faith" -> "creature of your choice within range";
            case "spell:phb:mage-armor" -> "touch a willing creature";
            case "spell:phb:barkskin", "spell:phb:protection-from-energy", "spell:phb:stoneskin" ->
                    "willing creature";
            case "spell:phb:longstrider" -> "touch a creature";
            case "spell:phb:bless" -> "up to three creatures of your choice within range";
            case "spell:phb:bane" -> "Up to three creatures of your choice that you can see within range";
            default -> throw new IllegalStateException("Unsupported target phrase owner " + id);
        };
    }

    private List<String> bindingEvidenceIds(MechanicContextBinding binding) {
        return switch (binding) {
            case AppliesForDuration duration -> duration.evidenceIds();
            case RequiresConcentration concentration -> concentration.evidenceIds();
            case AppliesWhen applies -> applies.evidenceIds();
            case ModifierOnSaveOutcome outcome -> outcome.evidenceIds();
            default -> throw new IllegalArgumentException("Unexpected modifier context binding");
        };
    }

    private String exactBody(SpellDefinition spell, SpellMechanicsEvidenceSource source, String expected) {
        PhbSpellMechanicsExtractor.require(spell.entries().size() == 1 && spell.entries().getFirst() instanceof TextEntryDte, "/entries");
        String text = ((TextEntryDte) spell.entries().getFirst()).text();
        PhbSpellMechanicsExtractor.require(text.equals(expected), BODY);
        source.verifyText(BODY, text);
        return text;
    }

    private record ApplicabilityEvidence(ApplicabilityCondition condition, String phrase) {
    }

}
