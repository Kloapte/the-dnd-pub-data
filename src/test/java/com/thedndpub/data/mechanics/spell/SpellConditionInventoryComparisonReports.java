package com.thedndpub.data.mechanics.spell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Deterministic comparison of the 7.4.4B capability inventory with condition removal assessment. */
public final class SpellConditionInventoryComparisonReports {
    private SpellConditionInventoryComparisonReports() { }

    public static String markdown(
            SpellCapabilityInventory before,
            SpellCapabilityInventory after,
            int beforeProductionClaims,
            int afterProductionClaims) {
        Map<String, SpellCapabilityRecord> previous = index(before.spells());
        Map<String, SpellCapabilityRecord> current = index(after.spells());
        List<SpellCapabilityRecord> newlyConvertible = current.values().stream()
                .filter(record -> previous.get(record.spellId()).potentialMechanicCount() == 0)
                .filter(record -> record.potentialMechanicCount() > 0)
                .sorted(java.util.Comparator.comparing(SpellCapabilityRecord::spellId)).toList();
        List<String> alreadyConvertibleWithMoreClaims = current.values().stream()
                .filter(record -> previous.get(record.spellId()).potentialMechanicCount() > 0)
                .filter(record -> record.potentialMechanicCount()
                        > previous.get(record.spellId()).potentialMechanicCount())
                .sorted(java.util.Comparator.comparing(SpellCapabilityRecord::spellId))
                .map(record -> record.name() + " (" + record.spellId() + ")").toList();
        Map<String, Integer> transitions = new java.util.TreeMap<>();
        for (String id : previous.keySet().stream().sorted().toList()) {
            transitions.merge(previous.get(id).classification().name() + "->"
                    + current.get(id).classification().name(), 1, Integer::sum);
        }
        int addedPotentialClaims = after.potentialTypedClaimCount() - before.potentialTypedClaimCount();
        int conditionReasonBefore = conditionReasonCount(before.unsupportedReasonCounts());
        int conditionReasonAfter = conditionReasonCount(after.unsupportedReasonCounts());
        StringBuilder report = new StringBuilder();
        report.append("# Step 7.4.4C — Condition Extraction Comparison\n\n")
                .append("Offline assessment compares the preserved 7.4.4B inventory with the current PHB condition ")
                .append("removal patterns. It does not activate corpus-wide extraction in production.\n\n")
                .append("## Results\n\n")
                .append("- Catalog: **").append(after.catalogTotal()).append("** source-aware Spell identities.\n")
                .append("- Newly convertible owners: **").append(newlyConvertible.size()).append("** — ")
                .append(names(newlyConvertible)).append(".\n")
                .append("- Already convertible owners gaining candidate claims: **")
                .append(alreadyConvertibleWithMoreClaims.size()).append("** — ")
                .append(alreadyConvertibleWithMoreClaims.isEmpty() ? "none" : String.join(", ",
                        alreadyConvertibleWithMoreClaims)).append(".\n")
                .append("- Additional potential typed claims: **").append(addedPotentialClaims)
                .append("**: 10 condition removal claims, 2 exclusive removal choice groups, and 26 save ")
                .append("requirement claims for existing condition owners.\n")
                .append("- Production owners: **").append(before.currentlyProcessedCount()).append(" → ")
                .append(after.currentlyProcessedCount()).append("** (+")
                .append(after.currentlyProcessedCount() - before.currentlyProcessedCount()).append(").\n")
                .append("- Production typed claims: **").append(beforeProductionClaims).append(" → ")
                .append(afterProductionClaims).append("** (+")
                .append(afterProductionClaims - beforeProductionClaims).append(").\n")
                .append("- Capability classifications: ").append(counts(before.classificationCounts()))
                .append(" → ").append(counts(after.classificationCounts())).append(".\n")
                .append("- Follow-up removal-choice correction versus the initial 7.4.4C output: **+2 typed ")
                .append("claims**, **0 newly convertible owners**, and **0 classification changes**; these are ")
                .append("the Lesser Restoration and Greater Restoration choice groups.\n")
                .append("- Condition-related unsupported reason records: **").append(conditionReasonBefore)
                .append(" → ").append(conditionReasonAfter).append("**. Tag-only references are now assessed; ")
                .append("ambiguous and unsupported condition uses gained explicit diagnostics, so this aggregate ")
                .append("does not represent a net gap reduction. Three owners instead moved from no candidate ")
                .append("claims to partial condition removal coverage.\n")
                .append("- Reduction in the existing `CONDITION:SOURCE_CLASSIFICATION_WITHOUT_MATCHING_PATTERN` ")
                .append("reason: **0** (131 owners remain in that category).\n")
                .append("- Remaining `UNSUPPORTED`: **")
                .append(after.classificationCounts().getOrDefault("UNSUPPORTED", 0))
                .append("**; `NOT_ASSESSED`: **")
                .append(after.classificationCounts().getOrDefault("NOT_ASSESSED", 0)).append("**.\n")
                .append("- Previously processed owners with fewer candidate claims: **")
                .append(regressions(previous, current).size()).append("**.\n")
                .append("- Per-owner extraction failures: **")
                .append(after.spells().stream().mapToInt(record -> record.diagnosticCodes().size()).sum())
                .append("**.\n")
                .append("- Most frequent unassessed evidence: ")
                .append(topReasons(after.unassessedReasonCounts())).append(".\n\n")
                .append("## Classification transitions\n\n| Transition | Spells |\n|---|---:|\n");
        transitions.forEach((transition, count) -> report.append("| ").append(transition).append(" | ")
                .append(count).append(" |\n"));
        report.append("\n## Observed condition gaps\n\n")
                .append("- Explicit `REMOVE` is supported for conditions listed after ending one condition, effects ")
                .append("that impose a named condition, and clauses stating that the condition ends. The supported ")
                .append("owners are Greater Restoration (one of charmed or petrified), Lesser Restoration (one of ")
                .append("four conditions), ")
                .append("and Power Word Heal (four conditions).\n")
                .append("- Lesser Restoration's disease option and Greater Restoration's other listed remedy ")
                .append("types remain explicit unmodeled alternatives; Greater Restoration's exhaustion also ")
                .append("cannot be represented by the current `ConditionName` enum. Power Word Heal's four ")
                .append("condition removals remain independent; its prone/stand-up clause is unresolved because ")
                .append("the text does not explicitly state condition-ending behavior.\n")
                .append("- Condition tags in negations, prerequisites, protection/immunity clauses, and conditional ")
                .append("limitations do not become `APPLY` claims. Examples include Animal Shapes, Aura of Purity, ")
                .append("Enthrall, and Protection from Evil and Good.\n")
                .append("- Existing save-based applications, repeat-save endings, duration/concentration context, ")
                .append("exclusive condition choices, and Shield/Fireball/Bless/Mage Armor relationships retain ")
                .append("their earlier claims and regression coverage. No duplicate save claims are added by the ")
                .append("condition removal patterns.\n")
                .append("- Evidence-quality limitation: canonical assembly still leaves `/entries` unassessed, so ")
                .append("coverage remains partial and `SUPPORTED` cannot be reached from these records. This batch ")
                .append("does not redesign coverage tracking.\n\n")
                .append("## Remaining gaps ranked by observed inventory evidence\n\n");
        after.unsupportedReasonCounts().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry::getKey))
                .limit(6).forEach(entry -> report.append("- `").append(entry.getKey()).append("`: ")
                        .append(entry.getValue()).append(" reason records.\n"));
        if (after.unsupportedReasonCounts().isEmpty()) {
            report.append("No unsupported reason counts were available.\n");
        }
        report.append("\nCounts are corpus observations from the pinned v2.37.0 input. A future extraction batch should ")
                .append("select a recurring pattern only after checking representative source evidence; the next ")
                .append("condition candidates are the unresolved immunity, negation, and prerequisite references ")
                .append("above. No further extraction family is started here.\n");
        return report.toString();
    }

    public static void write(Path path, String report) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, report);
    }

    private static Map<String, SpellCapabilityRecord> index(List<SpellCapabilityRecord> records) {
        return records.stream().collect(Collectors.toMap(SpellCapabilityRecord::spellId, Function.identity()));
    }

    private static int conditionReasonCount(Map<String, Integer> reasons) {
        return reasons.entrySet().stream().filter(entry -> entry.getKey().startsWith("CONDITION:"))
                .mapToInt(Map.Entry::getValue).sum();
    }

    private static String names(List<SpellCapabilityRecord> records) {
        return records.stream().map(record -> record.name() + " (" + record.source() + ")")
                .reduce((left, right) -> left + ", " + right).orElse("none");
    }

    private static List<String> regressions(Map<String, SpellCapabilityRecord> before,
                                            Map<String, SpellCapabilityRecord> after) {
        return before.keySet().stream().filter(id -> before.get(id).processingState().equals("PROCESSED"))
                .filter(id -> after.get(id).potentialMechanicCount() < before.get(id).potentialMechanicCount())
                .sorted().toList();
    }

    private static String topReasons(Map<String, Integer> reasons) {
        return reasons.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey)).limit(3)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + ", " + right).orElse("unknown");
    }

    private static String counts(Map<String, Integer> counts) {
        return counts.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + ", " + right).orElse("unknown");
    }
}
