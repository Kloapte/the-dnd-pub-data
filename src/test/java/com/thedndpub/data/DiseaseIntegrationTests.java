package com.thedndpub.data;

import com.thedndpub.data.dto.disease.DiseaseRecord;
import com.thedndpub.data.dto.disease.effects.DiseaseEffect;
import com.thedndpub.data.dto.disease.infection.DiseaseExposure;
import com.thedndpub.data.dto.disease.progression.DiseaseProgression;
import com.thedndpub.data.dto.disease.symptoms.DiseaseSymptom;
import com.thedndpub.data.repositories.disease.DiseaseRepository;
import com.thedndpub.data.services.disease.DiseaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DiseaseIntegrationTests {
    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void repositoryLoadsAllDiseasesAndTheirMechanics() {
        assertEquals(28, diseaseRepository.findAll().size());
        assertEquals(32, diseaseRepository.findAll().stream().mapToInt(disease -> disease.symptoms().size()).sum());
        assertEquals(34, diseaseRepository.findAll().stream().mapToInt(disease -> disease.effects().size()).sum());
        assertEquals(19, diseaseRepository.findAll().stream()
                .mapToInt(disease -> disease.progression().size())
                .sum());
        assertTrue(diseaseRepository.findAll().stream()
                .flatMap(disease -> disease.infection() == null
                        ? java.util.stream.Stream.empty()
                        : disease.infection().exposures().stream())
                .map(DiseaseExposure::kind)
                .distinct()
                .count() >= 8);
    }

    @Test
    void serviceFindsByIdAndReturnsAllExactNameMatches() {
        DiseaseRecord disease = diseaseService.getById("disease:xdmg:cackle-fever").orElseThrow();
        assertEquals("Cackle Fever", disease.name());
        assertTrue(diseaseService.getById("unknown").isEmpty());
        assertEquals(2, diseaseService.getByName("Cackle Fever").size());
        assertTrue(diseaseService.getByName("unknown").isEmpty());
    }

    @Test
    void mapsNestedPolymorphicDiseaseMechanics() {
        DiseaseRecord disease = diseaseService.getById("disease:dmg:cackle-fever").orElseThrow();
        assertTrue(disease.symptoms().stream().anyMatch(DiseaseSymptom.class::isInstance));
        assertTrue(disease.progression().stream().anyMatch(DiseaseProgression.class::isInstance));
        assertTrue(disease.recovery().getFirst().outcomeCounter() != null);
        assertTrue(disease.recovery().getFirst().outcomeCounter().onThreshold() != null);

        DiseaseRecord referenceDisease = diseaseService.getById("disease:gos:redface").orElseThrow();
        assertTrue(referenceDisease.effectProfile().sourceDisease().id().startsWith("disease:"));
    }

    @Test
    void exposesThreeReadEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/diseases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(28));

        mockMvc.perform(get("/api/diseases/{id}", "disease:xdmg:cackle-fever"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("disease:xdmg:cackle-fever"));

        mockMvc.perform(get("/api/diseases/name/{name}", "Cackle Fever"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/diseases/{id}", "unknown"))
                .andExpect(status().isNotFound());
    }
}
