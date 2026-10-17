package com.thedndpub.data.dto.traphazard;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.traphazard.TrapDte;
import com.thedndpub.data.dte.traphazard.TrapRatingDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Component
public final class TrapMapper {
    public TrapDefinition map(TrapDte source) {
        require(source.name(), "name");
        require(source.source(), "source");
        if (source.trapHazType() == null) {
            throw new IllegalArgumentException("Trap trapHazType must not be absent");
        }
        validatePage(source.page());
        return new TrapDefinition(new TrapId("trap:" + source.source() + ":" + slug(source.name())),
                source.name(), new SourceReference(source.source(), source.page()), source.trapHazType().name(),
                ratings(source.rating()), source.entries(), source.hasFluffImages(), source.effect(), source.trigger(),
                source.countermeasures(), source.srd(), source.reprintedAs(), source.srd52(), source.duration(),
                source.hauntBonus(), source.initiative(), source.eActive(), source.eDynamic(), source.eConstant(),
                source.initiativeNote());
    }

    private static List<TrapRatingDto> ratings(List<TrapRatingDte> values) {
        return values == null ? List.of() : values.stream()
                .map(value -> new TrapRatingDto(value.tier(), value.threat())).toList();
    }

    static String slug(String value) {
        String slug = Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Trap name cannot produce a stable ID: " + value);
        }
        return slug;
    }

    private static void validatePage(Integer page) {
        if (page != null && page < 1) {
            throw new IllegalArgumentException("Trap page must be positive");
        }
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Trap " + field + " must not be blank");
        }
    }
}
