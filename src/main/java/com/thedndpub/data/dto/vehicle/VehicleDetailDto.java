package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleDetailDto(
        VehicleId id,
        String name,
        SourceReference source,
        String vehicleType,
        List<String> size,
        List<String> terrain,
        Integer crewCapacity,
        Integer passengerCapacity,
        VehicleCapacityDto cargoCapacity,
        Integer creatureCapacity,
        List<String> dimensions,
        VehicleSpeedDto speed,
        VehiclePaceDto pace,
        VehicleHullDto hull,
        List<VehicleMovementComponentDto> movement,
        VehicleActionThresholdsDto actionThresholds,
        List<VehicleComponentDto> weapons,
        List<VehicleMovementComponentDto> controls,
        List<VehicleStationDto> stations,
        VehicleAbilityScoresDto abilityScores,
        Integer cost,
        Integer weight,
        List<String> damageImmunities,
        List<String> conditionImmunities,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public VehicleDetailDto {
        size = copy(size);
        terrain = copy(terrain);
        dimensions = copy(dimensions);
        movement = movement == null ? List.of() : List.copyOf(movement);
        weapons = weapons == null ? List.of() : List.copyOf(weapons);
        controls = copy(controls);
        stations = stations == null ? List.of() : List.copyOf(stations);
        damageImmunities = copy(damageImmunities);
        conditionImmunities = copy(conditionImmunities);
        crossCatalogReferences = copy(crossCatalogReferences);
    }

    public static VehicleDetailDto from(VehicleDefinition definition) {
        var data = definition.mechanics();
        List<VehicleComponentDto> weapons = data.weapons().stream()
                .map(value -> new VehicleComponentDto(value.name(), value.ac(), value.hp(), value.dt(), value.crew(),
                        value.count(), value.size() == null ? List.of() : value.size().values(),
                        value.costs() == null ? List.of()
                                : value.costs().stream().map(cost -> new VehicleWeaponCostDto(cost.cost())).toList()))
                .toList();
        List<VehicleMovementComponentDto> movement = data.movement().stream()
                .map(value -> new VehicleMovementComponentDto(value.name(), value.ac(), value.hp(), value.isControl()))
                .toList();
        List<VehicleMovementComponentDto> controls = data.controls().stream()
                .map(value -> new VehicleMovementComponentDto(value.name(), value.ac(), value.hp(), null))
                .toList();
        List<VehicleStationDto> stations = data.stations().stream()
                .map(value -> new VehicleStationDto(value.name(), value.ac(), value.hp(), value.crew(),
                        value.size() == null ? List.of() : value.size().values()))
                .toList();
        VehicleAbilityScoresDto abilityScores = data.strength() == null && data.dexterity() == null
                && data.constitution() == null && data.intelligence() == null && data.wisdom() == null
                && data.charisma() == null
                ? null
                : new VehicleAbilityScoresDto(data.strength(), data.dexterity(), data.constitution(),
                        data.intelligence(), data.wisdom(), data.charisma());
        return new VehicleDetailDto(definition.id(), definition.name(), definition.source(), definition.vehicleType(),
                data.size() == null ? List.of() : data.size().values(), data.terrain(), data.crewCapacity(),
                data.passengerCapacity(), VehicleDtoProjection.capacity(data.cargoCapacity()), data.creatureCapacity(),
                data.dimensions(), VehicleDtoProjection.speed(data.speed()), VehicleDtoProjection.pace(data.pace()),
                VehicleDtoProjection.hull(data.hull()), movement,
                VehicleDtoProjection.thresholds(data.actionThresholds()),
                weapons, controls, stations, abilityScores, data.cost(), data.weight(), data.damageImmunities(),
                data.conditionImmunities(),
                List.of());
    }

    public VehicleDetailDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new VehicleDetailDto(id, name, source, vehicleType, size, terrain, crewCapacity, passengerCapacity,
                cargoCapacity, creatureCapacity, dimensions, speed, pace, hull, movement, actionThresholds, weapons,
                controls, stations, abilityScores, cost, weight, damageImmunities, conditionImmunities, references);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
