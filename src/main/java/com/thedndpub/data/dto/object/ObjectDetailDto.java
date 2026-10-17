package com.thedndpub.data.dto.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dte.object.ObjectSpeedDte;
import com.thedndpub.data.dte.object.ObjectDefenseDte;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ObjectDetailDto(
        String id, String name, SourceReference source, String category, Boolean npc,
        List<String> size, ObjectStatisticDto armorClass, ObjectStatisticDto hitPoints,
        ObjectSpeedDte speed, Integer strength, Integer dexterity, Integer constitution,
        Integer intelligence, Integer wisdom, Integer charisma,
        List<ObjectDefenseDto> damageImmunities, List<ObjectDefenseDto> damageResistances,
        List<ObjectDefenseDto> damageVulnerabilities, List<String> conditionImmunities,
        Boolean srd, List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public ObjectDetailDto {
        size = copy(size);
        damageImmunities = copy(damageImmunities);
        damageResistances = copy(damageResistances);
        damageVulnerabilities = copy(damageVulnerabilities);
        conditionImmunities = copy(conditionImmunities);
        crossCatalogReferences = copy(crossCatalogReferences);
    }

    public static ObjectDetailDto from(ObjectDefinition definition) {
        return new ObjectDetailDto(definition.id(), definition.name(), definition.source(), definition.category(),
                definition.npc(), definition.size(), ObjectStatisticDto.from(definition.armorClass()),
                ObjectStatisticDto.from(definition.hitPoints()),
                definition.speed(), definition.strength(), definition.dexterity(), definition.constitution(),
                definition.intelligence(), definition.wisdom(), definition.charisma(),
                defenses(definition.damageImmunities()), defenses(definition.damageResistances()),
                defenses(definition.damageVulnerabilities()),
                definition.conditionImmunities(), definition.srd(), List.of());
    }

    public ObjectDetailDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new ObjectDetailDto(id, name, source, category, npc, size, armorClass, hitPoints, speed, strength,
                dexterity, constitution, intelligence, wisdom, charisma, damageImmunities, damageResistances,
                damageVulnerabilities, conditionImmunities, srd, references);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static List<ObjectDefenseDto> defenses(List<ObjectDefenseDte> values) {
        return values == null ? List.of() : values.stream().map(ObjectDefenseDto::from).toList();
    }
}
