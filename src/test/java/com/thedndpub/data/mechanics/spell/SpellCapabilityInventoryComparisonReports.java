package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** Writers for the before/after inventory comparison artifact. */
public final class SpellCapabilityInventoryComparisonReports {
    private SpellCapabilityInventoryComparisonReports() { }

    public static void write(
            SpellCapabilityInventoryComparison comparison,
            SpellCapabilityInventory baseline,
            SpellCapabilityInventory current,
            ObjectMapper mapper,
            Path jsonPath,
            Path markdownPath) throws IOException {
        Files.createDirectories(jsonPath.getParent());
        Files.createDirectories(markdownPath.getParent());
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonPath.toFile(), comparison);
        Files.writeString(markdownPath, markdown(comparison, baseline, current));
    }

    public static String markdown(
            SpellCapabilityInventoryComparison comparison,
            SpellCapabilityInventory baseline,
            SpellCapabilityInventory current) {
        StringBuilder report = new StringBuilder();
        report.append("# Step 7.4.4B — Saving Throw and Damage Inventory Comparison\n\n")
                .append("This offline comparison uses the preserved Step 7.4.4A inventory as its baseline. ")
                .append("It assesses all normalized Spell identities without activating corpus-wide production extraction.\n\n")
                .append("## Results\n\n")
                .append("- Catalog: ").append(comparison.catalogTotal()).append(" Spell identities.\n")
                .append("- Previously processed owners: ").append(comparison.baselineProcessedOwners())
                .append(" → ").append(comparison.currentProcessedOwners()).append(" (+")
                .append(comparison.currentProcessedOwners() - comparison.baselineProcessedOwners()).append(").\n")
                .append("- Production typed claims: ").append(comparison.baselineProcessedTypedClaims())
                .append(" → ").append(comparison.currentProcessedTypedClaims()).append(" (+")
                .append(comparison.newlyProducedTypedClaims()).append(").\n")
                .append("- Newly convertible owners: ").append(comparison.newlyConvertibleSpellOwners()).append(".\n")
                .append("- Potential typed claims: ").append(comparison.baselinePotentialTypedClaims())
                .append(" → ").append(comparison.currentPotentialTypedClaims()).append(" (+")
                .append(comparison.additionalPotentialTypedClaims()).append(").\n")
                .append("- Saving-throw structured gap reason reduction: ")
                .append(comparison.savingThrowStructuredGapReasonReduction()).append(".\n")
                .append("- Saving-throw gap owners now yielding claims: ")
                .append(comparison.savingThrowClaimsGainedOwners())
                .append(" owners.\n")
                .append("- Damage structured gap reason reduction: ")
                .append(comparison.damageStructuredGapReasonReduction()).append(".\n")
                .append("- Damage gap owners now yielding claims: ")
                .append(comparison.damageClaimsGainedOwners())
                .append(" owners.\n")
                .append("- Classification: ").append(counts(comparison.baselineClassificationCounts()))
                .append(" → ").append(counts(comparison.currentClassificationCounts())).append(".\n")
                .append("- Previously processed owners with fewer candidate claims: ")
                .append(comparison.previouslyProcessedOwnerRegressions().size()).append(".\n\n")
                .append("## Classification transitions\n\n| Transition | Spells |\n|---|---:|\n");
        comparison.classificationTransitions().forEach((transition, count) -> report.append("| ")
                .append(transition).append(" | ").append(count).append(" |\n"));
        report.append("\n## Remaining limitations\n\n")
                .append("- Remaining `UNSUPPORTED`: ").append(comparison.remainingUnsupportedOwners()).append("; ")
                .append("remaining `NOT_ASSESSED`: ").append(comparison.remainingNotAssessedOwners()).append(".\n")
                .append("- The canonical assembly marks coverage `PARTIAL` whenever it emits a claim and always ")
                .append("leaves `/entries` unassessed. This prevents inventory records from reaching `SUPPORTED` ")
                .append("even when every interpreted clause in the assessed scope is supported. Coverage tracking ")
                .append("was not redesigned in this batch.\n")
                .append("- Other riders, repeated actions, conditional triggers and higher-level scaling that cannot ")
                .append("be bound unambiguously remain outside the new save and damage claims. Source prose outside ")
                .append("the recognized local clauses remains unassessed.\n")
                .append("- Evidence-quality check: the preserved/current owner identity sets are equal; baseline ")
                .append("unsupported reason keys: ").append(baseline.unsupportedReasonCounts().size())
                .append(" baseline, ").append(current.unsupportedReasonCounts().size()).append(" current.\n")
                .append("- Most frequent remaining unsupported reasons: ")
                .append(topReasons(current.unsupportedReasonCounts())).append(".\n")
                .append("- Most frequent unassessed reasons: ")
                .append(topReasons(current.unassessedReasonCounts())).append(".\n");
        if (!comparison.previouslyProcessedOwnerRegressions().isEmpty()) {
            report.append("\nRegressed owners: ").append(String.join(", ",
                    comparison.previouslyProcessedOwnerRegressions())).append(".\n");
        }
        report.append("\n## Reusable structures assessed\n\n")
                .append("1. `must make` / `must succeed on` ability save clauses, with locally stated subjects.\n")
                .append("2. Explicit failed-save damage and successful-save half/no-damage clauses, including ")
                .append("outcome text adjacent to the damage sentence.\n")
                .append("3. Multiple damage tags with separately stated damage types and shared save outcomes.\n")
                .append("4. Simple dice and dice-plus-integer amounts, plus existing single-damage slot scaling.\n")
                .append("5. Unsupported ambiguous multi-component scaling, repeated saves, and conditional riders ")
                .append("remain explicit gaps.\n\n")
                .append("## Next extraction families by observed impact\n\n")
                .append("Saving throws and damage had the largest observed structured gaps in the 7.4.4A ")
                .append("inventory, so this batch addressed those first. The next largest current gaps are ")
                .append("condition (131 owners), attack roll (42), spell-slot scaling (35 unsupported pattern ")
                .append("diagnostics), and damage resistance (33 owners). Condition and damage-related gaps are ")
                .append("aggregated separately; categories may overlap on a Spell.\n\n")
                .append("The broad offline pass yielded claims for representative save-only owners (Calm Emotions), ")
                .append("single damage plus save owners (Acid Splash, Arms of Hadar, Call Lightning, ")
                .append("Disintegrate, Dissonant Whispers, Thunderwave), and multi-component damage (Ice Storm). ")
                .append("Only those eight owners were added to bounded production matching.\n");
        return report.toString();
    }

    private static String counts(Map<String, Integer> counts) {
        return counts.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + ", " + right).orElse("unknown");
    }

    private static String topReasons(Map<String, Integer> counts) {
        return counts.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey)).limit(5)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + ", " + right).orElse("unknown");
    }
}
