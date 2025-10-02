package com.thedndpub.data.services;

import com.thedndpub.data.assemblers.SearchAssembler;
import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.dto.race.SourceType;
import com.thedndpub.data.dto.search.SearchResult;
import com.thedndpub.data.dto.search.SearchSourceType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class SearchService {
    private final SearchAssembler searchAssembler;
    private final RaceService raceService;

    public SearchService(SearchAssembler searchAssembler, RaceService raceService) {
        this.searchAssembler = searchAssembler;
        this.raceService = raceService;
    }

    public List<SearchResult> searchRaces(SearchSourceType source, String query, boolean exact) {
        List<SearchResult> races = this.searchRaces(this.raceService.getRaces().values().stream(), source, query, exact).stream().map((race) -> { return searchAssembler.mapRaceToSearchResult(race, "race"); }).toList();
        List<SearchResult> subraces = this.searchRaces(this.raceService.getSubraces().values().stream(), source, query, exact).stream().map((race) -> { return searchAssembler.mapRaceToSearchResult(race, "subrace"); }).toList();
        List<SearchResult> variants = this.searchRaces(this.raceService.getVariants().values().stream(), source, query, exact).stream().map((race) -> { return searchAssembler.mapRaceToSearchResult(race, "variant"); }).toList();

        List<SearchResult> foundRaces = new ArrayList<>();
        foundRaces.addAll(races);
        foundRaces.addAll(subraces);
        foundRaces.addAll(variants);

        return foundRaces;
    }

    private List<RaceDto> searchRaces(Stream<RaceDto> input, SearchSourceType source, String query, boolean exact) {
        return input.filter((raceDto -> {
            boolean sourceMatches = false;
            if(source.equals(SearchSourceType.PHB) && raceDto.getSource().getSource().equals(SourceType.PHB)) {
                sourceMatches = true;
            }
            else if(source.equals(SearchSourceType.XPHB) && raceDto.getSource().getSource().equals(SourceType.XPHB)) {
                sourceMatches = true;
            }
            else if(source.equals(SearchSourceType.ALL)) {
                sourceMatches = true;
            }

            if(sourceMatches) {
                if(exact) {
                    return raceDto.getName().equalsIgnoreCase(query);
                }
                else {
                    return raceDto.getName().toLowerCase().contains(query.toLowerCase());
                }
            }
            return false;
        })).toList();
    }
}
