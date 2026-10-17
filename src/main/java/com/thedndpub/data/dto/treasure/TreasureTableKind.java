package com.thedndpub.data.dto.treasure;

public enum TreasureTableKind {
    INDIVIDUAL("individual"),
    HOARD("hoard"),
    DRAGON("dragon"),
    GEMS("gems"),
    ART_OBJECTS("artObjects"),
    MAGIC_ITEMS("magicItems");

    private final String sourceCollection;

    TreasureTableKind(String sourceCollection) {
        this.sourceCollection = sourceCollection;
    }

    public String sourceCollection() {
        return sourceCollection;
    }
}
