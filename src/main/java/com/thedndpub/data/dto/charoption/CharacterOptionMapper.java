package com.thedndpub.data.dto.charoption;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.charoption.CharacterCreationOptionDte;
import com.thedndpub.data.dte.charoption.CharacterOptionPrerequisiteDte;
import com.thedndpub.data.dte.charoption.CharacterOptionReprintDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class CharacterOptionMapper {
    public CharacterOptionImportRecord mapRecord(CharacterCreationOptionDte source) {
        Objects.requireNonNull(source, "source");
        requireValue(source.name(), "name");
        requireValue(source.source(), "source");
        if (source.page() == null || source.page() < 1) {
            throw new IllegalArgumentException("Character option page must be a positive integer");
        }
        if (source.optionType() == null || source.optionType().isEmpty()) {
            throw new IllegalArgumentException("Character option requires at least one option type");
        }
        if (source.entries() == null) {
            throw new IllegalArgumentException("Character option entries must be present");
        }

        List<CharacterOptionType> optionTypes = source.optionType().stream()
                .map(CharacterOptionType::new)
                .distinct()
                .sorted((left, right) -> left.code().compareTo(right.code()))
                .toList();
        if (optionTypes.size() != source.optionType().size()) {
            throw new IllegalArgumentException("Character option contains duplicate option type codes");
        }

        List<CharacterOptionPrerequisite> prerequisites = mapPrerequisites(source.prerequisite());
        List<SourceReference> otherSources = source.otherSources() == null
                ? List.of()
                : source.otherSources().stream()
                        .map(reference -> new SourceReference(reference.source(), reference.page()))
                        .toList();
        List<CharacterOptionReprint> reprints = source.reprintedAs() == null
                ? List.of()
                : source.reprintedAs().stream().map(CharacterOptionMapper::mapReprint).toList();

        CharacterOptionId id = new CharacterOptionId(
                "char-option:" + slug(source.source()) + ":" + categorySlug(optionTypes) + ":" + slug(source.name()));
        CharacterOptionDefinition definition = new CharacterOptionDefinition(
                id,
                source.name(),
                new SourceReference(new SourceIdentity(source.source()), source.page()),
                optionTypes,
                prerequisites,
                otherSources,
                reprints);
        CharacterOptionImportProvenance provenance = new CharacterOptionImportProvenance(
                "data/charcreationoptions.json",
                source.entries(),
                source.hasFluffImages());
        return new CharacterOptionImportRecord(definition, provenance);
    }

    private static List<CharacterOptionPrerequisite> mapPrerequisites(List<CharacterOptionPrerequisiteDte> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream().map(value -> new CharacterOptionPrerequisite(
                value.race() == null
                        ? List.of()
                        : value.race().stream()
                                .map(race -> new CharacterOptionRaceRequirement(race.name()))
                                .toList(),
                value.note())).toList();
    }

    private static CharacterOptionReprint mapReprint(CharacterOptionReprintDte value) {
        requireValue(value.uid(), "reprintedAs.uid");
        requireValue(value.tag(), "reprintedAs.tag");
        String[] parts = value.uid().split("\\|", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException("Character option reprint UID must be Name|Source: " + value.uid());
        }
        return new CharacterOptionReprint(parts[0], new SourceIdentity(parts[1]), value.tag(), value.uid());
    }

    private static String categorySlug(List<CharacterOptionType> optionTypes) {
        return optionTypes.stream().map(type -> slug(type.code())).sorted().reduce((left, right) -> left + "+" + right)
                .orElseThrow(() -> new IllegalArgumentException("Character option type is required"));
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Cannot create a stable character option ID from '" + value + "'");
        }
        return normalized;
    }

    private static void requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Character option " + field + " is required");
        }
    }
}
