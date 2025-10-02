package com.thedndpub.data.assemblers;

import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.dto.search.RaceSearchResult;
import com.thedndpub.data.dto.search.SearchResult;
import org.springframework.stereotype.Component;

@Component
public class SearchAssembler {
    public SearchResult mapRaceToSearchResult(RaceDto race, String raceType) {
        RaceSearchResult result = new RaceSearchResult();
        result.title = race.getName();
        result.description = race.getSource().getDescription();
        result.raceType = raceType;
        result.path = "/race/" + race.getName() + "/" + race.getSource().getSource().name();

        return result;
    }
}
