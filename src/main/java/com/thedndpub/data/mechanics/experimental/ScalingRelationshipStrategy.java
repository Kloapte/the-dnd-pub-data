package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.GenericScalingMechanic;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.ScalingQuantity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Links authored scaling progressions to mechanics only where their source wording identifies the quantity. */
public final class ScalingRelationshipStrategy implements MechanicRelationshipStrategy {
    @Override public String strategyId() { return "scaling-applies-to"; }
    @Override public String version() { return "1"; }
    @Override public Set<MechanicRelationshipType> supportedTypes() {
        return Set.of(MechanicRelationshipType.SCALING_APPLIES_TO);
    }

    @Override
    public RelationshipResolutionResult resolve(List<ExtractedMechanic> mechanics,
            MechanicInterpretationContext context) {
        List<ExtractedMechanic> scaling = mechanics.stream()
                .filter(item -> item.payload() instanceof GenericScalingMechanic)
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();
        List<ExtractedMechanic> targets = mechanics.stream()
                .filter(item -> item.payload() instanceof DamageMechanic || item.payload() instanceof HealingMechanic
                        || item.payload() instanceof AttackRollMechanic)
                .sorted(Comparator.comparing(ExtractedMechanic::id)).toList();
        List<ExperimentalMechanicRelationshipDto> relationships = new ArrayList<>();
        List<ExperimentalDiagnosticDto> diagnostics = new ArrayList<>();

        for (ExtractedMechanic source : scaling) {
            GenericScalingMechanic rule = (GenericScalingMechanic) source.payload();
            SourceFragment sourceFragment = context.fragments().get(source.sourceFragmentId());
            List<ExtractedMechanic> candidates = candidates(rule.quantity(), sourceFragment, targets, context);
            String code = candidates.isEmpty() ? "SCALING_AFFECTED_MECHANIC_UNRESOLVED"
                    : candidates.size() > 1 ? "SCALING_AFFECTED_MECHANIC_AMBIGUOUS" : null;
            String evidenceId = context.evidenceIdsByFragment().get(source.sourceFragmentId());
            if (code != null || evidenceId == null) {
                diagnostics.add(new ExperimentalDiagnosticDto(code == null ? "SCALING_RELATIONSHIP_EVIDENCE_MISSING" : code,
                        "WARNING", source.id(), sourceFragment == null ? null : sourceFragment.pointer(),
                        code != null ? "The authored scaling quantity does not identify exactly one extracted mechanic."
                                : "Scaling mechanic has no source evidence available for its relationship."));
                continue;
            }
            ExtractedMechanic target = candidates.getFirst();
            String id = "rel-" + MechanicsHashes.sha256(context.ownerId().value() + "|"
                    + context.sourceRevision().resourceSha256() + "|SCALING_APPLIES_TO|" + source.id()
                    + "|" + target.id() + "|" + rule.quantity()).substring(0, 20);
            relationships.add(new ExperimentalMechanicRelationshipDto(id,
                    MechanicRelationshipType.SCALING_APPLIES_TO, context.ownerId(), context.sourceRevision(),
                    source.id(), target.id(), List.of(), List.of(evidenceId), List.of(),
                    new ScalingRelationshipRule(rule.quantity())));
        }
        return new RelationshipResolutionResult(relationships.stream()
                .sorted(Comparator.comparing(ExperimentalMechanicRelationshipDto::id)).toList(), diagnostics);
    }

    private static List<ExtractedMechanic> candidates(ScalingQuantity quantity, SourceFragment scalingFragment,
            List<ExtractedMechanic> mechanics, MechanicInterpretationContext context) {
        String source = scalingFragment == null ? "" : scalingFragment.text().toLowerCase(Locale.ROOT);
        List<ExtractedMechanic> compatible = mechanics.stream().filter(item -> switch (quantity) {
            case DAMAGE_AMOUNT -> item.payload() instanceof DamageMechanic;
            case HEALING_AMOUNT -> item.payload() instanceof HealingMechanic;
            case ATTACK_COUNT -> item.payload() instanceof AttackRollMechanic;
            case PROJECTILE_COUNT -> item.payload() instanceof AttackRollMechanic || item.payload() instanceof DamageMechanic;
            case TARGET_COUNT -> item.payload() instanceof AttackRollMechanic || item.payload() instanceof DamageMechanic
                    || item.payload() instanceof HealingMechanic;
            case UNSPECIFIED_NUMERIC_QUANTITY -> false;
        }).toList();

        if (quantity == ScalingQuantity.DAMAGE_AMOUNT && source.matches("(?s).*\\b(acid|cold|fire|force|lightning|"
                + "necrotic|poison|psychic|radiant|thunder|bludgeoning|piercing|slashing) damage\\b.*")) {
            String type = source.replaceAll("(?s).*\\b(acid|cold|fire|force|lightning|necrotic|poison|psychic|"
                    + "radiant|thunder|bludgeoning|piercing|slashing) damage\\b.*", "$1");
            return compatible.stream().filter(item -> item.payload() instanceof DamageMechanic damage
                    && damage.damageType() instanceof FixedDamageType fixed
                    && fixed.authoredCode().equalsIgnoreCase(type)).toList();
        }
        if (quantity == ScalingQuantity.PROJECTILE_COUNT) {
            boolean attacksPerProjectile = context.fragments().values().stream().map(SourceFragment::text)
                    .map(text -> text.toLowerCase(Locale.ROOT)).anyMatch(text -> text.matches(
                            "(?s).*\\b(?:separate )?(?:attack roll|spell attack) for each "
                                    + "(?:ray|beam|dart|missile|projectile).*"));
            if (attacksPerProjectile) {
                List<ExtractedMechanic> attackTargets = compatible.stream()
                        .filter(item -> item.payload() instanceof AttackRollMechanic).toList();
                if (!attackTargets.isEmpty()) return attackTargets;
            }
            if (source.matches("(?s).*\\b(?:dart|missile)\\b.*") || compatible.stream()
                    .anyMatch(item -> item.payload() instanceof DamageMechanic)) {
                List<ExtractedMechanic> damageTargets = compatible.stream()
                        .filter(item -> item.payload() instanceof DamageMechanic).toList();
                if (!damageTargets.isEmpty()) return damageTargets;
            }
        }
        if (quantity == ScalingQuantity.TARGET_COUNT) {
            boolean explicitIndependentTargets = source.matches("(?s).*\\b(?:target|creature)s?\\b.*");
            if (!explicitIndependentTargets) return List.of();
        }
        return compatible;
    }
}
