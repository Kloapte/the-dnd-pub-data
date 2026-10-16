package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.book.PublicationDto;
import com.thedndpub.data.dto.book.PublicationSummaryDto;
import com.thedndpub.data.services.PublicationsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BooksController {
    private final PublicationsService publicationsService;

    public BooksController(PublicationsService publicationsService) {
        this.publicationsService = publicationsService;
    }

    @GetMapping
    public List<PublicationSummaryDto> findBooks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source) {
        return publicationsService.findPublications(name, source);
    }

    @GetMapping("/{id}")
    public PublicationDto findById(@PathVariable String id) {
        return publicationsService.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<PublicationDto> findByName(@PathVariable String name) {
        return publicationsService.findByName(name);
    }
}
