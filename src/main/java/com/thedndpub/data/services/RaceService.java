package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.RaceDte;
import com.thedndpub.data.dte.race.RacesWrapperDte;
import com.thedndpub.data.dte.race.SubraceDte;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class RaceService {

    private final List<RaceDte> races;
    private final List<SubraceDte> subRaces;

    public RaceService() {
        RacesWrapperDte wrapper = loadRacesFromJson();
        this.races = wrapper.getRace();
        this.subRaces = wrapper.getSubrace();
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

    public List<RaceDte> getAllRaces() {
        return races;
    }

    public List<SubraceDte> getAllSubRaces() {
        return subRaces;
    }
}
