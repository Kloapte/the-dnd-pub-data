package com.thedndpub.data.services;

import com.thedndpub.data.dto.cultboon.BoonDefinition;
import com.thedndpub.data.dto.cultboon.CultDefinition;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.EntryTableCellDte;
import com.thedndpub.data.dte.common.entry.InsetEntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.QuoteEntryDte;
import com.thedndpub.data.dte.common.entry.RollTableCellDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.StructuredTableCellDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.common.entry.TextTableCellDte;
import com.thedndpub.data.dto.cultboon.CultImportRecord;
import com.thedndpub.data.dto.cultboon.BoonImportRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Validates source identities in typed root metadata and explicit inline references. */
@Component
public final class CultsAndBoonsSourceValidator {
    private static final Pattern INLINE_TAG = Pattern.compile("\\{@([a-zA-Z][\\w-]*)\\s+([^}]*)}");

    private final SourceRegistry sourceRegistry;

    public CultsAndBoonsSourceValidator(SourceRegistry sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
    }

    public void validate(CultImportRecord imported) {
        CultDefinition definition = imported.definition();
        requireSource(definition.source().identity().code(), "Cult primary source");
        definition.otherSources().forEach(source -> requireSource(
                source.identity().code(), "Cult secondary source"));
        definition.reprintedAs().forEach(reprint -> requireSource(
                reprint.source().code(), "Cult reprint source"));
        validateEntries(imported.provenance().entries(), "cult", definition.id().value());
        validateText(Arrays.asList(definition.goalEntry(), definition.cultistsEntry(), definition.signatureSpellsEntry()),
                "cult", definition.id().value());
    }

    public void validate(BoonImportRecord imported) {
        BoonDefinition definition = imported.definition();
        requireSource(definition.source().identity().code(), "Boon primary source");
        definition.reprintedAs().forEach(reprint -> requireSource(
                reprint.source().code(), "Boon reprint source"));
        validateEntries(imported.provenance().entries(), "boon", definition.id().value());
        validateText(Arrays.asList(definition.abilityEntry(), definition.signatureSpellsEntry()),
                "boon", definition.id().value());
    }

    private void validateEntries(List<EntryDte> entries, String ownerKind, String ownerId) {
        for (EntryDte entry : entries) {
            validateText(entryText(entry), ownerKind, ownerId);
        }
    }

    private void validateText(List<String> values, String ownerKind, String ownerId) {
        for (String value : values) {
            if (value == null) {
                continue;
            }
            Matcher matcher = INLINE_TAG.matcher(value);
            while (matcher.find()) {
                String source = sourceFromTag(matcher.group(1), matcher.group(2));
                if (source != null) {
                    requireSource(source, ownerKind + " inline " + matcher.group(1) + " reference in " + ownerId);
                }
            }
        }
    }

    private static String sourceFromTag(String tag, String arguments) {
        String[] parts = arguments.split("\\|", -1);
        if (("spell".equals(tag) || "item".equals(tag) || "creature".equals(tag))
                && parts.length > 1 && !parts[1].isBlank()) {
            return parts[1].trim();
        }
        if ("deity".equals(tag) && parts.length > 2 && !parts[parts.length - 1].isBlank()) {
            return parts[parts.length - 1].trim();
        }
        if ("filter".equals(tag)) {
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.startsWith("source=") && trimmed.length() > "source=".length()) {
                    return trimmed.substring("source=".length()).trim();
                }
            }
        }
        return null;
    }

    private static List<String> entryText(EntryDte entry) {
        List<String> values = new ArrayList<>();
        if (entry instanceof TextEntryDte text) {
            values.add(text.text());
        } else if (entry instanceof NamedEntryDte named) {
            addIfPresent(values, named.name());
            addIfPresent(values, named.entry());
            addChildText(values, named.entries());
        } else if (entry instanceof ListEntryDte list) {
            addChildText(values, list.items());
        } else if (entry instanceof ListItemEntryDte item) {
            addIfPresent(values, item.name());
            addIfPresent(values, item.entry());
            addChildText(values, item.entries());
        } else if (entry instanceof TableEntryDte table) {
            addIfPresent(values, table.caption());
            if (table.colLabels() != null) {
                values.addAll(table.colLabels());
            }
            if (table.rows() != null) {
                table.rows().stream().flatMap(List::stream).forEach(cell -> values.addAll(cellText(cell)));
            }
            if (table.footnotes() != null) {
                values.addAll(table.footnotes());
            }
        } else if (entry instanceof QuoteEntryDte quote) {
            addIfPresent(values, quote.by());
            addChildText(values, quote.entries());
        } else if (entry instanceof InsetEntryDte inset) {
            addIfPresent(values, inset.name());
            addChildText(values, inset.entries());
        } else if (entry instanceof SectionEntryDte section) {
            addIfPresent(values, section.name());
            addChildText(values, section.entries());
        }
        return values;
    }

    private static List<String> cellText(EntryTableCellDte cell) {
        List<String> values = new ArrayList<>();
        if (cell instanceof TextTableCellDte text) {
            values.add(text.text());
        } else if (cell instanceof RollTableCellDte roll && roll.entry() != null) {
            values.addAll(entryText(roll.entry()));
        } else if (cell instanceof StructuredTableCellDte structured) {
            addChildText(values, structured.entries());
        }
        return values;
    }

    private static void addChildText(List<String> target, List<EntryDte> entries) {
        if (entries != null) {
            entries.forEach(entry -> target.addAll(entryText(entry)));
        }
    }

    private static void addIfPresent(List<String> target, String value) {
        if (value != null) {
            target.add(value);
        }
    }

    private void requireSource(String code, String field) {
        if (sourceRegistry.find(code) == null) {
            throw new IllegalStateException("Cult/boon " + field + " is missing from Source Registry: " + code);
        }
    }
}
