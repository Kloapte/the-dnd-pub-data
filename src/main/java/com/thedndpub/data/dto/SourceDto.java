package com.thedndpub.data.dto;

public enum SourceDto {
    PHB("PHB", "Player's Handbook"),
    DMG("DMG", "Dungeon Master's Guide"),
    MM("MM","Monster Manual"),
    TCE("TCE","Tasha's Cauldron of Everything"),
    XGE("XGE","Xanathar's Guide to Everything"),
    VRGR("VRGR","Van Richten's Guide to Ravenloft"),
    MOT("MOT","Mythic Odysseys of Theros"),

    SCAG("SCAG","Sword Coast Adventurer's Guide"),
    EGM("EGM","Explorer's Guide to Wildemount"),
    MTF("MTF","Mordenkainen's Tome of Foes"),
    VGtM("VGtM","Volo's Guide to Monsters"),
    AI("AI","Acquisitions Incorporated"),
    UA("UA","Unearthed Arcana"),

    FTD("FTD","Fizban's Treasury of Dragons"),
    GoS("GoS","Ghosts of Saltmarsh"),
    BOOM("BOOM","Bigby Presents: Glory of the Giants"),
    MPMM("MPMM","Mordenkainen Presents: Monsters of the Multiversie"),

    HB("HB","Homebrew"),
    CUSTOM("CUSTOM","Custom"),
    CR("CR","Critical Role"),
    WOTC("WOTC","Wizard of the Coast"),

    UNKNOWN("UNKNOWN", "Unknown");

    private final String source;
    private final String sourceName;

    SourceDto(String source, String sourceName) {
        this.source = source;
        this.sourceName = sourceName;
    }

    public String getSource() {
        return source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public static SourceDto fromString(String input) {
        try {
            return SourceDto.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Unknown Source: " + input);
            return SourceDto.UNKNOWN;
        }
    }
}
