package com.thedndpub.data.dte.feat;

/** A root record or an inherited variant prepared for normalization. */
public record ExpandedFeatDte(
        FeatDte definition,
        String parentName,
        String parentSource,
        Integer versionIndex,
        FeatVersionDte declaration) {
    public boolean isVariant() {
        return declaration != null;
    }
}
