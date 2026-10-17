package com.thedndpub.data.dto.feat;

import com.thedndpub.data.dto.race.RaceGrantChoiceDto;
import com.thedndpub.data.dto.race.RaceGrantDto;
import com.thedndpub.data.dto.race.RaceGrantGroupDto;
import com.thedndpub.data.dto.race.RaceResistanceDto;
import com.thedndpub.data.dte.feat.FeatBooleanGrantDte;
import com.thedndpub.data.dte.feat.FeatChoiceGrantDte;
import com.thedndpub.data.dte.feat.FeatChoiceResistanceDte;
import com.thedndpub.data.dte.feat.FeatCountGrantDte;
import com.thedndpub.data.dte.feat.FeatDte;
import com.thedndpub.data.dte.feat.FeatGrantChoiceDte;
import com.thedndpub.data.dte.feat.FeatGrantDte;
import com.thedndpub.data.dte.feat.FeatGrantValueDte;
import com.thedndpub.data.dte.feat.FeatNamedResistanceDte;
import com.thedndpub.data.dte.feat.FeatOptionalFeatureProgressionDte;
import com.thedndpub.data.dte.feat.FeatResistanceDte;
import com.thedndpub.data.dte.feat.FeatSenseGrantDte;
import com.thedndpub.data.dte.feat.FeatSkillToolLanguageChoiceDte;

import java.util.List;

/** Projects only existing typed fields; entries and prerequisite interpretation are excluded. */
public final class FeatMechanicsMapper {
    private FeatMechanicsMapper() {
    }

    public static FeatMechanics map(FeatDte source) {
        FeatProficiencies proficiencies = proficiencies(source);
        FeatDefenses defenses = defenses(source);
        if (proficiencies == null && defenses == null && source.senses() == null
                && source.bonusSenses() == null && source.optionalfeatureProgression() == null) {
            return null;
        }
        return new FeatMechanics(proficiencies, defenses, senses(source.senses()), senses(source.bonusSenses()),
                progressions(source.optionalfeatureProgression()));
    }

    private static FeatProficiencies proficiencies(FeatDte source) {
        if (source.skillProficiencies() == null && source.expertise() == null
                && source.languageProficiencies() == null && source.toolProficiencies() == null
                && source.weaponProficiencies() == null && source.armorProficiencies() == null
                && source.savingThrowProficiencies() == null && source.skillToolLanguageProficiencies() == null) {
            return null;
        }
        return new FeatProficiencies(grants(source.skillProficiencies()), grants(source.expertise()),
                grants(source.languageProficiencies()), grants(source.toolProficiencies()),
                grants(source.weaponProficiencies()), grants(source.armorProficiencies()),
                grants(source.savingThrowProficiencies()), combined(source.skillToolLanguageProficiencies()));
    }

    private static List<RaceGrantGroupDto> grants(List<FeatGrantDte> groups) {
        if (groups == null) {
            return null;
        }
        return groups.stream().map(group -> new RaceGrantGroupDto(group.values().entrySet().stream()
                .map(entry -> grant(entry.getKey(), entry.getValue())).toList())).toList();
    }

    private static RaceGrantDto grant(String key, FeatGrantValueDte value) {
        if (value instanceof FeatBooleanGrantDte flag) {
            return new RaceGrantDto(key, flag.value(), null, null);
        }
        if (value instanceof FeatCountGrantDte count) {
            return new RaceGrantDto(key, null, count.value(), null);
        }
        if (value instanceof FeatChoiceGrantDte choice) {
            return new RaceGrantDto(key, null, null, choice(choice.value()));
        }
        throw new IllegalArgumentException("Unsupported Feat grant value for key '" + key + "'");
    }

    private static RaceGrantChoiceDto choice(FeatGrantChoiceDte value) {
        return new RaceGrantChoiceDto(value.count(), value.from(), List.of(), value.fromFilter());
    }

    private static List<FeatCombinedGrant> combined(List<FeatSkillToolLanguageChoiceDte> groups) {
        if (groups == null) {
            return null;
        }
        return groups.stream().map(group -> new FeatCombinedGrant(group.choose() == null ? null
                : group.choose().stream().map(FeatMechanicsMapper::choice).toList())).toList();
    }

    private static FeatDefenses defenses(FeatDte source) {
        if (source.conditionImmune() == null && source.immune() == null && source.resist() == null) {
            return null;
        }
        return new FeatDefenses(source.conditionImmune(), source.immune(), resistances(source.resist()));
    }

    private static List<RaceResistanceDto> resistances(List<FeatResistanceDte> values) {
        if (values == null) {
            return null;
        }
        return values.stream().map(value -> {
            if (value instanceof FeatNamedResistanceDte named) {
                return new RaceResistanceDto(named.type(), null);
            }
            if (value instanceof FeatChoiceResistanceDte choice) {
                return new RaceResistanceDto(null, choice(choice.choose()));
            }
            throw new IllegalArgumentException("Unsupported Feat resistance value");
        }).toList();
    }

    private static List<FeatSenseValues> senses(List<FeatSenseGrantDte> values) {
        return values == null ? null : values.stream().map(value -> new FeatSenseValues(value.ranges())).toList();
    }

    private static List<FeatOptionalFeatureProgression> progressions(List<FeatOptionalFeatureProgressionDte> values) {
        if (values == null) {
            return null;
        }
        return values.stream().map(value -> new FeatOptionalFeatureProgression(
                value.name(), value.featureType(), value.progression())).toList();
    }
}
