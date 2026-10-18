package com.thedndpub.data.dto.spell.api;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellConsumeFlag;
import com.thedndpub.data.dto.spell.SpellConsumeNote;
import com.thedndpub.data.dto.spell.SpellMaterialDetails;
import com.thedndpub.data.dto.spell.SpellMaterialText;
import com.thedndpub.data.dto.spell.SpellSchool;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionDto;
import com.thedndpub.data.services.SpellFormulaProjectionCatalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public final class SpellPublicMapper {
    private final SpellFormulaProjectionCatalog formulaProjection;

    /** Standalone legacy mapping leaves status unavailable rather than assuming no observations. */
    public SpellPublicMapper() {
        formulaProjection = null;
    }

    @Autowired
    public SpellPublicMapper(SpellFormulaProjectionCatalog formulaProjection) {
        this.formulaProjection = Objects.requireNonNull(formulaProjection, "formulaProjection");
    }

    public SpellSummaryDto summary(SpellDefinition spell) {
        List<SpellCastingTimeDto> castingTimes = spell.castingTimes().stream()
                .map(time -> new SpellCastingTimeDto(
                        time.number(), time.unit(), present(time.condition()), present(time.note())))
                .toList();
        List<String> damageTypes = spell.mechanics().damageInflicted();

        return new SpellSummaryDto(
                spell.id().value(),
                spell.identity().name(),
                spell.source(),
                spell.level(),
                school(spell),
                spell.ritual(),
                concentration(spell),
                castingTimes,
                range(spell),
                damageTypes);
    }

    public SpellDto detail(SpellDefinition spell) {
        return detail(spell, null);
    }

    public SpellDto detail(SpellDefinition spell, SpellMechanicsInspectionDto inspection) {
        return new SpellDto(
                spell.id().value(),
                spell.identity().name(),
                spell.source(),
                spell.aliases(),
                spell.level(),
                school(spell),
                spell.castingTimes().stream()
                        .map(time -> new SpellCastingTimeDto(
                                time.number(), time.unit(), present(time.condition()), present(time.note())))
                        .toList(),
                spell.ritual(),
                range(spell),
                components(spell),
                spell.durations().stream()
                        .map(duration -> new SpellDurationDto(
                                duration.type(),
                                duration.concentration(),
                                duration.duration() == null ? null : new SpellDurationAmountDto(
                                        duration.duration().type(),
                                        duration.duration().amount(),
                                        duration.duration().upTo()),
                                duration.ends()))
                        .toList(),
                mechanics(spell),
                spell.scaling().stream()
                        .map(scaling -> new SpellScalingDto(scaling.label(), scaling.values().stream()
                                .map(value -> new SpellScalingValueDto(value.levelKey(), value.formula()))
                                .toList()))
                        .toList(),
                sourceRelationships(spell),
                formulaProjection == null ? null : formulaProjection.statusFor(spell.id()),
                inspection);
    }

    private SpellRangeDto range(SpellDefinition spell) {
        if (spell.range() == null) {
            return null;
        }
        var sourceDistance = spell.range().distance();
        SpellDistanceDto distance = sourceDistance == null
                ? null
                : new SpellDistanceDto(sourceDistance.type(), sourceDistance.amount());
        return new SpellRangeDto(spell.range().type(), distance);
    }

    private SpellSchoolDto school(SpellDefinition spell) {
        SpellSchool school = SpellSchool.fromCode(spell.school());
        return new SpellSchoolDto(school.code(), school.displayName());
    }

    private SpellComponentsDto components(SpellDefinition spell) {
        if (spell.components() == null) {
            return null;
        }
        return new SpellComponentsDto(
                spell.components().verbal(),
                spell.components().somatic(),
                spell.components().royal(),
                material(spell));
    }

    private SpellMaterialDto material(SpellDefinition spell) {
        return switch (spell.components().material()) {
            case null -> null;
            case SpellMaterialText ignored -> new SpellMaterialDto(true, null, null);
            case SpellMaterialDetails details -> new SpellMaterialDto(
                    true, details.cost(), consumed(details));
        };
    }

    private Boolean consumed(SpellMaterialDetails material) {
        return switch (material.consume()) {
            case null -> null;
            case SpellConsumeFlag flag -> flag.value();
            case SpellConsumeNote ignored -> null;
        };
    }

    private SpellMechanicsDto mechanics(SpellDefinition spell) {
        var mechanics = spell.mechanics();
        if (mechanics.abilityChecks().isEmpty()
                && mechanics.affectsCreatureTypes().isEmpty()
                && mechanics.areaTags().isEmpty()
                && mechanics.conditionImmunities().isEmpty()
                && mechanics.conditionsInflicted().isEmpty()
                && mechanics.damageImmunities().isEmpty()
                && mechanics.damageInflicted().isEmpty()
                && mechanics.damageResistances().isEmpty()
                && mechanics.damageVulnerabilities().isEmpty()
                && mechanics.miscellaneousTags().isEmpty()
                && mechanics.savingThrows().isEmpty()
                && mechanics.spellAttacks().isEmpty()) {
            return null;
        }
        return new SpellMechanicsDto(
                mechanics.abilityChecks(),
                mechanics.affectsCreatureTypes(),
                mechanics.areaTags(),
                mechanics.conditionImmunities(),
                mechanics.conditionsInflicted(),
                mechanics.damageImmunities(),
                mechanics.damageInflicted(),
                mechanics.damageResistances(),
                mechanics.damageVulnerabilities(),
                mechanics.miscellaneousTags(),
                mechanics.savingThrows(),
                mechanics.spellAttacks());
    }

    private SpellSourceRelationshipsDto sourceRelationships(SpellDefinition spell) {
        var relationships = spell.sourceRelationships();
        if (relationships.additionalSources().isEmpty()
                && relationships.otherSources().isEmpty()
                && relationships.referenceSources().isEmpty()
                && relationships.reprintedAs().isEmpty()) {
            return null;
        }
        return new SpellSourceRelationshipsDto(
                relationships.additionalSources(),
                relationships.otherSources(),
                relationships.referenceSources(),
                relationships.reprintedAs().stream()
                        .map(reprint -> new SpellReprintDto(
                                reprint.targetId().value(),
                                reprint.target().name(),
                                new SourceReference(reprint.target().source(), null)))
                        .toList());
    }

    private Boolean concentration(SpellDefinition spell) {
        if (spell.durations().stream().anyMatch(duration -> Boolean.TRUE.equals(duration.concentration()))) {
            return true;
        }
        if (!spell.durations().isEmpty()
                && spell.durations().stream().allMatch(duration -> Boolean.FALSE.equals(duration.concentration()))) {
            return false;
        }
        return null;
    }

    private Boolean present(String value) {
        return value == null ? null : true;
    }
}
