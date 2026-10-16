package com.thedndpub.data.action;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.ActionsController;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.dto.action.ActionMapper;
import com.thedndpub.data.dto.action.ActionSummaryDto;
import com.thedndpub.data.dte.action.ActionDte;
import com.thedndpub.data.dte.action.ActionsFileDte;
import com.thedndpub.data.dte.action.ActionTimeSpecialDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.services.ActionCatalog;
import com.thedndpub.data.services.ActionNotFoundException;
import com.thedndpub.data.services.ActionsService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ActionPipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActionCatalog catalog;

    @Autowired
    private ActionMapper mapper;

    @Autowired
    private ActionsService service;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Test
    void completeCorpusDeserializesAndNormalizesWithRegisteredSources() throws Exception {
        ActionsFileDte source;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/actions.json")) {
            assertNotNull(input);
            source = objectMapper.readerFor(ActionsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }

        assertEquals(48, source.action().size());
        assertEquals(48, catalog.size());
        assertEquals("action:xphb:dash", mapper.mapRecord(source.action().stream()
                .filter(action -> action.name().equals("Dash") && action.source().equals("XPHB"))
                .findFirst().orElseThrow()).definition().id().value());
        assertTrue(List.of("DMG", "PHB", "XPHB", "XGE").stream()
                .allMatch(code -> sourceRegistry.find(code) != null));
    }

    @Test
    void structuredTimingAlternativesAndSourceSpecificReprintsArePreserved() {
        var dash = catalog.findById("action:xphb:dash");
        assertEquals(1, dash.time().getFirst().number());
        assertEquals("action", dash.time().getFirst().unit());

        var castSpell = catalog.findById("action:phb:cast-a-spell");
        assertEquals("Varies", castSpell.time().getFirst().special());
        assertTrue(catalog.findById("action:dmg:mark").time().isEmpty());
        assertEquals(2, catalog.findById("action:xge:identify-a-spell").time().size());
        assertEquals("PHB", catalog.findById("action:phb:dash").source().identity().code());
        assertEquals("XPHB", catalog.findById("action:xphb:dash").source().identity().code());

        var grappleReprint = catalog.findById("action:phb:grapple").reprintedAs().getFirst();
        assertEquals("Unarmed Strike", grappleReprint.name());
        assertEquals("XPHB", grappleReprint.source().code());
        assertEquals("variantrule", grappleReprint.tag());
        assertEquals("XPHB", catalog.findById("action:phb:dash").reprintedAs().getFirst().source().code());
    }

    @Test
    void provenanceRetainsEntriesRelatedActionLabelsAndAvailabilityFlags() {
        var attack = catalog.importById("action:phb:attack").provenance();
        assertFalse(attack.entries().isEmpty());
        assertFalse(attack.seeAlsoAction().isEmpty());
        assertEquals(Boolean.TRUE, attack.srd());
        assertEquals("Action Options", catalog.importById("action:dmg:overrun").provenance().fromVariant());
    }

    @Test
    void rejectsUnknownSourcesAndDuplicateNameSourceIdentities() {
        ActionDte unknownSource = action("Test Action", "NOT_REGISTERED");
        assertThrows(IllegalStateException.class,
                () -> new ActionCatalog(new ActionsFileDte(List.of(unknownSource)), mapper, sourceRegistry));

        ActionDte first = action("Duplicate", "PHB");
        ActionDte duplicate = action("duplicate", "phb");
        assertThrows(IllegalStateException.class,
                () -> new ActionCatalog(new ActionsFileDte(List.of(first, duplicate)), mapper, sourceRegistry));
    }

    @Test
    void filtersSortsAndSerializesWithoutProseOrImportMetadata() throws Exception {
        assertEquals(2, service.findByName("dAsH", null).size());
        assertEquals(1, service.findByName("Dash", "XPHB").size());
        assertEquals(1, service.findActions("dash", "phb").size());
        assertThrows(UnsupportedOperationException.class, () -> catalog.summaries().clear());
        List<ActionSummaryDto> sortedSummaries = new ArrayList<>(catalog.summaries());
        sortedSummaries.sort(Comparator
                .comparing(ActionSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));
        assertEquals(sortedSummaries, catalog.summaries());
        assertThrows(ActionNotFoundException.class, () -> service.findById("missing"));

        String detail = objectMapper.writeValueAsString(service.findById("action:phb:dash"));
        assertTrue(detail.contains("\"page\":192"));
        assertTrue(detail.contains("\"unit\":\"action\""));
        assertTrue(detail.contains("reprintedAs"));
        assertFalse(detail.contains("entries"));
        assertFalse(detail.contains("basicRules"));
        assertFalse(detail.contains("{@"));

        String specialTiming = objectMapper.writeValueAsString(service.findById("action:phb:cast-a-spell"));
        assertTrue(specialTiming.contains("\"special\":\"Varies\""));
        assertFalse(objectMapper.writeValueAsString(service.findById("action:dmg:mark")).contains("time"));
    }

    @Test
    void endpointsServeSummariesDetailsAmbiguousNamesAndNotFoundResponses() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ActionsController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
        mvc.perform(get("/api/actions").param("name", "dash").param("source", "phb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("action:phb:dash"));
        mvc.perform(get("/api/actions/action:xphb:dash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source.page").value(365));
        mvc.perform(get("/api/actions/by-name/Dash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
        mvc.perform(get("/api/actions/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actionResourceLoadsFromPackagedJar() throws Exception {
        Path jarPath = temporaryDirectory.resolve("actions-resource.jar");
        try (InputStream input = new ClassPathResource("data/actions.json").getInputStream();
             JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jar.putNextEntry(new JarEntry("data/actions.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            Resource resource = new ClassPathResource("data/actions.json", classLoader);
            try (InputStream input = resource.getInputStream()) {
                ActionsFileDte source = objectMapper.readerFor(ActionsFileDte.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(input);
                assertEquals(48, source.action().size());
            }
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
            assertEquals(1, resolver.getResources("classpath*:data/actions.json").length);
        }
    }

    private static ActionDte action(String name, String source) {
        return new ActionDte(
                name,
                source,
                1,
                null,
                null,
                null,
                null,
                List.of(new ActionTimeSpecialDte("Free")),
                List.of(new TextEntryDte("test")),
                null,
                null,
                null);
    }
}
