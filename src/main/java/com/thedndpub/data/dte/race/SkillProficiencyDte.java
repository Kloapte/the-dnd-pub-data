package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SkillProficiencyDte {
    private String name;
    private ChooseDte choose;
    private int any;
    private boolean perception;
    private boolean intimidation;
    private boolean stealth;
    private boolean athletics;
    private boolean acrobatics;
    private boolean performance;
    private boolean persuasion;
    private boolean survival;
    @JsonProperty("animal handling")
    private boolean animalHandling;
    private boolean history;
    private boolean nature;
    @JsonProperty("sleight of hand")
    private boolean sleightOfHand;
    private boolean deception;

    public boolean hasSkillProficiency() {
        return perception || intimidation || stealth || athletics || acrobatics || performance || persuasion || survival || sleightOfHand || animalHandling || history || nature || deception;
    }
}
