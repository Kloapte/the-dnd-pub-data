package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.AppliesOnEachAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollInstanceScope;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.InstanceTargetAllocation;
import com.thedndpub.data.mechanics.model.InstanceTargetRequirement;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SelectedTargetCount;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetCategory;
import com.thedndpub.data.mechanics.model.TargetCategoryEligibilityStatus;
import com.thedndpub.data.mechanics.model.TargetReusePolicy;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TargetSelectionCardinality;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.syntax.MarkupTag;

import java.util.List;

/** Source-bounded XGE interpretation for the Steel Wind Strike selected-target attack pattern. */
final class XgeSteelWindStrikeRules implements SpellMechanicsPattern {
    static final String OWNER_ID = "spell:xge:steel-wind-strike";
    private static final String CHOOSE_TARGETS = "Choose up to five creatures you can see within range.";
    private static final String ATTACK_TARGETS = "Make a melee spell attack against each target.";
    private static final String HIT_DAMAGE = "On a hit, a target takes {@damage 6d10} force damage.";
    private static final String TELEPORT = "You can then teleport to an unoccupied space you can see within 5 feet "
            + "of one of the targets you hit or missed.";

    @Override
    public String patternId() {
        return "xge-steel-wind-strike-selected-target-attacks-v1";
    }

    @Override
    public boolean supports(SpellDefinition spell) {
        return OWNER_ID.equals(spell.id().value())
                && "XGE".equals(spell.source().identity().code());
    }

    @Override
    public MechanicsDocument extract(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        ModelChecks.require(supports(spell), "Unsupported XGE Steel Wind Strike owner");
        ModelChecks.require(original.owner().equals(source.owner())
                        && OWNER_ID.equals(original.owner().id())
                        && original.sources().equals(List.of(source.source().revision())),
                "Steel Wind Strike source/document mismatch");
        ModelChecks.require(original.processing().state()
                        == com.thedndpub.data.mechanics.model.ProcessingState.NOT_ATTEMPTED,
                "Steel Wind Strike extraction requires an unattempted owner");
        ModelChecks.require(spell.entries().size() == 2
                        && spell.entries().get(0) instanceof TextEntryDte first
                        && first.text().contains(CHOOSE_TARGETS + " " + ATTACK_TARGETS + " " + HIT_DAMAGE)
                        && spell.entries().get(1) instanceof TextEntryDte second
                        && second.text().equals(TELEPORT),
                "Steel Wind Strike authored text differs from the bounded source pattern");
        ModelChecks.require(spell.mechanics().spellAttacks().equals(List.of("M"))
                        && spell.mechanics().damageInflicted().equals(List.of("force")),
                "Steel Wind Strike structured mechanics differ from the reviewed pattern");
        source.verifyTextList("/spellAttack", spell.mechanics().spellAttacks());
        source.verifyTextList("/damageInflict", spell.mechanics().damageInflicted());

        var chooser = source.textEvidence("/entries/0", CHOOSE_TARGETS);
        var attackText = source.textEvidence("/entries/0", ATTACK_TARGETS);
        var casterText = source.textEvidence("/entries/0", "You flourish the weapon used in the casting");
        var damageEffect = source.textEvidence("/entries/0", HIT_DAMAGE);
        var hitText = source.textEvidence("/entries/0", "On a hit");
        TextMechanicsEvidence teleportText = source.textEvidence("/entries/1", TELEPORT);
        var attackKind = source.fieldEvidence("/spellAttack/0");
        var damageType = source.fieldEvidence("/damageInflict/0");
        var castTime = source.fieldEvidence("/time/0");
        var range = source.fieldEvidence("/range");
        MarkupTag damageFormula = PhbSpellMechanicsExtractor.singleFormula(source, "/entries/0", "damage");
        TextMechanicsEvidence formulaText = source.textEvidence("/entries/0", damageFormula.originalText());

        SpellTargetSubject target = new SpellTargetSubject(TargetScope.MULTIPLE_TARGET_CREATURES,
                null, 5);
        InstanceTargetAllocation allocation = new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                List.of(TargetCategory.CREATURE),
                TargetReusePolicy.DISTINCT_TARGETS_ONLY,
                true,
                TargetCategoryEligibilityStatus.SOURCE_SPECIFIED,
                List.of(InstanceTargetRequirement.VISIBLE_TO_CASTER));
        var assembly = new SpellMechanicsAssembly(source, original, "xge-steel-wind-strike");
        MechanicRecord attack = assembly.attempt("steel-wind-strike-attack-roll-v1", MechanicFamily.ATTACK_ROLL,
                () -> assembly.claim("attack-roll", "steel-wind-strike-attack-roll-v1",
                        new AttackRollMechanic(AttackKind.MELEE_SPELL, AttackActorRole.CASTER, null, true,
                                AttackActivationTiming.CAST_TIME, null, allocation, new SelectedTargetCount(5)),
                        new MechanicContext(target, List.of()),
                        List.of(
                                MechanicEvidenceAssociation.of(attackText, "/payload", EvidenceRole.EFFECT_ROLE),
                                MechanicEvidenceAssociation.of(attackKind, "/payload/attackKind", EvidenceRole.VALUE),
                                MechanicEvidenceAssociation.of(attackText,
                                        "/payload/attackKind", EvidenceRole.VALUE),
                                MechanicEvidenceAssociation.of(casterText,
                                        "/payload/actorRole", EvidenceRole.SUBJECT),
                                MechanicEvidenceAssociation.of(chooser,
                                        "/payload/selectedTargetCount/maximumSelectedTargets", EvidenceRole.VALUE),
                                MechanicEvidenceAssociation.of(attackText,
                                        "/payload/selectedTargetCount", EvidenceRole.TRIGGER),
                                MechanicEvidenceAssociation.of(attackText,
                                        "/payload/attacksIndependent", EvidenceRole.VALUE),
                                MechanicEvidenceAssociation.of(attackText,
                                        "/payload/targetAllocation/cardinality", EvidenceRole.VALUE),
                                MechanicEvidenceAssociation.of(chooser,
                                        "/payload/targetAllocation/eligibleTargetCategories/0", EvidenceRole.SUBJECT),
                                MechanicEvidenceAssociation.of(chooser,
                                        "/payload/targetAllocation/targetReusePolicy", EvidenceRole.QUALIFIER),
                                MechanicEvidenceAssociation.of(chooser,
                                        "/payload/targetAllocation/withinSpellRange", EvidenceRole.QUALIFIER),
                                MechanicEvidenceAssociation.of(chooser,
                                        "/payload/targetAllocation/requirements/0", EvidenceRole.QUALIFIER),
                                MechanicEvidenceAssociation.of(castTime,
                                        "/payload/activationTiming", EvidenceRole.VALUE),
                                MechanicEvidenceAssociation.of(range,
                                        "/payload/targetAllocation/withinSpellRange", EvidenceRole.QUALIFIER),
                                MechanicEvidenceAssociation.of(chooser, "/context/subject", EvidenceRole.SUBJECT)),
                        false));

