package com.thedndpub.data.dto.nametable;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.nametable.NameTableDte;
import com.thedndpub.data.dte.nametable.NameTableOptionDte;
import com.thedndpub.data.dte.nametable.NameTableRowDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class NameTableMapper {
    public NameTableDefinition map(NameTableDte source) {
        Objects.requireNonNull(source, "source");
        requireValue(source.name(), "name");
        requireValue(source.source(), "source");
        if (source.page() == null || source.page() < 1) {
            throw new IllegalArgumentException("Name table page must be a positive integer");
        }
        if (source.tables() == null || source.tables().isEmpty()) {
            throw new IllegalArgumentException("Name table must contain at least one option table");
        }

        List<NameTableOption> options = source.tables().stream()
                .map(NameTableMapper::mapOption)
                .toList();
        return new NameTableDefinition(
                new NameTableId("name-table:" + slug(source.source()) + ":" + slug(source.name())),
                source.name(),
                new SourceReference(new SourceIdentity(source.source()), source.page()),
                options);
    }

    private static NameTableOption mapOption(NameTableOptionDte source) {
        requireValue(source.option(), "option");
        requireValue(source.diceExpression(), "diceExpression");
        if (source.table() == null || source.table().isEmpty()) {
            throw new IllegalArgumentException("Name table option must contain at least one row");
        }
        return new NameTableOption(
                source.option(),
                source.diceExpression(),
                source.table().stream().map(NameTableMapper::mapRow).toList());
    }

    private static NameTableRow mapRow(NameTableRowDte source) {
        if (source.min() == null || source.max() == null) {
            throw new IllegalArgumentException("Name table row requires source-authored min and max values");
        }
        boolean authoredTerminalSentinel = source.min() == 99 && source.max() == 0;
        if (source.min() > source.max() && !authoredTerminalSentinel) {
            throw new IllegalArgumentException("Name table row has an unsupported reversed range");
        }
        requireValue(source.result(), "result");
        return new NameTableRow(source.min(), source.max(), source.result());
    }

    private static void requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name table " + field + " is required");
        }
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a stable name table ID from '" + value + "'");
        }
        return normalized;
    }
}
