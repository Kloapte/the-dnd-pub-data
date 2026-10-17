package com.thedndpub.data.dte.cultboon;

import java.util.List;

public record CultsAndBoonsFileDte(List<CultDte> cult, List<BoonDte> boon) {
    public CultsAndBoonsFileDte {
        cult = cult == null ? null : List.copyOf(cult);
        boon = boon == null ? null : List.copyOf(boon);
    }
}
