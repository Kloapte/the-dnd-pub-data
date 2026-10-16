package com.thedndpub.data.dto.action;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.action.ActionDte;
import com.thedndpub.data.dte.action.ActionReprintDte;
import com.thedndpub.data.dte.action.ActionTimeDte;
import com.thedndpub.data.dte.action.ActionTimeSpecialDte;
import com.thedndpub.data.dte.action.ActionTimeUnitDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class ActionMapper {
    public ActionImportRecord mapRecord(ActionDte source) {
        Objects.requireNonNull(source, "source");
        if (source.name() == null || source.name().isBlank()
                || source.source() == null || source.source().isBlank()) {
            throw new IllegalArgumentException("Action name and source are required");
        }

        ActionDefinition definition = new ActionDefinition(
                new ActionId("action:" + slug(source.source()) + ":" + slug(source.name())),
                source.name(),
                new SourceReference(source.source(), source.page()),
                timings(source.time()),
                reprints(source.reprintedAs()));
        ActionImportProvenance provenance = new ActionImportProvenance(
                values(source.entries()),
                values(source.seeAlsoAction()),
                source.fromVariant(),
                source.srd(),
                source.basicRules(),
                source.srd52(),
                source.basicRules2024());
        return new ActionImportRecord(definition, provenance);
    }

    private static List<ActionTiming> timings(List<ActionTimeDte> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream().map(ActionMapper::timing).toList();
    }

    private static ActionTiming timing(ActionTimeDte timing) {
        if (timing instanceof ActionTimeUnitDte unit) {
            return new ActionTiming(unit.number(), unit.unit(), null);
        }
        if (timing instanceof ActionTimeSpecialDte special) {
            return new ActionTiming(null, null, special.value());
        }
        throw new IllegalArgumentException("Unsupported action timing type: " + timing.getClass().getName());
    }

    private static List<ActionReference> reprints(List<ActionReprintDte> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream().map(ActionMapper::reprint).toList();
    }

    private static ActionReference reprint(ActionReprintDte value) {
        String[] parts = value.uid().split("\\|", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException("Invalid action reprint reference: " + value.uid());
        }
        return new ActionReference(parts[0], new SourceIdentity(parts[1]), value.tag());
    }

    private static <T> List<T> values(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a stable action ID from '" + value + "'");
        }
        return normalized;
    }
}
