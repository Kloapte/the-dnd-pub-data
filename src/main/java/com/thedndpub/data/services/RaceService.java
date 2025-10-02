package com.thedndpub.data.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.assemblers.RaceAssembler;
import com.thedndpub.data.dte.race.RaceDte;
import com.thedndpub.data.dte.race.RacesWrapperDte;
import com.thedndpub.data.dte.race.SubraceDte;
import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.dto.race.SubraceDto;
import com.thedndpub.data.dto.race.SubraceSourceDto;
import com.thedndpub.data.dto.race.VariantDto;
import com.thedndpub.data.util.records.RaceKey;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class RaceService {

    private final RaceAssembler raceAssembler;

    @Getter
    private final HashMap<RaceKey, RaceDto> races;
    @Getter
    private final HashMap<RaceKey, RaceDto> subraces;
    @Getter
    private final HashMap<RaceKey, RaceDto> variants;

    public RaceService(RaceAssembler raceAssembler) throws JsonProcessingException {
        this.raceAssembler = raceAssembler;

        RacesWrapperDte wrapper = loadRacesFromJson();

        this.races = makeRaces(wrapper.getRace());
        this.subraces = makeSubraces(wrapper.getSubrace());
        List<RaceDto> combined = new ArrayList<>();
        combined.addAll(this.races.values().stream().toList());
        combined.addAll(this.subraces.values().stream().toList());
        this.variants = makeVariants(combined);
    }

    private RacesWrapperDte loadRacesFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data/races.json").getInputStream();
            return mapper.readValue(inputStream, RacesWrapperDte.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load races.json", e);
        }
    }

    private HashMap<RaceKey, RaceDto> makeRaces(List<RaceDte> racesDte) {
        HashMap<RaceKey, RaceDto> races = new HashMap<>();

        List<RaceDte> copyRaces = new ArrayList<>();
        for(RaceDte raceDte : racesDte) {
            if(raceDte.get_copy() == null) {
                RaceDto race = this.raceAssembler.mapRaceDteToDto(raceDte);
                races.putIfAbsent(this.keyFromDto(race), race);
            }
            else {
                copyRaces.add(raceDte);
            }
        }

        for(RaceDte copyRace : copyRaces) {
            RaceDto original = races.get(new RaceKey(copyRace.get_copy().getName(), copyRace.get_copy().getSource()));
            if(original != null) {
                RaceDto newCopy = this.raceAssembler.mapRaceCopyToDto(copyRace, original);
                races.putIfAbsent(this.keyFromDto(newCopy), newCopy);
            }
            else {
                throw new IllegalStateException("Parent of copy not found (name=" + copyRace.getName() + ", source=" + copyRace.getSource() + ")");
            }
        }

        return races;
    }

    private RaceKey keyFromDto(RaceDto dto) {
        return new RaceKey(dto.getName(), dto.getSource().getSource().name());
    }

    private HashMap<RaceKey, RaceDto> makeSubraces(List<SubraceDte> subracesDte) throws JsonProcessingException {
        HashMap<RaceKey,RaceDto> subraces = new HashMap<>();
        for(SubraceDte subraceDte : subracesDte) {
            SubraceDto subraceDto = this.raceAssembler.mapSubRaceToRaceDto(subraceDte);
            RaceDto parent = this.races.get(new RaceKey(subraceDto.getParent(), subraceDto.getParentSource().name()));
            if(parent != null) {
                if (parent.getSubraces() == null) {
                    parent.setSubraces(new ArrayList<>());
                }

                RaceDto subrace = this.raceAssembler.mapSubraceIntoParent(parent, subraceDto);
                parent.getSubraces().add(new SubraceSourceDto(subrace.getName(), subrace.getSource()));

                subraces.putIfAbsent(this.keyFromDto(subrace), subrace);
            }
            else {
                throw new IllegalStateException("Parent of subrace not found (name=" + subraceDte.getName() + ", source=" + subraceDte.getSource() + ")");
            }
        }

        return subraces;
    }

    private HashMap<RaceKey, RaceDto> makeVariants(List<RaceDto> races) throws JsonProcessingException {
        HashMap<RaceKey, RaceDto> variants = new HashMap<>();
        for(RaceDto parent : races) {
            if(parent.getVariants() != null) {
                for(VariantDto variant : parent.getVariants()) {
                    RaceDto variantRace = this.raceAssembler.mapVariantIntoParent(parent, variant);
                    variants.putIfAbsent(this.keyFromDto(variantRace), variantRace);
                }
            }
        }
        return variants;
    }
}
