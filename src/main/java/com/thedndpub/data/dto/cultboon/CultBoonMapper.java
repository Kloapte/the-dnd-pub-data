package com.thedndpub.data.dto.cultboon;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.cultboon.BoonDte;
import com.thedndpub.data.dte.cultboon.CultBoonReprintDte;
import com.thedndpub.data.dte.cultboon.CultBoonReprintUidDte;
import com.thedndpub.data.dte.cultboon.CultBoonTaggedReprintDte;
import com.thedndpub.data.dte.cultboon.CultDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class CultBoonMapper {
    public CultImportRecord mapCult(CultDte source) {
        Objects.requireNonNull(source, "source");
        validateRoot(source.name(), source.source(), source.page(), source.type(), source.entries(), "cult");
        CultDefinition definition = new CultDefinition(
                new CultId("cult:" + slug(source.source()) + ":" + slug(source.name())),
                source.name(),
                new SourceReference(source.source(), source.page()),
                source.type(),
                entryValue(source.goal()),
                entryValue(source.cultists()),
                entryValue(source.signatureSpells()),
                source.otherSources() == null
                        ? List.of()
                        : source.otherSources().stream()
                                .map(reference -> new SourceReference(reference.source(), reference.page()))
                                .toList(),
                mapReprints(source.reprintedAs()));
        return new CultImportRecord(
                definition,
                new CultImportProvenance("data/cultsboons.json", source.entries()));
    }

    public BoonImportRecord mapBoon(BoonDte source) {
        Objects.requireNonNull(source, "source");
        validateRoot(source.name(), source.source(), source.page(), source.type(), source.entries(), "boon");
        BoonDefinition definition = new BoonDefinition(
                new BoonId("boon:" + slug(source.source()) + ":" + slug(source.name())),
                source.name(),
                new SourceReference(source.source(), source.page()),
                source.type(),
                source.abilityEntry(),
                entryValue(source.signatureSpells()),
                mapReprints(source.reprintedAs()));
        return new BoonImportRecord(
                definition,
                new BoonImportProvenance("data/cultsboons.json", source.entries()));
    }

    private static List<CultBoonReprint> mapReprints(List<CultBoonReprintDte> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream().map(CultBoonMapper::mapReprint).toList();
    }

    private static CultBoonReprint mapReprint(CultBoonReprintDte value) {
        if (value == null || value.uid() == null || value.uid().isBlank()) {
            throw new IllegalArgumentException("Cult/boon reprint UID is required");
        }
        if (value instanceof CultBoonTaggedReprintDte tagged && (tagged.tag() == null || tagged.tag().isBlank())) {
            throw new IllegalArgumentException("Tagged cult/boon reprint requires a non-empty tag");
        }
        String[] parts = value.uid().split("\\|", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException("Cult/boon reprint UID must have Name|Source form: " + value.uid());
        }
        String tag = value instanceof CultBoonTaggedReprintDte tagged ? tagged.tag() : null;
        if (!(value instanceof CultBoonTaggedReprintDte) && !(value instanceof CultBoonReprintUidDte)) {
            throw new IllegalArgumentException("Unsupported cult/boon reprint structure: " + value.getClass());
        }
        return new CultBoonReprint(parts[0], new SourceIdentity(parts[1]), tag, value.uid());
    }

    private static String entryValue(com.thedndpub.data.dte.cultboon.CultTextDte value) {
        return value == null ? null : value.entry();
    }

    private static void validateRoot(
            String name,
            String source,
            Integer page,
            String type,
            List<?> entries,
            String kind) {
        requireValue(name, kind + " name");
        requireValue(source, kind + " source");
        requireValue(type, kind + " type");
        if (page == null || page < 1) {
            throw new IllegalArgumentException(kind + " page must be a positive integer");
        }
        if (entries == null) {
            throw new IllegalArgumentException(kind + " entries must be present");
        }
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Cannot create a stable identity component from '" + value + "'");
        }
        return normalized;
    }

    private static void requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Cult/boon " + field + " is required");
        }
    }
}
