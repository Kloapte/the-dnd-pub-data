package com.thedndpub.data.dte.feat;

import java.util.List;

public record FeatEntryModificationDte(
        String mode,
        FeatReplaceTargetDte replace,
        List<String> names,
        FeatModificationItemsDte items) {
}
