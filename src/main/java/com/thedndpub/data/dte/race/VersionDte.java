package com.thedndpub.data.dte.race;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VersionDte {
    private String name;
    private String source;
    private VersionModDte _mod;
    private VersionDte _abstract;
    private List<ImplementationDte> _implementations;
    private OverwriteDte overwrite;
    private Object traitTags;
    private Object skillProficiencies;
    private Object weaponProficiencies;
    private Object darkvision;
    private Object additionalSpells;
    private Object speed;
    private Object resist;
}
