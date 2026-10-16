package com.thedndpub.data.dte.magicvariant;

import java.util.List;

public record MagicVariantRequirementDte(
        String type,
        String source,
        MagicVariantStringValuesDte name,
        MagicVariantStringValuesDte property,
        String weaponCategory,
        String dmgType,
        String scfType,
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
}
