package com.thedndpub.data;

import com.thedndpub.data.dto.source.PublicationGroup;
import com.thedndpub.data.dto.source.PublicationKind;
import com.thedndpub.data.dto.source.Source;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SourceRegistryIntegrationTests {
    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void loadsAllSourcesAndPublicationReferences() {
        assertEquals(181, sourceRegistry.findAll().size());
        assertEquals(167, sourceRegistry.findAll().stream()
                .mapToInt(source -> source.publications().size())
                .sum());
    }

    @Test
    void deserializesRepresentativeOptionalFieldsAndEnums() {
        Source phb = sourceRegistry.findByCode("PHB").orElseThrow();
        assertEquals(LocalDate.of(2014, 8, 19), phb.publications().getFirst().published());
        assertEquals(PublicationGroup.CORE, phb.publications().getFirst().group());

        Source codeOnly = sourceRegistry.findByCode("EEPC").orElseThrow();
        assertTrue(codeOnly.publications().isEmpty());
        assertTrue(codeOnly.codeAliases().isEmpty());

        Source mot = sourceRegistry.findByCode("MOT").orElseThrow();
        assertEquals(2, mot.publications().size());
        assertEquals(PublicationKind.ADVENTURE, mot.publications().get(1).kind());
        assertEquals("MOT-NSS", mot.publications().get(1).publicationId());

        Source psz = sourceRegistry.findByCode("PSZ").orElseThrow();
        assertEquals("PS-Z", psz.codeAliases().getFirst());
        assertEquals("PS-Z", psz.publications().getFirst().publicationId());

        Source aag = sourceRegistry.findByCode("AAG").orElseThrow();
        assertEquals("SAiS", aag.publications().getFirst().parentSource());
        assertEquals("Spelljammer: Astral Adventurer's Guide",
                aag.publications().getFirst().titleAliases().getFirst());
    }

    @Test
    void findsCanonicalCodesAndExplicitAliases() {
        assertEquals("MOT", sourceRegistry.findByCode("MOT").orElseThrow().code());
        assertEquals("PSZ", sourceRegistry.findByCodeAlias("PS-Z").orElseThrow().code());
        assertEquals("PSZ", sourceRegistry.findByCodeOrAlias("PS-Z").orElseThrow().code());
        assertFalse(sourceRegistry.findByCodeAlias("not-an-alias").isPresent());
    }

    @Test
    void rejectsDuplicateCodesAndAliasCollisions() {
        IllegalStateException duplicateCodeError = assertThrows(
                IllegalStateException.class,
                () -> registryFrom("[{\"code\":\"ONE\"},{\"code\":\"ONE\"}]"));
        assertTrue(duplicateCodeError.getMessage().contains("Duplicate"));

        IllegalStateException aliasCodeError = assertThrows(
                IllegalStateException.class,
                () -> registryFrom(
                        "[{\"code\":\"ONE\",\"codeAliases\":[\"TWO\"]},{\"code\":\"TWO\"}]"));
        assertTrue(aliasCodeError.getMessage().contains("collides"));

        IllegalStateException aliasError = assertThrows(
                IllegalStateException.class,
                () -> registryFrom(
                        "[{\"code\":\"ONE\",\"codeAliases\":[\"ALT\"]},"
                                + "{\"code\":\"TWO\",\"codeAliases\":[\"alt\"]}]"));
        assertTrue(aliasError.getMessage().contains("alias"));
    }

    @Test
    void failsClearlyForMissingOrMalformedDataset() {
        IllegalStateException missing = assertThrows(
                IllegalStateException.class,
                () -> new SourceRegistry(objectMapper, new ClassPathResource("normalized/sources/missing.json")));
        assertTrue(missing.getMessage().contains("Unable to load Source dataset"));

        ByteArrayResource malformed = resource("[{\"code\":");
        IllegalStateException invalidJson = assertThrows(
                IllegalStateException.class,
                () -> new SourceRegistry(objectMapper, malformed));
        assertTrue(invalidJson.getMessage().contains("Unable to load Source dataset"));

        assertThrows(IllegalStateException.class,
                () -> registryFrom("[{\"code\":\"ONE\",\"unknownField\":true}]"));
        assertThrows(IllegalStateException.class,
                () -> registryFrom("[{\"code\":\"ONE\",\"publications\":["
                        + "{\"kind\":\"book\",\"group\":\"new-group\"}]}]"));
        assertThrows(IllegalStateException.class,
                () -> registryFrom("[{\"code\":\"ONE\",\"codeAliases\":[]}]"));
    }

    @Test
    void exposesCanonicalListAndAliasLookupOverHttp() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/sources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("AAG"))
                .andExpect(jsonPath("$.length()").value(181));

        mockMvc.perform(get("/api/sources/{code}", "MOT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("MOT"))
                .andExpect(jsonPath("$.publications.length()").value(2))
                .andExpect(jsonPath("$.publications[0].group").value("setting"))
                .andExpect(jsonPath("$.publications[1].kind").value("adventure"))
                .andExpect(jsonPath("$.publications[1].parentSource").value("MOT"));

        mockMvc.perform(get("/api/sources/{code}", "EEPC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("EEPC"))
                .andExpect(jsonPath("$.publications").doesNotExist())
                .andExpect(jsonPath("$.codeAliases").doesNotExist());

        mockMvc.perform(get("/api/sources/{code}", "PS-Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PSZ"))
                .andExpect(jsonPath("$.codeAliases[0]").value("PS-Z"));

        mockMvc.perform(get("/api/sources/{code}", "unknown-code"))
                .andExpect(status().isNotFound());
    }

    private SourceRegistry registryFrom(String json) {
        return new SourceRegistry(objectMapper, resource(json));
    }

    private ByteArrayResource resource(String json) {
        return new ByteArrayResource(json.getBytes(StandardCharsets.UTF_8), "test Source registry");
    }
}
