package com.thedndpub.data.dto.variantrule;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.variantrule.VariantRuleDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

@Component
public final class VariantRuleMapper {
    public VariantRuleDefinition map(VariantRuleDte source) {
        Objects.requireNonNull(source, "source");
        if (source.name() == null || source.name().isBlank() || source.source() == null || source.source().isBlank()) {
            throw new IllegalArgumentException("Variant Rule requires authored name and source");
        }
        String slug = slug(source.name());
        if (slug.isBlank()) {
            throw new IllegalArgumentException("Variant Rule name has no stable slug: " + source.name());
        }
        VariantRuleStructureCounts counts = VariantRuleStructureCounter.count(source.entries());
        return new VariantRuleDefinition(
                new VariantRuleId("variant-rule:" + source.source() + ":" + slug),
                source.name(),
                new SourceReference(source.source(), source.page()),
                source.ruleType(),
                source.type(),
                source.srd(),
                source.srd52(),
                source.basicRules2024(),
                source.reprintedAs(),
                source.additionalSources(),
                source.entries(),
                counts.sections(),
                counts.tables());
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
        return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
