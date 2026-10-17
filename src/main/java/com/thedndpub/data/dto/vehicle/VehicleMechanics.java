package com.thedndpub.data.dto.vehicle;

import com.thedndpub.data.dte.vehicle.VehicleActionDte;
import com.thedndpub.data.dte.vehicle.VehicleActionStationDte;
import com.thedndpub.data.dte.vehicle.VehicleActionThresholdsDte;
import com.thedndpub.data.dte.vehicle.VehicleArmorClassDte;
import com.thedndpub.data.dte.vehicle.VehicleControlDte;
import com.thedndpub.data.dte.vehicle.VehicleHitPointsDte;
import com.thedndpub.data.dte.vehicle.VehicleHullDte;
import com.thedndpub.data.dte.vehicle.VehicleMovementDte;
import com.thedndpub.data.dte.vehicle.VehicleNamedEntriesDte;
import com.thedndpub.data.dte.vehicle.VehicleNumberOrTextDte;
import com.thedndpub.data.dte.vehicle.VehiclePaceDte;
import com.thedndpub.data.dte.vehicle.VehicleSpeedDte;
import com.thedndpub.data.dte.vehicle.VehicleStationDte;
import com.thedndpub.data.dte.vehicle.VehicleStringOrListDte;
import com.thedndpub.data.dte.vehicle.VehicleWeaponDte;
import com.thedndpub.data.dte.vehicle.VehicleDte;

import java.util.List;

/** Structured vehicle statistics, separate from authored descriptive entries and importer flags. */
public record VehicleMechanics(
        VehicleStringOrListDte size,
        List<String> terrain,
        List<String> dimensions,
        Integer crewCapacity,
        String crewNote,
        Integer passengerCapacity,
        VehicleNumberOrTextDte cargoCapacity,
        Integer creatureCapacity,
        VehicleArmorClassDte armorClass,
        VehicleHitPointsDte hitPoints,
        VehicleSpeedDte speed,
        VehiclePaceDte pace,
        List<String> damageImmunities,
        List<String> conditionImmunities,
        Integer strength,
        Integer dexterity,
        Integer constitution,
        Integer intelligence,
        Integer wisdom,
        Integer charisma,
        VehicleHullDte hull,
        List<VehicleControlDte> controls,
        List<VehicleMovementDte> movement,
        VehicleActionThresholdsDte actionThresholds,
        List<VehicleWeaponDte> weapons,
        List<VehicleActionDte> actions,
        List<VehicleActionStationDte> actionStations,
        List<VehicleStationDte> stations,
        List<VehicleNamedEntriesDte> traits,
        List<VehicleNamedEntriesDte> reactions,
        Integer cost,
        Integer weight) {
    public VehicleMechanics {
        terrain = copy(terrain);
        dimensions = copy(dimensions);
        damageImmunities = copy(damageImmunities);
        conditionImmunities = copy(conditionImmunities);
        controls = copy(controls);
        movement = copy(movement);
        weapons = copy(weapons);
        actions = copy(actions);
        actionStations = copy(actionStations);
        stations = copy(stations);
        traits = copy(traits);
        reactions = copy(reactions);
    }

    public static VehicleMechanics fromSource(VehicleDte source) {
        return new VehicleMechanics(source.size(), source.terrain(), source.dimensions(), source.capCrew(),
                source.capCrewNote(), source.capPassenger(), source.capCargo(), source.capCreature(), source.ac(),
                source.hp(), source.speed(), source.pace(), source.immune(), source.conditionImmune(), source.str(),
                source.dex(), source.con(), source.intScore(), source.wis(), source.cha(), source.hull(),
                source.control(), source.movement(), source.actionThresholds(), source.weapon(), source.action(),
                source.actionStation(), source.station(), source.trait(), source.reaction(), source.cost(),
                source.weight());
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
