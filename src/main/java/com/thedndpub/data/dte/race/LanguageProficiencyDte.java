package com.thedndpub.data.dte.race;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageProficiencyDte {
    private String name;
    private ChooseDte choose;
    private int anyStandard;
    private boolean common;
    private boolean other;
    private boolean auran;
    private boolean celestial;
    private boolean goblin;
    private boolean sylvan;
    private boolean draconic;
    private boolean dwarvish;
    private boolean elvish;
    private boolean giant;
    private boolean primordial;
    private boolean gnomish;
    private boolean terran;
    private boolean undercommon;
    private boolean orc;
    private boolean halfling;
    private boolean aquan;
    private boolean infernal;
    private boolean abyssal;

    public boolean hasLanguageProficiency() {
        return common || other || auran || celestial || goblin || sylvan || draconic || dwarvish || elvish || giant || primordial || gnomish || terran || undercommon || orc || halfling || aquan || infernal || abyssal;
    }
}
