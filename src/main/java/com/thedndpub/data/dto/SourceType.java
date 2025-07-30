package com.thedndpub.data.dto;

import lombok.Getter;

@Getter
public enum SourceType {
    PHB("Player's Handbook"),
    XPHB("Expanded Player's Handbook"),
    DMG("Dungeon Master's Guide"),
    MM("Monster Manual"),
    TCE("Tasha's Cauldron of Everything"),
    XGE("Xanathar's Guide to Everything"),
    VRGR("Van Richten's Guide to Ravenloft"),
    MOT("Mythic Odysseys of Theros"),
    EEPC("Elemental Evil Player's Companion"),
    AAG("Astral Adventurer's Guide"),

    SCAG("Sword Coast Adventurer's Guide"),
    EGW("Explorer's Guide to Wildemount"),
    MTF("Mordenkainen's Tome of Foes"),
    VGtM("Volo's Guide to Monsters"),
    VGM("Volo's Guide to Monsters"),
    AI("Acquisitions Incorporated"),
    UA("Unearthed Arcana"),
    PSK("Princes of the Apocalypse"),
    PSA("Plane Shift: Amonkhet"),
    PSD("Plane Shift: Dominaria"),
    PSZ("Plane Shift: Zendikar"),
    PSI("Plane Shift: Ixalan"),
    PSX("Plane Shift: Kaladesh"),
    ERLW("Eberron: Rising from the Last War"),
    GGR("Guildmasters' Guide to Ravnica"),
    WBtW("The Wild Beyond the Witchlight"),
    DSotDQ("Dragonlance: Shadow of the Dragon Queen"),
    SCC("Strixhaven: Curriculum of Chaos"),

    LR("Locathah Rising"),
    FTD("Fizban's Treasury of Dragons"),
    GoS("Ghosts of Saltmarsh"),
    BOOM("Bigby Presents: Glory of the Giants"),
    MPMM("Mordenkainen Presents: Monsters of the Multiverse"),

    TTP("The Tortle Package"),
    OGA("One Grung Above"),
    AWM("Acquisitions Incorporated: The Way of the Mad Mage"),
    HB("Homebrew"),
    CUSTOM("Custom"),
    CR("Critical Role"),
    WOTC("Wizard of the Coast"),

    UNKNOWN("Unknown");

    private final String sourceName;

    SourceType(String sourceName) {
        this.sourceName = sourceName;
    }

    public static SourceType fromString(String input) {
        try {
            return SourceType.valueOf(input);
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Unknown Source: " + input);
            return SourceType.UNKNOWN;
        }
    }
}
