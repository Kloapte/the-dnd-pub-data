package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeaponProficiencyDte {
    private ChooseDte choose;
    @JsonProperty("battleaxe|phb")
    private boolean battleaxe;
    @JsonProperty("handaxe|phb")
    private boolean handaxe;
    @JsonProperty("light hammer|phb")
    private boolean lightHammer;
    @JsonProperty("warhammer|phb")
    private boolean warhammer;
    @JsonProperty("longsword|phb")
    private boolean longsword;
    @JsonProperty("shortsword|phb")
    private boolean shortsword;
    @JsonProperty("shortbow|phb")
    private boolean shortbow;
    @JsonProperty("longbow|phb")
    private boolean longbow;
    private boolean firearms;
    @JsonProperty("spear|phb")
    private boolean spear;
    @JsonProperty("javelin|phb")
    private boolean javelin;
    @JsonProperty("rapier|phb")
    private boolean rapier;
    @JsonProperty("hand crossbow|phb")
    private boolean handCrossbow;
    @JsonProperty("light crossbow|phb")
    private boolean lightCrossbow;
    @JsonProperty("trident|phb")
    private boolean trident;
    @JsonProperty("net|phb")
    private boolean net;
    @JsonProperty("greatsword|phb")
    private boolean greatsword;
}
