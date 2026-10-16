package com.thedndpub.data.dto.race;

import com.thedndpub.data.dte.race.RaceDte;
import com.thedndpub.data.dte.race.RaceVersionDte;
import com.thedndpub.data.dte.race.RaceVersionImplementationDte;
import com.thedndpub.data.dte.race.SubraceDte;

public record RaceSourceData(
        RaceDte race,
        SubraceDte subrace,
        RaceVersionDte directVersion,
        RaceVersionDte template,
        RaceVersionImplementationDte implementation) {
    public RaceSourceData {
        int recordCount = (race == null ? 0 : 1) + (subrace == null ? 0 : 1);
        if (recordCount != 1) {
            throw new IllegalArgumentException("Race source data must hold exactly one race or subrace root");
        }
        if (directVersion != null && (template != null || implementation != null)) {
            throw new IllegalArgumentException("Direct versions cannot also be template implementations");
        }
        if ((template == null) != (implementation == null)) {
            throw new IllegalArgumentException("Template and implementation must be supplied together");
        }
    }
}
