package com.thedndpub.data.dte.race;

import com.thedndpub.data.dte.race.deserializers.ModDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VersionDte extends RaceDte {
    private String parent;
    private ModDto _mod;
    private VersionDte _abstract;
    private List<ImplementationDte> _implementations;
    private OverwriteDte overwrite;
}
