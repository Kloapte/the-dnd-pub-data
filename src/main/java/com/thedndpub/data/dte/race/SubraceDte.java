package com.thedndpub.data.dte.race;

import com.thedndpub.data.dte.race.deserializers.ModDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubraceDte extends RaceDte {
    private String raceName;
    private List<String> alias;
    private String raceSource;
    private ModDto _mod;
    private SubraceDte _abstract;
    private List<ImplementationDte> _implementations;
    private OverwriteDte overwrite;
}
