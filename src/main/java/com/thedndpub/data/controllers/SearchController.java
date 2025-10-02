package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.search.SearchRequest;
import com.thedndpub.data.dto.search.SearchResult;
import com.thedndpub.data.services.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping()
    public List<SearchResult> search(@RequestBody SearchRequest request) {
        List<SearchResult> results = new ArrayList<>();

        if(request.races()) {
            results.addAll(this.searchService.searchRaces(request.source(), request.query(), request.exact()));
        }

        return results;
    }
}
