package com.thedndpub.data.dto;

public enum LicenseType {
    OPEN(true, "You can freely use and redistribute it in your own tools or apps, including commercial use (with proper attribution for SRD 5.2 CC-BY 4.0)"),
    BASIC(false, "Not automatically licensed for redistribution — you can link to it, but not copy wholesale.  (if linking to D&D Beyond rather than redistributing text)"),
    RESTRICTED(false, "Not free to redistribute in your own public app");

    private final boolean shareable;
    private final String description;

    LicenseType(boolean shareable, String description) {
        this.shareable = shareable;
        this.description = description;
    }
}
