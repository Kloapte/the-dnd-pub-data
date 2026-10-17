package com.thedndpub.data.dto.traphazard;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.traphazard.HazardDte;
import com.thedndpub.data.dte.traphazard.TrapRatingDte;
import com.thedndpub.data.dte.traphazard.TrapSourceReferenceDte;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class HazardMapper {
    public HazardDefinition map(HazardDte source) {
        require(source.name(), "name");
        require(source.source(), "source");
        if (source.page() != null && source.page() < 1) {
            throw new IllegalArgumentException("Hazard page must be positive");
        }
        validateSecondaryPages(source.otherSources());
        return new HazardDefinition(new HazardId("hazard:" + source.source() + ":" + TrapMapper.slug(source.name())),
                source.name(), new SourceReference(source.source(), source.page()),
                source.trapHazType() == null ? null : source.trapHazType().name(),
                ratings(source.rating()), source.entries(), source.hasFluffImages(), source.reprintedAs(),
                source.srd52(), source.otherSources());
    }

    private static List<TrapRatingDto> ratings(List<TrapRatingDte> values) {
        return values == null ? List.of() : values.stream()
                .map(value -> new TrapRatingDto(value.tier(), value.threat())).toList();
    }

    private static void validateSecondaryPages(List<TrapSourceReferenceDte> references) {
        if (references != null && references.stream().anyMatch(reference -> reference.page() != null
                && reference.page() < 1)) {
            throw new IllegalArgumentException("Hazard secondary source page must be positive");
        }
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hazard " + field + " must not be blank");
        }
    }
}
