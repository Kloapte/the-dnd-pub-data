package com.thedndpub.data.dto.monster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.monster.catalog.Catalog;
import com.thedndpub.data.dto.monster.content.ContentArray;
import com.thedndpub.data.dto.monster.content.ContentBoolean;
import com.thedndpub.data.dto.monster.content.ContentNull;
import com.thedndpub.data.dto.monster.content.ContentNumber;
import com.thedndpub.data.dto.monster.content.ContentObject;
import com.thedndpub.data.dto.monster.content.ContentText;
import com.thedndpub.data.dto.monster.content.ContentValue;
import com.thedndpub.data.dto.monster.content.InsetContent;
import com.thedndpub.data.dto.monster.content.ListContent;
import com.thedndpub.data.dto.monster.content.ListItem;
import com.thedndpub.data.dto.monster.content.MonsterContentDto;
import com.thedndpub.data.dto.monster.content.PlainText;
import com.thedndpub.data.dto.monster.content.QuoteContent;
import com.thedndpub.data.dto.monster.content.RollCell;
import com.thedndpub.data.dto.monster.content.Section;
import com.thedndpub.data.dto.monster.content.SpellcastingContent;
import com.thedndpub.data.dto.monster.content.StructuredCell;
import com.thedndpub.data.dto.monster.content.TableCellDto;
import com.thedndpub.data.dto.monster.content.TableContent;
import com.thedndpub.data.dto.monster.content.TextCell;
import com.thedndpub.data.dto.monster.defenses.Defense;
import com.thedndpub.data.dto.monster.defenses.Defenses;
import com.thedndpub.data.dto.monster.features.Combat;
import com.thedndpub.data.dto.monster.features.Feature;
import com.thedndpub.data.dto.monster.features.Features;
import com.thedndpub.data.dto.monster.features.VariantReference;
import com.thedndpub.data.dto.monster.identity.Alignment;
import com.thedndpub.data.dto.monster.identity.CreatureType;
import com.thedndpub.data.dto.monster.identity.Identity;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dto.monster.identity.Reference;
import com.thedndpub.data.dto.monster.identity.RulesAvailability;
import com.thedndpub.data.dto.monster.identity.ShortName;
import com.thedndpub.data.dto.monster.identity.Source;
import com.thedndpub.data.dto.monster.identity.SourceReference;
import com.thedndpub.data.dto.monster.identity.Summoning;
import com.thedndpub.data.dto.monster.identity.TypeTag;
import com.thedndpub.data.dto.monster.spellcasting.SpellLevel;
import com.thedndpub.data.dto.monster.spellcasting.SpellReference;
import com.thedndpub.data.dto.monster.spellcasting.SpellcastingBlock;
import com.thedndpub.data.dto.monster.statistics.Ability;
import com.thedndpub.data.dto.monster.statistics.AbilityScores;
import com.thedndpub.data.dto.monster.statistics.AlternateMovement;
import com.thedndpub.data.dto.monster.statistics.ArmorClass;
import com.thedndpub.data.dto.monster.statistics.ChallengeRating;
import com.thedndpub.data.dto.monster.statistics.HitPoints;
import com.thedndpub.data.dto.monster.statistics.Initiative;
import com.thedndpub.data.dto.monster.statistics.Movement;
import com.thedndpub.data.dto.monster.statistics.MovementValue;
import com.thedndpub.data.dto.monster.statistics.NullableList;
import com.thedndpub.data.dto.monster.statistics.PassivePerception;
import com.thedndpub.data.dto.monster.statistics.ProficiencyBonus;
import com.thedndpub.data.dto.monster.statistics.SkillAlternatives;
import com.thedndpub.data.dto.monster.statistics.SkillBonus;
import com.thedndpub.data.dto.monster.statistics.SkillValue;
import com.thedndpub.data.dto.monster.statistics.Statistics;
import java.util.List;

/**
 * Application-facing, reusable monster definition. It contains no encounter state.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MonsterDto(Identity identity, Statistics statistics, Defenses defenses,
                         Features features, Combat combat, List<SpellcastingBlock> spellcasting,
                         Catalog catalog) {
    public MonsterDto {
        spellcasting = List.copyOf(spellcasting);
    }
}
