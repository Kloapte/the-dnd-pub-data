package com.thedndpub.data;

import com.thedndpub.data.dto.source.PublicationGroup;
import com.thedndpub.data.dto.source.PublicationKind;
import com.thedndpub.data.dto.source.Source;
import com.thedndpub.data.repositories.source.SourceRepository;
import com.thedndpub.data.services.source.SourceService;
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
class SourceIntegrationTests {
    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void repositoryLoadsAllSources() {
        assertEquals(181, sourceRepository.findAll().size());
        assertEquals(167, sourceRepository.findAll().stream()
                .mapToInt(source -> source.publications().size())
                .sum());
    }

    @Test
    void serviceFindsByCodeAndPublicationName() {
        Source source = sourceService.getById("PHB").orElseThrow();
        assertEquals(PublicationGroup.CORE, source.publications().getFirst().group());
        assertEquals("PHB", source.code());

        assertEquals("MOT", sourceService.getByName("Mythic Odysseys of Theros").orElseThrow().code());
        assertTrue(sourceService.getById("unknown").isEmpty());
    }

    @Test
    void mapsOptionalPublicationMetadataAndEnums() {
        Source codeOnly = sourceService.getById("EEPC").orElseThrow();
        assertTrue(codeOnly.publications().isEmpty());
        assertTrue(codeOnly.codeAliases().isEmpty());

        Source mot = sourceService.getById("MOT").orElseThrow();
        assertEquals(PublicationKind.ADVENTURE, mot.publications().get(1).kind());
        assertEquals("MOT-NSS", mot.publications().get(1).publicationId());
    }

    @Test
    void exposesThreeReadEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/sources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(181));

        mockMvc.perform(get("/api/sources/{id}", "PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PHB"));

        mockMvc.perform(get("/api/sources/name/{name}", "Mythic Odysseys of Theros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("MOT"));

        mockMvc.perform(get("/api/sources/{id}", "unknown"))
                .andExpect(status().isNotFound());
    }
}
