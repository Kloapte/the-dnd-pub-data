package com.thedndpub.data.controllers;

import com.thedndpub.data.assemblers.RaceAssembler;
import com.thedndpub.data.dte.race.AdditionalSpellDte;
import com.thedndpub.data.dte.race.RaceDte;
import com.thedndpub.data.dte.race.SubraceDte;
import com.thedndpub.data.dte.race.VersionDte;
import com.thedndpub.data.dto.RaceDto;
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

    @GetMapping("/name")
    public List<String> getRaceNames() {
        return raceService.getAllRaces().stream().map((race) -> race.getName() + " - " + race.getSource()).toList();
    }

    @GetMapping("/name/{name}")
    public List<RaceDte> getRaceOnName(@PathVariable("name") String name) {
        System.out.println("Filtering in name: " + name);
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
}