package com.thedndpub.data.services;

import com.thedndpub.data.dte.bastion.FacilityDte;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class FacilitySourceMarkupValidator {
    private static final Pattern SOURCE_QUALIFIED_MARKUP = Pattern.compile(
            "\\{@(book|deck|spell|item|skill|variantrule|reward)\\s+([^}]+)}");
    private static final Pattern SOURCE_CODE = Pattern.compile("[A-Z][A-Z0-9.]+");

    private FacilitySourceMarkupValidator() {
    }

    static void validate(FacilityDte facility, SourceRegistry registry) {
        List<String> textValues = new ArrayList<>();
        collectEntries(facility.entries(), textValues);
        if (facility.prerequisite() != null) {
            facility.prerequisite().stream()
                    .filter(value -> value.otherSummary() != null)
                    .forEach(value -> {
                        textValues.add(value.otherSummary().entry());
                        textValues.add(value.otherSummary().entrySummary());
                    });
        }
        for (String text : textValues) {
            validateText(text, facility.name(), registry);
        }
    }

    private static void validateText(String text, String facilityName, SourceRegistry registry) {
        if (text == null) {
            return;
        }
        Matcher matcher = SOURCE_QUALIFIED_MARKUP.matcher(text);
        while (matcher.find()) {
            String[] arguments = matcher.group(2).split("\\|", -1);
            if (arguments.length < 2) {
                continue;
            }
            String candidate = arguments[1].trim();
            if (SOURCE_CODE.matcher(candidate).matches() && registry.find(candidate) == null) {
                throw new IllegalStateException("Facility '" + facilityName
                        + "' references unknown markup source code: " + candidate);
            }
        }
    }

    private static void collectEntries(List<EntryDte> entries, List<String> values) {
        if (entries == null) {
            return;
        }
        for (EntryDte entry : entries) {
            collectEntry(entry, values);
        }
    }

    private static void collectEntry(EntryDte entry, List<String> values) {
        if (entry instanceof TextEntryDte text) {
            values.add(text.text());
        } else if (entry instanceof NamedEntryDte named) {
            values.add(named.name());
            values.add(named.entry());
            collectEntries(named.entries(), values);
        } else if (entry instanceof ListEntryDte list) {
            collectEntries(list.items(), values);
        } else if (entry instanceof ListItemEntryDte item) {
            values.add(item.name());
            values.add(item.entry());
            collectEntries(item.entries(), values);
        } else if (entry instanceof TableEntryDte table) {
            values.add(table.caption());
            values.addAll(table.footnotes() == null ? List.of() : table.footnotes());
            if (table.rows() != null) {
                table.rows().stream().flatMap(List::stream).forEach(cell -> collectCell(cell, values));
            }
        } else if (entry instanceof QuoteEntryDte quote) {
            values.add(quote.by());
            collectEntries(quote.entries(), values);
        } else if (entry instanceof InsetEntryDte inset) {
            values.add(inset.name());
            collectEntries(inset.entries(), values);
        } else if (entry instanceof SectionEntryDte section) {
            values.add(section.name());
            collectEntries(section.entries(), values);
        }
    }

    private static void collectCell(EntryTableCellDte cell, List<String> values) {
        if (cell instanceof TextTableCellDte text) {
            values.add(text.text());
        } else if (cell instanceof RollTableCellDte roll) {
            if (roll.entry() != null) {
                collectEntry(roll.entry(), values);
            }
        } else if (cell instanceof StructuredTableCellDte structured) {
            collectEntries(structured.entries(), values);
        }
    }
}
