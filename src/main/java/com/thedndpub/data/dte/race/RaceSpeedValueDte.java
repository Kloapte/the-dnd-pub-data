package com.thedndpub.data.dte.race;

/** Preserves the source's numeric movement value or its explicit boolean marker. */
public record RaceSpeedValueDte(Integer feet, Boolean sourceFlag) {
    public static RaceSpeedValueDte feet(Integer value) {
        return value == null ? null : new RaceSpeedValueDte(value, null);
    }

    public static RaceSpeedValueDte flag(Boolean value) {
        return value == null ? null : new RaceSpeedValueDte(null, value);
    }
}
