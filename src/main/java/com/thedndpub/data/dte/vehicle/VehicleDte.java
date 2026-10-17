package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record VehicleDte(
        String name,
        String source,
        Integer page,
        String srd,
        List<String> reprintedAs,
        String vehicleType,
        VehicleStringOrListDte size,
        List<String> terrain,
        Integer capCrew,
        Integer capPassenger,
        VehicleArmorClassDte ac,
        VehicleHitPointsDte hp,
        VehicleSpeedDte speed,
        List<String> immune,
        List<EntryDte> entries,
        Boolean hasToken,
        Boolean hasFluffImages,
        List<String> dimensions,
        VehiclePaceDte pace,
        List<VehicleWeaponDte> weapon,
        Boolean hasFluff,
        VehicleNumberOrTextDte capCargo,
        Integer str,
        Integer dex,
        Integer con,
        @JsonProperty("int") Integer intScore,
        Integer wis,
        Integer cha,
        List<String> conditionImmune,
        VehicleHullDte hull,
        List<VehicleControlDte> control,
        List<VehicleMovementDte> movement,
        VehicleActionThresholdsDte actionThresholds,
        List<VehicleActionDte> action,
        Integer cost,
        Integer weight,
        Integer capCreature,
        List<VehicleNamedEntriesDte> trait,
        List<VehicleActionStationDte> actionStation,
        List<VehicleNamedEntriesDte> reaction,
        String tokenCredit,
        Boolean tokenCustom,
        List<VehicleOtherSourceDte> otherSources,
        String capCrewNote,
        List<VehicleStationDte> station,
        String type) {
    public VehicleDte {
        reprintedAs = copy(reprintedAs);
        terrain = copy(terrain);
        immune = copy(immune);
        entries = copy(entries);
        dimensions = copy(dimensions);
        weapon = copy(weapon);
        conditionImmune = copy(conditionImmune);
        control = copy(control);
        movement = copy(movement);
        action = copy(action);
        trait = copy(trait);
        actionStation = copy(actionStation);
        reaction = copy(reaction);
        otherSources = copy(otherSources);
        station = copy(station);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
