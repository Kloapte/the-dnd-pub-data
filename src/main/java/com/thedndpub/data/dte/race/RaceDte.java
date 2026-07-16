package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dte.generic.AbilityDte;
import com.thedndpub.data.dte.generic.CopyDte;
import com.thedndpub.data.dte.generic.EntryDte;
import com.thedndpub.data.dte.generic.OtherSourceDte;
import com.thedndpub.data.dte.generic.deserializers.EntryDteDeserializer;
import com.thedndpub.data.dte.race.deserializers.ResistDteDeserializer;
import com.thedndpub.data.dte.race.deserializers.SpeedDteDeserializer;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RaceDte {
    private String name;
    private String source;
    private Integer page;
    private boolean srd;
    private boolean srd52;
    private boolean basicRules;
    private boolean basicRules2024;
    private String edition;
    private List<String> creatureTypes;
    private List<OtherSourceDte> otherSources;
    private List<OtherSourceDte> additionalSources;
    private List<String> reprintedAs;
    private CopyDte _copy;
    private String lineage;
    private List<String> creatureTypeTags;
    private List<String> size;
    @JsonDeserialize(using = SpeedDteDeserializer.class)
    private SpeedDte speed;
    private List<AbilityDte> ability;
    private HeightAndWeightDte heightAndWeight;
    private AgeDte age;
    private int darkvision;
    private int blindsight;
    private List<FeatDte> feats;
    //Not mapping because just tags, not important
    private List<String> traitTags;
    private List<ToolProficiencyDte> toolProficiencies;
    private List<WeaponProficiencyDte> weaponProficiencies;
    private List<SkillProficiencyDte> skillProficiencies;
    private List<LanguageProficiencyDte> languageProficiencies;
    private List<ArmorProficiencyDte> armorProficiencies;
    private List<AdditionalSpellDte> additionalSpells;
    private List<SkillToolLanguageProficienciesDte> skillToolLanguageProficiencies;
    @JsonDeserialize(using = ResistDteDeserializer.class)
    private ResistDte resist;
    private List<String> immune;
    private List<String> conditionImmune;
    private List<String> vulnerable;
    private SoundClipDte soundClip;
    private SizeEntryDte sizeEntry;
    @JsonDeserialize(using = EntryDteDeserializer.class)
    private List<EntryDte> entries;
    private boolean hasFluff;
    private boolean hasFluffImages;
    private List<VersionDte> _versions;
}
