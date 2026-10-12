package com.thedndpub.data.dto.monster;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Application-facing, reusable monster definition. It contains no encounter state.
 */
public record MonsterDto(Identity identity, Statistics statistics, Defenses defenses,
                         Features features, Combat combat, List<SpellcastingBlock> spellcasting,
                         Classification classification, Catalog catalog) {
    public MonsterDto {
        spellcasting = List.copyOf(spellcasting);
    }

    public record MonsterId(String value) {
    }

    public enum Ability {STR, DEX, CON, INT, WIS, CHA}

    public record Identity(MonsterId id, String name, List<String> aliases, Source source,
                           List<String> groups, List<String> sizes, CreatureType creatureType,
                           List<Alignment> alignments, String alignmentPrefix, ShortName shortName,
                           Boolean namedCreature, Boolean npc, String dragonAge, String dragonCastingColor,
                           Summoning summoning, Reference legendaryGroup) {
    }

    public record Source(String code, Integer page, List<SourceReference> otherSources,
                         List<SourceReference> referenceSources, List<String> reprintedAs,
                         RulesAvailability availability) {
    }

    public record SourceReference(String source, Integer page) {
    }

    public record RulesAvailability(Boolean basicRules, Boolean basicRules2024, Boolean srd,
                                    Boolean srd52, Set<String> editionIds) {
    }

    public record Reference(String name, String source) {
    }

    public record ShortName(Boolean enabled, String value) {
    }

    public record Summoning(String summonedByClass, String summonedBySpell, Integer spellLevel) {
    }

    public record CreatureType(String value, List<String> choices, List<TypeTag> tags, String swarmSize,
                               Boolean sidekickHidden, List<String> sidekickTags, String sidekickType) {
    }

    public record TypeTag(String tag, String prefix, Boolean prefixHidden) {
    }

    public record Alignment(List<String> codes, Integer chance, String special, String note) {
    }

    public record Statistics(AbilityScores abilities, Initiative initiative, List<ArmorClass> armorClasses,
                             HitPoints hitPoints, Movement movement, ChallengeRating challengeRating,
                             ProficiencyBonus proficiencyBonus, Map<String, String> savingThrows,
                             Map<String, SkillValue> skills, PassivePerception passivePerception,
                             List<String> senses, NullableList<String> languages, Integer level,
                             List<String> forcedSaves, List<String> legendaryForcedSaves,
                             List<String> spellForcedSaves, String proficiencyBonusNote) {
    }

    public record AbilityScores(Integer strength, Integer dexterity, Integer constitution,
                                Integer intelligence, Integer wisdom, Integer charisma) {
    }

    public record Initiative(Integer value, Integer proficiency, String advantageMode) {
    }

    public record ArmorClass(Integer value, List<String> sources, String condition, Boolean braces, String special) {
    }

    public record HitPoints(BigDecimal average, String formula, String special) {
    }

    public record Movement(Map<String, MovementValue> modes, Boolean canHover, List<AlternateMovement> alternate,
                           Integer choiceAmount, List<String> choiceModes, String choiceNote, String note) {
    }

    public record MovementValue(Integer number, String condition) {
    }

    public record AlternateMovement(Integer number, String condition, String mode) {
    }

    public record ChallengeRating(String value, String covenValue, String lairValue,
                                  Integer experience, Integer lairExperience) {
    }

    public record ProficiencyBonus(Integer value, String note, String derivationRuleset) {
    }

    public sealed interface SkillValue permits SkillBonus, SkillAlternatives {
    }

    public record SkillBonus(String sourceBonus) implements SkillValue {
    }

    public record SkillAlternatives(List<Map<String, String>> choices) implements SkillValue {
    }

    public record PassivePerception(Integer value, String sourceText) {
    }

    public record NullableList<T>(boolean explicitNull, List<T> values) {
    }

    public record Defenses(NullableList<Defense> resistances, NullableList<Defense> immunities,
                           NullableList<Defense> vulnerabilities, NullableList<Defense> conditionImmunities) {
    }

    public record Defense(List<String> terms, Boolean conditional, String note, String preNote,
                          String postNote, String special, List<Defense> nested) {
    }

    public record Features(List<Feature> traits, List<Feature> actions, List<Feature> bonusActions,
                           List<Feature> reactions, List<Feature> legendaryActions, List<Feature> mythicActions,
                           List<Feature> variants, String actionNote, String reactionNote,
                           List<String> legendaryHeader, List<String> mythicHeader, List<String> reactionHeader) {
    }

    public record Feature(String name, List<MonsterContentDto> entries, String sourceType,
                          String source, Integer page, Reference token, VariantReference versionReference) {
    }

    public record VariantReference(String name, String addAs, String addHeadersAs) {
    }

    public record Combat(Integer legendaryActions, Integer legendaryActionsLair) {
    }

    public record SpellcastingBlock(String name, String type, String ability, String displayAs,
                                    List<MonsterContentDto> header, List<MonsterContentDto> footer,
                                    List<SpellReference> atWill, Map<String, SpellLevel> spellLevels,
                                    Map<String, Map<String, List<SpellReference>>> limitedUses,
                                    List<SpellReference> constant, List<SpellReference> rituals, Set<String> hidden,
                                    String chargesItem,
                                    Integer saveDc, Integer attackBonus, Integer casterLevel,
                                    Map<String, Integer> slots, Map<String, ContentValue> sourceProperties) {
    }

    public record SpellLevel(Integer slots, Integer lower, List<SpellReference> spells) {
    }

    public record SpellReference(String sourceText, Boolean hidden, String resolvedSpellId) {
    }

    public record Classification(Set<String> actionTags, Set<String> damageTags,
                                 Set<String> legendaryDamageTags, Set<String> spellDamageTags,
                                 Set<String> conditionInflict, Set<String> legendaryConditionInflict,
                                 Set<String> spellConditionInflict, Set<String> languageTags,
                                 Set<String> miscTags, Set<String> senseTags,
                                 Set<String> spellcastingTags, Set<String> traitTags) {
    }

    public record Catalog(Set<String> environments, List<GearReference> gear, List<String> attachedItems,
                          Set<String> treasure, Boolean familiar, Boolean hasFluff, Boolean hasFluffImages,
                          Boolean hasToken, Boolean customToken, String tokenCredit, Double foundryTokenScale,
                          List<AlternateArt> alternateArt, SoundReference sound,
                          RulesAvailability rulesAvailability, Boolean reprinted) {
    }

    public record GearReference(String item, String displayName, Integer quantity) {
    }

    public record AlternateArt(String name, String source, Integer page) {
    }

    public record SoundReference(String type, String path) {
    }

    public sealed interface MonsterContentDto permits PlainText, Section, ListContent, ListItem,
            TableContent, QuoteContent, InsetContent, SpellcastingContent {
    }

    public record PlainText(String text) implements MonsterContentDto {
    }

    public record Section(String sourceType, String name, String entry, List<MonsterContentDto> entries,
                          String source, Integer page,
                          Map<String, ContentValue> sourceProperties) implements MonsterContentDto {
    }

    public record ListContent(String style, List<MonsterContentDto> items,
                              Map<String, ContentValue> sourceProperties) implements MonsterContentDto {
    }

    public record ListItem(String name, String entry, List<MonsterContentDto> entries,
                           Map<String, ContentValue> sourceProperties) implements MonsterContentDto {
    }

    public record TableContent(String caption, List<String> columnLabels, List<String> columnStyles,
                               List<List<TableCellDto>> rows, String source, Integer page,
                               Map<String, ContentValue> sourceProperties) implements MonsterContentDto {
    }

    public record QuoteContent(String attribution, List<MonsterContentDto> entries,
                               Map<String, ContentValue> sourceProperties) implements MonsterContentDto {
    }

    public record InsetContent(String name, List<MonsterContentDto> entries, String source, Integer page,
                               Map<String, ContentValue> sourceProperties) implements MonsterContentDto {
    }

    public record SpellcastingContent(SpellcastingBlock spellcasting) implements MonsterContentDto {
    }

    public sealed interface ContentValue permits ContentText, ContentNumber, ContentBoolean, ContentNull, ContentArray, ContentObject {
    }

    public record ContentText(String value) implements ContentValue {
    }

    public record ContentNumber(BigDecimal value) implements ContentValue {
    }

    public record ContentBoolean(boolean value) implements ContentValue {
    }

    public record ContentNull() implements ContentValue {
    }

    public record ContentArray(List<ContentValue> values) implements ContentValue {
    }

    public record ContentObject(Map<String, ContentValue> properties) implements ContentValue {
    }

    public sealed interface TableCellDto permits TextCell, RollCell, StructuredCell {
    }

    public record TextCell(String text) implements TableCellDto {
    }

    public record RollCell(Integer exact, Integer minimum, Integer maximum, Boolean pad) implements TableCellDto {
    }

    public record StructuredCell(String sourceType, String name, List<MonsterContentDto> entries,
                                 Map<String, ContentValue> sourceProperties) implements TableCellDto {
    }
}
