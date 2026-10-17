package com.thedndpub.data.dto.encounter;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.encounter.EncounterDte;
import com.thedndpub.data.dte.encounter.EncounterRowDte;
import com.thedndpub.data.dte.encounter.EncounterTableDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class EncounterMapper {
    public EncounterDefinition map(EncounterDte source) {
        Objects.requireNonNull(source, "source");
        requireText(source.name(), "name");
        requireText(source.source(), "source");
        if (source.page() != null && source.page() < 1) {
            throw new IllegalArgumentException("Encounter page must be positive");
        }
        if (source.tables() == null || source.tables().isEmpty()) {
            throw new IllegalArgumentException("Encounter must contain at least one table");
        }
        List<EncounterTable> tables = source.tables().stream().map(EncounterMapper::mapTable).toList();
        return new EncounterDefinition(new EncounterId("encounter:" + source.source() + ":" + slug(source.name())),
                source.name(), new SourceReference(new SourceIdentity(source.source()), source.page()), tables);
    }

    private static EncounterTable mapTable(EncounterTableDte source) {
        requireText(source.diceExpression(), "diceExpression");
        if ((source.minlvl() == null) != (source.maxlvl() == null)
                || (source.minlvl() != null && source.minlvl() > source.maxlvl())) {
            throw new IllegalArgumentException("Encounter table has invalid level bounds");
        }
        if (source.table() == null || source.table().isEmpty()) {
            throw new IllegalArgumentException("Encounter table must contain rows");
        }
        List<EncounterRow> rows = source.table().stream().map(EncounterMapper::mapRow).toList();
        return new EncounterTable(source.minlvl(), source.maxlvl(), source.diceExpression(), rows,
                source.caption(), source.captionPrefix(), source.captionSuffix(),
                source.footnotes() == null ? List.of() : source.footnotes(), source.page(), source.rollAttitude());
    }

    private static EncounterRow mapRow(EncounterRowDte source) {
        if (source == null || source.min() == null || source.max() == null) {
            throw new IllegalArgumentException("Encounter row has invalid authored roll range");
        }
        // Source d100 tables encode the terminal 00 result as a maximum of zero.
        boolean terminalZeroRange = source.min() > source.max() && source.max() == 0;
        if (source.min() > source.max() && !terminalZeroRange) {
            throw new IllegalArgumentException("Encounter row has invalid authored roll range");
        }
        requireText(source.result(), "result");
        return new EncounterRow(source.min(), source.max(), source.result(), source.resultAttitude());
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Encounter " + field + " must not be blank");
        }
    }

    private static String slug(String value) {
        String slug = Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Encounter name cannot produce a stable ID: " + value);
        }
        return slug;
    }
}

