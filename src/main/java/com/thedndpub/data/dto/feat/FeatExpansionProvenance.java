package com.thedndpub.data.dto.feat;

import com.thedndpub.data.dte.feat.FeatVersionDte;

public record FeatExpansionProvenance(FeatId parentId, int versionIndex, FeatVersionDte declaration) {
}
