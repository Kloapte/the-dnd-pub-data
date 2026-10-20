package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.mechanics.model.ModelChecks;

/** Pinned source-artifact identity for Spell editions used by production extraction. */
enum SpellMechanicsSourceEdition {
    PHB("PHB", "phb", "c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a"),
    XGE("XGE", "xge", "17e7e9fe7ad20c7df094d2a4929558c5d2a331f7e1498c7575181864ec5dc1dc"),
    XPHB("XPHB", "xphb", "442544a91aad1181cbad78d49cfa48f9ced7d8b048dbc781e32fa6fefc6278b9"),
    AU("AU", "au", "805a3ccb583e8415ee494e582c6172d900ebd63f85d599b785f260591992ead7"),
    TCE("TCE", "tce", "2366f928759cf4b20807a91f716c1dde3c40727edbc5573eafb61c471a9633bc");

    private static final String DATASET_VERSION = "5eTools v2.37.0";

    private final String sourceCode;
    private final String resourceName;
    private final String resourceSha256;

    SpellMechanicsSourceEdition(String sourceCode, String resourceName, String resourceSha256) {
        this.sourceCode = sourceCode;
        this.resourceName = resourceName;
        this.resourceSha256 = resourceSha256;
    }

    static SpellMechanicsSourceEdition forCode(String sourceCode) {
        for (SpellMechanicsSourceEdition edition : values()) {
            if (edition.sourceCode.equals(sourceCode)) {
                return edition;
            }
        }
        return null;
    }

    void validateSourceRevision(SpellMechanicsSourceOwner owner) {
        ModelChecks.require(owner.source().identity().code().equals(sourceCode)
                        && owner.revision().datasetVersion().equals(DATASET_VERSION)
                        && owner.revision().resourcePath().equals("data/spells/spells-" + resourceName + ".json")
                        && owner.revision().resourceSha256().equals(resourceSha256),
                "Spell input differs from the audited pinned source revision");
    }
}
