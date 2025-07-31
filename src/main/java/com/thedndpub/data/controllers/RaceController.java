package com.thedndpub.data.controllers;

import com.thedndpub.data.assemblers.RaceAssembler;
import com.thedndpub.data.dte.race.*;
import com.thedndpub.data.dto.RaceDto;
import com.thedndpub.data.dto.WeaponProficiencyType;
import com.thedndpub.data.services.RaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/races")
public class RaceController {

    private final RaceService raceService;
    private final RaceAssembler raceAssembler;

    public RaceController(RaceService raceService, RaceAssembler raceAssembler) {
        this.raceService = raceService;
        this.raceAssembler = raceAssembler;
    }

    @GetMapping
    public List<RaceDto> getRaces() {
        List<RaceDto> races = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            races.add(this.raceAssembler.mapRaceDteToDto(race));
        }
        return races;
    }

    @GetMapping("/unmapped")
    public List<RaceDte> getRacesUnmapped() {
        return raceService.getAllRaces();
    }

    @GetMapping("/name")
    public List<String> getRaceNames() {
        return raceService.getAllRaces().stream().map((race) -> race.getName() + " - " + race.getSource()).toList();
    }

    @GetMapping("/name/{name}")
    public List<RaceDto> getRaceOnName(@PathVariable("name") String name) {
        return this.getRaces().stream().filter(race -> race.getName().contains(name)).toList();
    }

    @GetMapping("/name/{name}/weapon")
    public List<RaceDto> getRaceWeaponsOnName(@PathVariable("name") String name) {
        return this.getRaces().stream().filter(race -> race.getWeaponProficiencies() != null).toList();
    }


    @GetMapping("/name/{name}/unmapped")
    public List<RaceDte> getRaceUnmappedOnName(@PathVariable("name") String name) {
        return raceService.getAllRaces().stream().filter(race -> race.getName().contains(name)).toList();
    }

    @GetMapping("/subraces")
    public List<SubraceDte> getSubraces() {
        List<SubraceDte> subraces = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.get_versions() != null) {
                for(SubraceDte raceVersion : race.get_versions()) {
                    raceVersion.setRaceName(race.getName());
                }
                subraces.addAll(race.get_versions());
            }
        }
        subraces.addAll(raceService.getAllSubRaces());
        return subraces;
    }

    @GetMapping("/spells")
    public List<AdditionalSpellDte> getAdditionalSpells() {
        List<AdditionalSpellDte> spells = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getAdditionalSpells() != null && !race.getAdditionalSpells().isEmpty()) {
                spells.addAll(race.getAdditionalSpells());
            }
        }
        return spells;
    }

    @GetMapping("/speeds")
    public List<SpeedDte> getSpeeds() {
        List<SpeedDte> speeds = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getAdditionalSpells() != null && !race.getAdditionalSpells().isEmpty()) {
                speeds.add(race.getSpeed());
            }
        }
        return speeds;
    }

    @GetMapping("/abilities")
    public List<AbilityDte> getAbilities() {
        List<AbilityDte> abilities = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getAbility() != null && !race.getAbility().isEmpty()) {
                for(AbilityDte ability : race.getAbility()) {
                    ability.setName(race.getName());
                    if(ability.getChoose() != null && ability.getChoose().getCount() > 1) {
                        abilities.add(ability);
                    }
                }
            }
        }
        return abilities;
    }

    @GetMapping("/weapons")
    public List<WeaponProficiencyDte> getWeapons() {
        List<WeaponProficiencyDte> weapons = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getWeaponProficiencies() != null && !race.getWeaponProficiencies().isEmpty()) {
                for(WeaponProficiencyDte weapon : race.getWeaponProficiencies()) {
                    weapon.setName(race.getName());
                    if(weapon.isLightCrossbow()) {
                        weapons.add(weapon);
                    }
                }
            }
        }
        return weapons;
    }

    @GetMapping("/tools")
    public List<ToolProficiencyDte> getTools() {
        List<ToolProficiencyDte> tools = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getToolProficiencies() != null && !race.getToolProficiencies().isEmpty()) {
                for(ToolProficiencyDte tool : race.getToolProficiencies()) {
                    tool.setName(race.getName());
//                    if(tool) {
                        tools.add(tool);
//                    }
                }
            }
        }
        return tools;
    }

    @GetMapping("/source")
    public List<RaceDte> getSources() {
        List<RaceDte> sources = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getReprintedAs() != null && race.getReprintedAs().size() > 1) {
                sources.add(race);
            }
        }
        return sources;
    }

    @GetMapping("/traits")
    public List<String> getTypes() {
        List<String> types = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getTraitTags() != null && race.getTraitTags().size() > 1) {
                types.add(race.getName());
                types.addAll(race.getTraitTags());
            }
        }
        return types;
    }

    @GetMapping("/conditions")
    public List<String> getConditions() {
        List<String> types = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getImmune() != null) {
                types.add(race.getName());
                types.addAll(race.getImmune());
            }
        }
        return types;
    }

    @GetMapping("/feats")
    public List<FeatDte> getFeats() {
        List<FeatDte> feats = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getFeats() != null) {
                feats.addAll(race.getFeats());
            }
        }
        return feats;
    }
}