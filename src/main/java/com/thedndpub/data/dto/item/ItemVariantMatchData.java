package com.thedndpub.data.dto.item;

import java.util.List;

/** Internal fields used to evaluate source-authored magic-variant applicability conditions. */
public record ItemVariantMatchData(
        String itemId,
        String name,
        String source,
        List<String> typeCodes,
        String alternateTypeCode,
        List<String> propertyCodes,
        String weaponCategory,
        String damageType,
        String spellcastingFocusType,
        Boolean weapon,
        Boolean armor,
        Boolean arrow,
        Boolean axe,
        Boolean bolt,
        Boolean bow,
        Boolean crossbow,
        Boolean net,
        Boolean polearm,
        Boolean spear,
        Boolean sword,
        Boolean bulletFirearm,
        Boolean cellEnergy) {
    public ItemVariantMatchData {
        typeCodes = List.copyOf(typeCodes);
        propertyCodes = List.copyOf(propertyCodes);
    }
}
