package com.thedndpub.data.dte.traphazard;

import java.util.List;

public record TrapHazardsFileDte(List<TrapDte> trap, List<HazardDte> hazard) {
    public TrapHazardsFileDte {
        trap = trap == null ? null : List.copyOf(trap);
        hazard = hazard == null ? null : List.copyOf(hazard);
    }
}
