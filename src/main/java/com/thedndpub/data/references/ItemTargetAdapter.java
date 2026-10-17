package com.thedndpub.data.references;

import com.thedndpub.data.dto.item.ItemSummaryDto;
import com.thedndpub.data.dto.item.ItemDefinitionKind;
import com.thedndpub.data.services.ItemCatalog;
import com.thedndpub.data.services.SourceRegistry;

/** Matches equipment item UIDs against both Item and Base Item definitions without merging their identities. */
public final class ItemTargetAdapter extends AbstractIndexedTargetAdapter {
    public ItemTargetAdapter(
            ItemCatalog catalog,
            SourceRegistry sourceRegistry,
            ReferenceTargetKind targetKind) {
        super(targetKind, catalog.summaries().stream()
                .filter(summary -> definitionKind(summary, targetKind))
                .map(summary -> target(summary, sourceRegistry, targetKind))
                .toList());
    }

    private static boolean definitionKind(ItemSummaryDto summary, ReferenceTargetKind targetKind) {
        return switch (targetKind) {
            case ITEM -> true;
            case BASE_ITEM -> summary.definitionKind() == ItemDefinitionKind.BASE_ITEM;
            default -> false;
        };
    }

    private static TargetIdentity target(
            ItemSummaryDto summary,
            SourceRegistry sourceRegistry,
            ReferenceTargetKind targetKind) {
        String source = summary.source().identity().code();
        if (sourceRegistry.find(source) == null) {
            throw new IllegalStateException("Unknown item target source: " + source);
        }
        ReferenceTargetKind identityKind = summary.definitionKind() == ItemDefinitionKind.BASE_ITEM
                ? ReferenceTargetKind.BASE_ITEM : ReferenceTargetKind.ITEM;
        return new TargetIdentity(identityKind, summary.id(), summary.name(), source);
    }
}
