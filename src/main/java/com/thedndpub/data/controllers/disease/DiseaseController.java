package com.thedndpub.data.controllers.disease;

import com.thedndpub.data.dto.disease.DiseaseRecord;
import com.thedndpub.data.services.disease.DiseaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
@Tag(name = "Diseases", description = "Canonical disease mechanics")
public class DiseaseController {
    private final DiseaseService diseaseService;

    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    @GetMapping
    @Operation(summary = "Get all diseases")
    @ApiResponse(responseCode = "200", description = "Diseases in normalized dataset order")
    public List<DiseaseRecord> getAll() {
        return diseaseService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a disease by its exact identifier")
    @ApiResponse(responseCode = "404", description = "No disease has this identifier")
    public DiseaseRecord getById(@Parameter(description = "Exact disease identifier") @PathVariable String id) {
        return diseaseService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disease not found: " + id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get diseases by exact name")
    @ApiResponse(responseCode = "200", description = "All diseases with this exact name")
    public List<DiseaseRecord> getByName(@Parameter(description = "Exact disease name") @PathVariable String name) {
        return diseaseService.getByName(name);
    }
}
