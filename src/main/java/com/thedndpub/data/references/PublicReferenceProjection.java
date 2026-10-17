package com.thedndpub.data.references;

import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.references.PublicReferenceRole;
import com.thedndpub.data.dto.references.PublicReferenceTargetKind;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Indexes startup resolution results for additive public detail projections. */
@Component
public final class PublicReferenceProjection {
    private final Map<ReferenceOwner, List<PublicCrossCatalogReferenceDto>> byOwner;

    public PublicReferenceProjection(CrossCatalogResolutionCoordinator coordinator) {
        ResolutionSnapshot snapshot = coordinator.snapshot();
        Objects.requireNonNull(snapshot, "snapshot");
        Map<ReferenceOwner, ArrayList<PublicCrossCatalogReferenceDto>> grouped = new LinkedHashMap<>();
        for (ResolutionResult result : snapshot.results()) {
            AuthoredReference reference = result.reference();
            ReferenceTargetKind publicTargetKind = result.resolvedDefinitionKind() == null
                    ? reference.targetKind()
                    : result.resolvedDefinitionKind();
            PublicReferenceTargetKind targetKind = PublicReferenceTargetKind.valueOf(publicTargetKind.name());
            PublicCrossCatalogReferenceDto projected = new PublicCrossCatalogReferenceDto(
                    reference.fieldLocator(),
                    targetKind,
                    role(reference),
                    reference.originalToken(),
                    reference.parsedName() == null ? reference.originalToken() : reference.parsedName(),
                    reference.originalSource(),
                    result.resolvedTargetId());
            grouped.computeIfAbsent(reference.owner(), ignored -> new ArrayList<>()).add(projected);
        }
        Map<ReferenceOwner, List<PublicCrossCatalogReferenceDto>> immutable = new LinkedHashMap<>();
        grouped.forEach((owner, values) -> immutable.put(owner, List.copyOf(values)));
        byOwner = Collections.unmodifiableMap(immutable);
    }

    public List<PublicCrossCatalogReferenceDto> forOwner(String catalog, String stableId) {
        return byOwner.getOrDefault(new ReferenceOwner(catalog, stableId), List.of());
    }

    private static PublicReferenceRole role(AuthoredReference reference) {
        if (reference.semanticForm() == ReferenceSemanticForm.CHOICE) {
            return PublicReferenceRole.CHOICE;
        }
        if (reference.semanticForm() == ReferenceSemanticForm.FILTER) {
            return PublicReferenceRole.FILTER;
        }
        if (reference.semanticForm() == ReferenceSemanticForm.GROUP) {
            return PublicReferenceRole.GROUP;
        }
        String locator = reference.fieldLocator().toLowerCase(java.util.Locale.ROOT);
        if ((locator.contains("startingequipment[") && locator.contains(".options["))
                || (locator.contains("startingoptions.equipment[") && locator.contains(".branches["))) {
            return PublicReferenceRole.CHOICE;
        }
        if (locator.contains("prerequisite")) {
            return PublicReferenceRole.PREREQUISITE;
        }
        if (locator.equals("baseitem")) {
            return PublicReferenceRole.REFERENCE;
        }
        if (reference.semanticForm() == ReferenceSemanticForm.UNSUPPORTED) {
            return PublicReferenceRole.REFERENCE;
        }
        if (locator.contains("grant") || locator.contains("proficiencies")
                || locator.contains("startingequipment")
                || locator.contains("additionalspells")) {
            return PublicReferenceRole.GRANT;
        }
        return PublicReferenceRole.REFERENCE;
    }
}
