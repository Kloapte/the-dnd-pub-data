package com.thedndpub.data.dte.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContainerCapacityItemDte {
    @JsonProperty("crossbow bolt|phb")
    private int crossbowBolt_phb;
    @JsonProperty("bolt|xphb")
    private int bolt_xphb;
    @JsonProperty("parchment (one sheet)|phb")
    private int parchmentOneSheet_phb;
    @JsonProperty("paper (one sheet)|phb")
    private int paperOneSheet_phb;
    @JsonProperty("parchment|xphb")
    private int parchment_xphb;
    @JsonProperty("paper|xphb")
    private int paper_xphb;
    @JsonProperty("sling bullet|phb")
    private int slingBullet_phb;
    @JsonProperty("blowgun needle|phb")
    private int blowgunNeedle_phb;
    @JsonProperty("firearm bullet|xphb")
    private int firearmBullet_xphb;
    @JsonProperty("sling bullet|xphb")
    private int slingBullet_xphb;
    @JsonProperty("needle|xphb")
    private int needle_xphb;
    @JsonProperty("arrow|phb")
    private int arrow_phb;
    @JsonProperty("arrow|xphb")
    private int arrow_xphb;
    @JsonProperty("javelin|phb")
    private int javelin_phb;
    @JsonProperty("quarterstaff|phb")
    private int quarterstaff_phb;
    @JsonProperty("spear|phb")
    private int spear_phb;
    @JsonProperty("javelin|xphb")
    private int javelin_xphb;
    @JsonProperty("quarterstaff|xphb")
    private int quarterstaff_xphb;
    @JsonProperty("spear|xphb")
    private int spear_xphb;
    @JsonProperty("longbow|xphb")
    private int longbow_xphb;
    @JsonProperty("shortbow|xphb")
    private int shortbow_xphb;
}