        assembly.attempt("steel-wind-strike-hit-damage-v1", MechanicFamily.DAMAGE, () -> {
            ModelChecks.require(attack != null, "/entries/0");
            var hitBinding = new AppliesOnEachAttackRollOutcome(attack.revision().reference(attack.id()),
                    AttackRollOutcome.HIT, AttackRollInstanceScope.CORRESPONDING_ATTACK_INSTANCE,
                    List.of(hitText.id()));
            return assembly.claim("damage", "steel-wind-strike-hit-damage-v1",
                    new DamageMechanic(PhbSpellMechanicsExtractor.dice(damageFormula, "/entries/0"),
                            new FixedDamageType("force")),
                    new MechanicContext(target, List.of(hitBinding)),
                    List.of(
                            MechanicEvidenceAssociation.of(damageEffect, "/payload", EvidenceRole.EFFECT_ROLE),
                            MechanicEvidenceAssociation.of(formulaText, "/payload/amount", EvidenceRole.VALUE),
                            MechanicEvidenceAssociation.of(damageType, "/payload/damageType", EvidenceRole.VALUE),
                            MechanicEvidenceAssociation.of(hitText, "/context/bindings/0", EvidenceRole.TRIGGER),
                            MechanicEvidenceAssociation.of(damageEffect, "/context/subject", EvidenceRole.SUBJECT)),
                    false);
        });

        assembly.unsupported("steel-wind-strike-teleportation", MechanicFamily.UNDETERMINED,
                UnsupportedReason.FAMILY_NOT_IMPLEMENTED, List.of(teleportText.id()));
        String root = source.source().jsonPointer();
        return assembly.finish(List.of(root + "/spellAttack", root + "/damageInflict", root + "/time",
                        root + "/range", root + "/entries/0", root + "/entries/1"),
                List.of());
    }
}
