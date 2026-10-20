package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** Deterministic serializers for the offline Step 7.4.4A evidence artifacts. */
public final class SpellCapabilityInventoryReports {
    private SpellCapabilityInventoryReports() { }

    public static void write(SpellCapabilityInventory inventory, ObjectMapper mapper, Path json, Path markdown)
            throws IOException {
        Files.createDirectories(json.getParent());
        Files.createDirectories(markdown.getParent());
        mapper.writerWithDefaultPrettyPrinter().writeValue(json.toFile(), inventory);
        Files.writeString(markdown, markdown(inventory));
    }

    public static String markdown(SpellCapabilityInventory inventory) {
        StringBuilder report = new StringBuilder();
        report.append("# Step 7.4.4A — Spell Capability Inventory\n\n")
                .append("Dataset: ").append(inventory.datasetVersion()).append(". This is an offline ")
                .append("assessment using the existing Spell pattern registry. It does not change production ")
                .append("processing, review, licensing, or publication state.\n\n")
                .append("## Catalog\n\nTotal normalized Spell identities: **")
                .append(inventory.catalogTotal()).append("**.\n\nSource distribution:\n\n");
        inventory.sourceCounts().forEach((source, count) -> report.append("- ").append(source).append(": ")
                .append(count).append('\n'));
        report.append("\n## Capability classification\n\n| Classification | Count | Percent |\n| --- | ---: | ---: |\n");
        for (SpellCapabilityClassification classification : SpellCapabilityClassification.values()) {
            int count = inventory.classificationCounts().getOrDefault(classification.name(), 0);
            report.append("| ").append(classification).append(" | ").append(count).append(" | ")
                    .append(percent(count, inventory.catalogTotal())).append(" |\n");
        }
        report.append("\nEach Spell record keeps extraction readiness, current `processingState` and ")
                .append("`currentCoverageStatus` separate. Candidate output has its own coverage status and ")
                .append("a per-claim completeness count.\n\n");
        report.append("\nCurrently processed: **").append(inventory.currentlyProcessedCount())
                .append("**. Newly convertible if attempted: **").append(inventory.newlyConvertibleCount())
                .append("**. Potential typed claims from the current patterns: **")
                .append(inventory.potentialTypedClaimCount()).append("**.\n\n")
                .append("## Mechanic-family coverage\n\nCounts are Spell owners with at least one produced claim in the family.\n\n");
        inventory.mechanicFamilyCounts().forEach((family, count) -> report.append("- ").append(family)
                .append(": ").append(count).append(" Spell owners\n"));
        report.append("\n## Unsupported and unassessed reasons\n\nMost frequent detected unsupported reasons:\n\n");
        appendTop(report, inventory.unsupportedReasonCounts());
        report.append("\nMost frequent unassessed reasons:\n\n");
        appendTop(report, inventory.unassessedReasonCounts());
        report.append("\n## Representative Spells\n\n");
        for (SpellCapabilityClassification classification : SpellCapabilityClassification.values()) {
            report.append("### ").append(classification).append("\n\n");
            List<SpellCapabilityRecord> examples = inventory.spells().stream()
                    .filter(record -> record.classification() == classification).limit(5).toList();
            if (examples.isEmpty()) report.append("No Spells in this classification.\n\n");
            else for (SpellCapabilityRecord spell : examples) report.append("- ").append(spell.name())
                    .append(" (").append(spell.source()).append(", `").append(spell.spellId()).append("`)")
                    .append(spell.patternMatched() ? "; " + spell.potentialMechanicCount() + " claims" : "")
                    .append('\n');
            report.append('\n');
        }
        report.append("## Extraction failures and evidence quality\n\n");
        List<SpellCapabilityRecord> failures = inventory.spells().stream()
                .filter(record -> !record.diagnosticCodes().isEmpty()).toList();
        report.append("Per-Spell extraction failures: **").append(failures.size()).append("**.\n\n");
        if (failures.isEmpty()) report.append("No extraction failures were recorded.\n\n");
        else failures.forEach(spell -> report.append("- `").append(spell.spellId()).append("`: ")
                .append(String.join(", ", spell.diagnosticCodes())).append('\n'));
        report.append("A missing pattern match leaves source prose unassessed unless a source-authored structured ")
                .append("mechanic classification identifies a specific unsupported family. A claim is never ")
                .append("considered complete solely because one or more claims were produced.\n\n")
                .append("## Recommended next extraction families by observed impact\n\n");
        Map<String, Integer> gaps = new java.util.TreeMap<>();
        inventory.unsupportedReasonCounts().forEach((reason, count) -> {
            int colon = reason.indexOf(':');
            if (colon > 0) gaps.merge(reason.substring(0, colon), count, Integer::sum);
        });
        int rank = 1;
        for (var gap : gaps.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue()
                .reversed().thenComparing(Map.Entry::getKey)).toList()) {
            report.append(rank++).append(". **").append(gap.getKey()).append("** — ")
                    .append(gap.getValue()).append(" affected Spell owners in observed structured or extraction diagnostics.\n");
        }
        if (gaps.isEmpty()) report.append("No unsupported families were evidenced by structured source tags.\n");
        report.append("\nCounts describe distinct Spell owners per family where stated; reason frequencies count ")
                .append("distinct per-Spell reason codes. Unsupported family detection uses source-authored ")
                .append("structured classifications only; unmatched narrative remains unassessed.\n");
        return report.toString();
    }

    private static String percent(int count, int total) {
        return total == 0 ? "unknown" : String.format(java.util.Locale.ROOT, "%.1f%%", count * 100.0 / total);
    }

    private static void appendTop(StringBuilder report, Map<String, Integer> counts) {
        List<Map.Entry<String, Integer>> top = counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry::getKey))
                .limit(10).toList();
        if (top.isEmpty()) report.append("- None detected\n");
        else top.forEach(entry -> report.append("- ").append(entry.getKey()).append(": ")
                .append(entry.getValue()).append('\n'));
    }
}
