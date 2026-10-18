package com.thedndpub.data.publication.spell;

public enum SpellPublicationNotation {
    DAMAGE_TAG("damage"),
    DICE_TAG("dice");

    private final String tagName;

    SpellPublicationNotation(String tagName) {
        this.tagName = tagName;
    }

    public String tagName() {
        return tagName;
    }
}
