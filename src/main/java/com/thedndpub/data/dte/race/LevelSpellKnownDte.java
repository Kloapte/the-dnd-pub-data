package com.thedndpub.data.dte.race;

import lombok.Data;

import java.util.List;

@Data
public class LevelSpellKnownDte {
    private List<String> spells;
    private String choose;
    private Integer count;
    private boolean rest;
    private boolean daily;
    private int level;
}
