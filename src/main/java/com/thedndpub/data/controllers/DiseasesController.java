package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.condition.DiseaseDto;
import com.thedndpub.data.dto.condition.DiseaseSummaryDto;
import com.thedndpub.data.services.DiseasesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseasesController {
    private final DiseasesService diseasesService;

    public DiseasesController(DiseasesService diseasesService) {
        this.diseasesService = diseasesService;
    }

    @GetMapping
    public List<DiseaseSummaryDto> findDiseases(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source) {
        return diseasesService.findDiseases(name, source);
    }

    @GetMapping("/{id}")
    public DiseaseDto findById(@PathVariable String id) {
        return diseasesService.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public List<DiseaseDto> findByName(
            @PathVariable String name,
            @RequestParam(required = false) String source) {
        return diseasesService.findByName(name, source);
    }
}
