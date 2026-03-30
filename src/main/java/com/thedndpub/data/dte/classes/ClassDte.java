package com.thedndpub.data.dte.classes;

import lombok.Data;

import java.util.List;

@Data
public class ClassDte {
    private String name;
    private String source;
    private Integer page;
    private boolean srd;
    private boolean srd52;
    private boolean basicRules2024;
    private List<String> reprintedAs;
    private String edition;
    private Object hd;
    private List<String> proficiency;
    private String spellcastingAbility;
    private List<Object> primaryAbility;
    private String casterProgression;
    private String preparedSpells;
    private List<Integer> preparedSpellsProgression;
    private String preparedSpellsChange;
    private List<Integer> cantripProgression;
    private Object startingProficiencies;
    private Object startingEquipment;
    private Object multiclassing;
    private List<Object> classTableGroups;
    private List<Object> classFeatures;
    private List<Object> featProgression;
    private List<Object> additionalSpells;
    private String subclassTitle;
    private Boolean hasFluff;
    private Boolean hasFluffImages;
}