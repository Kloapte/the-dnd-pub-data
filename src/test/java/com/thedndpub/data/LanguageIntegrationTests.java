package com.thedndpub.data;

import com.thedndpub.data.dto.language.LanguageRecord;
import com.thedndpub.data.dto.language.communication.LanguageConcealedMessage;
import com.thedndpub.data.dto.language.speakers.FilteredCreatureSpeaker;
import com.thedndpub.data.dto.language.speakers.ReferencedCreatureSpeaker;
import com.thedndpub.data.repositories.language.LanguageRepository;
import com.thedndpub.data.services.language.LanguageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class LanguageIntegrationTests {
    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void repositoryLoadsAllLanguagesAndNestedData() {
        assertEquals(201, languageRepository.findAll().size());
        assertEquals(138, languageRepository.findAll().stream()
                .mapToInt(language -> language.typicalSpeakers().size())
                .sum());
        assertEquals(2, languageRepository.findAll().stream()
                .mapToInt(language -> language.communicationCapabilities().size())
                .sum());
    }

    @Test
    void serviceFindsByIdAndReturnsAllExactNameMatches() {
        LanguageRecord language = languageService.getById("language:phb:druidic").orElseThrow();
        assertEquals("Druidic", language.name());
        assertTrue(languageService.getById("unknown").isEmpty());
        assertEquals(7, languageService.getByName("Common").size());
        assertTrue(languageService.getByName("unknown").isEmpty());
    }

    @Test
    void mapsSpeakerUnionsAndConcealedCommunication() {
        assertInstanceOf(ReferencedCreatureSpeaker.class,
                languageService.getById("language:ggr:kraul").orElseThrow().typicalSpeakers().getFirst());
        assertInstanceOf(FilteredCreatureSpeaker.class,
                languageService.getById("language:erlw:draconic").orElseThrow().typicalSpeakers().getFirst());

        LanguageRecord druidic = languageService.getById("language:phb:druidic").orElseThrow();
        LanguageConcealedMessage message = druidic.communicationCapabilities().getFirst();
        assertEquals("CHECK_FOR_PRESENCE", message.recognition().otherwise().kind().name());
        assertEquals("skill:phb:perception", message.recognition().otherwise().skill().id());
    }

    @Test
    void exposesThreeReadEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(201));

        mockMvc.perform(get("/api/languages/{id}", "language:phb:druidic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("language:phb:druidic"));

        mockMvc.perform(get("/api/languages/name/{name}", "Common"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7));

        mockMvc.perform(get("/api/languages/{id}", "unknown"))
                .andExpect(status().isNotFound());
    }
}
