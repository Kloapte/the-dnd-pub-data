package com.thedndpub.data.dao;

import lombok.Getter;

@Getter
public enum Source {
    //Official
    PHB("Player's Handbook"),
    DMG("Dungeon Master's Guid"),
    MM("Monster Manual"),
    TCE("Tasha's Cauldron of Everything"),
    XGE("Xanathar's Guid to Everything"),
    VRGR("Van Richten's Guide to Ravenloft"),
    MOT("Mythic Odysseys of Theros"),

    //Race & Settings
    SCAG("Sword Coast Adventurer's Guide"),
    EGW("Explorer's Guide to Wildemount"),
    MTF("Mordenkainen's Tome of Foes"),
    VGtM("Volo's Guid to Monsters"),
    AI("Acquisitions Incorporated"),
    UA("Unearthed Arcana"),

    //Dragon-Themed & Advanced Resources
    FTD("Fizban's Treasury of Dragons"),
    GoS("Ghosts of Saltmarsh"),
    BOOM("Bigby Presents: Glory of the Giants"),
    MPMM("Mordenkainen Presents: Monsters of the Multiverse"),

    //Custom /Community
    HB("Homebrew"),
    CUSTOM("Custom"),
    CR("Critical Role"),
    WOTC("Wizard of the Coast");

    private final String source;

    Source(String source) {
        this.source = source;
    }
}