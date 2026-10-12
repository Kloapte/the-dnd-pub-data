package com.thedndpub.data.dte.monsters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.generic.EntryDte;
import com.thedndpub.data.dte.generic.OtherSourceDte;
import com.thedndpub.data.dte.generic.deserializers.EntryDteDeserializer;
import com.thedndpub.data.dte.monsters.deserializers.*;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MonsterDte(
        String name,
        String source,
        List<String> alias,
        @JsonDeserialize(using = ShortNameDeserializer.class)
        String shortName,
        List<OtherSourceDte> otherSources,
        List<String> reprintedAs,
        Integer page,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024,
        List<String> size,
        String sizeNote,
        @JsonDeserialize(using = MonsterTypeDeserializer.class)
        String type, //TODO
        Boolean isNpc,
        Boolean isNamedCreature,
        Boolean familiar,
        String summonedBySpell,
        Integer summonedBySpellLevel,
        String summonedByClass,
        @JsonDeserialize(contentUsing = AlignmentDeserializer.class)
        List<String> alignment,
        String alignmentPrefix,
        @JsonDeserialize(contentUsing = AcDeserializer.class)
        List<AcDte> ac,
        HpDte hp,
        MonsterSpeedDte speed, //TODO
        @JsonProperty("str")
        Integer strength,
        @JsonProperty("dex")
        Integer dexterity,
        @JsonProperty("con")
        Integer constitution,
        @JsonProperty("int")
        Integer intelligence,
        @JsonProperty("wis")
        Integer wisdom,
        @JsonProperty("cha")
        Integer charisma,
        SaveDte save,
        SkillDte skill,
        List<String> senses,
        String passive,
        @JsonDeserialize(using = ResistDeserializer.class)
        List<DamageTraitDte> resist,
        @JsonDeserialize(using = ImmumeDeserializer.class)
        List<DamageTraitDte> immune,
        @JsonDeserialize(using = ConditionImmuneDeserializer.class)
        List<DamageTraitDte> conditionImmune,
        @JsonDeserialize(using = VulnerableDeserializer.class)
        List<DamageTraitDte> vulnerable,
        List<String> languages,
        @JsonDeserialize(using = CombatRatingDeserializer.class)
        CombatRatingDte cr,
        Integer level,
        @JsonDeserialize(using = EntryDteDeserializer.class)
        List<EntryDte> trait,
        List<String> group,
        String dragonAge,
        String dragonCastingColor,
        String pbNote,
        @JsonDeserialize(using = EntryDteDeserializer.class)
        List<EntryDte> action,
        String actionNote,
        @JsonDeserialize(using = EntryDteDeserializer.class)
        List<EntryDte> bonus,
        List<SpellcastingDte> spellcasting, //TODO
        List<String> reactionHeader,
        List<Object> reaction, //TODO
        List<String> legendaryHeader,
        List<Object> legendary, //TODO
        Integer legendaryActionsLair,
        Integer legendaryActions,
        Object legendaryGroup, //TODO
        List<String> mythicHeader,
        List<Object> mythic, //TODO
        List<String> conditionInflict,
        List<String> conditionInflictSpell,
        List<String> conditionInflictLegendary,
        List<String> savingThrowForced,
        List<String> savingThrowForcedSpell,
        List<String> savingThrowForcedLegendary,
        List<String> attachedItems,
        List<String> environment,
        Object initiative, //TODO
        List<String> treasure,
        List<Object> gear, //TODO

        List<Object> _versions, //TODO
        Object _copy, //TODO
        List<Object> variant, //TODO

        List<String> traitTags,
        List<String> senseTags,
        List<String> actionTags,
        List<String> languageTags,
        List<String> damageTags,
        List<String> damageTagsSpell,
        List<String> spellcastingTags,
        List<String> damageTagsLegendary,
        List<String> miscTags,
        String tokenCredit,
        List<Object> altArt, //TODO
        Boolean hasToken,
        Boolean hasFluff,
        Boolean hasFluffImages,
        Object soundClip //TODO
) {
}
