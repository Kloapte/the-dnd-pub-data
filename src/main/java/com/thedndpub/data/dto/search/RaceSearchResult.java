package com.thedndpub.data.dto.search;

public class RaceSearchResult extends SearchResult {
    public String raceType;

    public RaceSearchResult() {
        this.type = SearchResultType.RACE;
    }
}
