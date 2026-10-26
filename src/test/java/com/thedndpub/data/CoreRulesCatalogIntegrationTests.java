package com.thedndpub.data;

import com.thedndpub.data.dto.corerules.CanonicalReference;
import com.thedndpub.data.dto.corerules.ReferenceResolution;
import com.thedndpub.data.services.CoreRulesCatalog;
import com.thedndpub.data.services.corerules.SkillService;
import com.thedndpub.data.services.corerules.SenseService;
import com.thedndpub.data.services.corerules.StatusService;
import com.thedndpub.data.services.corerules.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CoreRulesCatalogIntegrationTests {
    @Autowired
    private CoreRulesCatalog catalog;

    @Autowired
    private SkillService skillService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private SenseService senseService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void loadsEveryPackagedCanonicalRecordAndIndexesByDomainAndIdentity() {
        assertEquals(585, catalog.findAll().size());
        assertEquals(Map.of(
                "action", 46,
                "condition", 30,
                "disease", 28,
                "language", 201,
                "rule", 231,
                "sense", 8,
                "skill", 36,
                "status", 5),
                Map.of(
                        "action", catalog.findAllByDomain("action").size(),
                        "condition", catalog.findAllByDomain("condition").size(),
                        "disease", catalog.findAllByDomain("disease").size(),
                        "language", catalog.findAllByDomain("language").size(),
                        "rule", catalog.findAllByDomain("rule").size(),
                        "sense", catalog.findAllByDomain("sense").size(),
                        "skill", catalog.findAllByDomain("skill").size(),
                        "status", catalog.findAllByDomain("status").size()));
        assertEquals((long) catalog.findAll().size(), catalog.findAll().stream()
                .map(record -> record.identity().id())
                .distinct()
                .count());
        assertEquals("rule:dmg:planar-effects",
                catalog.findById("rule:dmg:planar-effects").orElseThrow().identity().id());
        assertTrue(catalog.findById("action:dmg:breath-weapon").isEmpty());
        assertEquals(8, applicationContext.getBeansOfType(
                com.thedndpub.data.repositories.corerules.CoreRulesDomainRepository.class).size());
        assertEquals(8, applicationContext.getBeansOfType(
                com.thedndpub.data.services.corerules.CoreRulesDomainService.class).size());
    }

    @Test
    void resolvesCanonicalSourceKnownAndUnknownReferencesSeparately() {
        CanonicalReference canonical = new CanonicalReference("condition", "condition:phb:prone", null, null);
        ReferenceResolution canonicalResult = catalog.resolve(canonical);
        assertEquals(ReferenceResolution.State.CANONICAL_TARGET_AVAILABLE, canonicalResult.state());
        assertTrue(canonicalResult.canonicalTarget() != null);

        CanonicalReference sourceOnly = new CanonicalReference("spell", "spell:phb:fireball", null, null);
        ReferenceResolution sourceOnlyResult = catalog.resolve(sourceOnly);
        assertEquals(ReferenceResolution.State.SOURCE_KNOWN_WITHOUT_CANONICAL_OUTPUT, sourceOnlyResult.state());
        assertTrue(sourceOnlyResult.canonicalTarget() == null);

        CanonicalReference unknown = new CanonicalReference("rule", "rule:phb:not-in-source", null, null);
        assertEquals(ReferenceResolution.State.SOURCE_IDENTITY_UNKNOWN, catalog.resolve(unknown).state());
    }

    @Test
    void preservesTypedReferencesAndCompleteCanonicalJson() throws Exception {
        var action = catalog.findById("action:phb:grapple").orElseThrow();
        assertFalse(action.references().isEmpty());
        assertTrue(action.references().stream().anyMatch(reference ->
                reference.targetType().equals("condition") && reference.id().equals("condition:phb:grappled")));

        var completeRecord = objectMapper.readTree(catalog.findCanonicalJson(action.identity().id()).orElseThrow());
        assertEquals(action.identity().id(), completeRecord.get("id").asText());
        assertTrue(completeRecord.has("operations"));
    }

    @Test
    void listsAndFindsCanonicalRecordsThroughRest() throws Exception {
        mockMvc.perform(get("/api/core-rules/skill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("skill:phb:acrobatics"))
                .andExpect(jsonPath("$[0].ability").value("dexterity"));

        mockMvc.perform(get("/api/core-rules/skill/phb/acrobatics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("skill:phb:acrobatics"))
                .andExpect(jsonPath("$.sourceMetadata.source").value("PHB"));

        mockMvc.perform(get("/api/core-rules/rule/dmg/diagonals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("rule:dmg:diagonals"))
                .andExpect(jsonPath("$.mechanics.length()").value(1));
    }

    @Test
    void everyDomainControllerListsAndFindsItsFirstCanonicalRecord() throws Exception {
        for (String domain : java.util.List.of(
                "action", "condition", "disease", "language", "rule", "sense", "skill", "status")) {
            var record = catalog.findAllByDomain(domain).getFirst();
            String[] identity = record.identity().id().split(":", 3);

            mockMvc.perform(get("/api/core-rules/" + domain))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(record.identity().id()));
            mockMvc.perform(get("/api/core-rules/" + domain + "/" + identity[1] + "/" + identity[2]))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(record.identity().id()));
        }
    }

    @Test
    void jacksonRoundTripsSharedIdentityAndProvenanceForEveryCanonicalRecord() throws Exception {
        for (var record : catalog.findAll()) {
            var identity = objectMapper.readValue(
                    objectMapper.writeValueAsBytes(record.identity()),
                    record.identity().getClass());
            var provenance = objectMapper.readValue(
                    objectMapper.writeValueAsBytes(record.sourceMetadata()),
                    record.sourceMetadata().getClass());
            assertEquals(record.identity(), identity, record.identity().id());
            assertEquals(record.sourceMetadata(), provenance, record.identity().id());
        }
    }

    @Test
    void skillModelsRoundTripEveryCanonicalSkillWithoutChangingItsContract() throws Exception {
        var skills = skillService.findAllSkills();
        assertEquals(36, skills.size());
        for (var skill : skills) {
            byte[] original = catalog.findCanonicalJson(skill.id()).orElseThrow();
            var expected = objectMapper.readTree(original);
            var actual = objectMapper.readTree(objectMapper.writeValueAsBytes(skill));
            assertEquals(expected, actual, skill.id());
        }
    }

    @Test
    void statusMechanicVariantsRoundTripEveryCanonicalStatusWithoutChangingItsContract() throws Exception {
        var statuses = statusService.findAllStatuses();
        assertEquals(5, statuses.size());
        for (var status : statuses) {
            byte[] original = catalog.findCanonicalJson(status.id()).orElseThrow();
            var expected = objectMapper.readTree(original);
            var actual = objectMapper.readTree(objectMapper.writeValueAsBytes(status));
            assertEquals(expected, actual, status.id());
        }
    }

    @Test
    void senseCapabilityVariantsRoundTripEveryCanonicalSenseWithoutChangingItsContract() throws Exception {
        var senses = senseService.findAllSenses();
        assertEquals(8, senses.size());
        for (var sense : senses) {
            byte[] original = catalog.findCanonicalJson(sense.id()).orElseThrow();
            var expected = objectMapper.readTree(original);
            var actual = objectMapper.readTree(objectMapper.writeValueAsBytes(sense));
            assertEquals(expected, actual, sense.id());
        }
    }

    @Test
    void languageVariantsRoundTripEveryCanonicalLanguageWithoutChangingItsContract() throws Exception {
        var languages = languageService.findAllLanguages();
        assertEquals(201, languages.size());
        for (var language : languages) {
            byte[] original = catalog.findCanonicalJson(language.id()).orElseThrow();
            var expected = objectMapper.readTree(original);
            var actual = objectMapper.readTree(objectMapper.writeValueAsBytes(language));
            assertEquals(expected, actual, language.id());
        }
    }

    @Test
    void reportsReferenceResolutionStatesThroughRest() throws Exception {
        mockMvc.perform(get("/api/core-rules/references/resolve")
                        .param("targetType", "condition")
                        .param("id", "condition:phb:prone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("CANONICAL_TARGET_AVAILABLE"))
                .andExpect(jsonPath("$.canonicalTarget.id").value("condition:phb:prone"))
                .andExpect(jsonPath("$.canonicalTarget.resourcePath").doesNotExist());

        mockMvc.perform(get("/api/core-rules/references/resolve")
                        .param("targetType", "spell")
                        .param("id", "spell:phb:fireball"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("SOURCE_KNOWN_WITHOUT_CANONICAL_OUTPUT"));

        mockMvc.perform(get("/api/core-rules/references/resolve")
                        .param("targetType", "rule")
                        .param("id", "rule:phb:not-in-source"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("SOURCE_IDENTITY_UNKNOWN"));
    }

    @Test
    void returnsNotFoundAndBadRequestForInvalidRestQueries() throws Exception {
        mockMvc.perform(get("/api/core-rules/not-a-domain"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/core-rules/rule/phb/not-in-catalog"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/core-rules/references/resolve")
                        .param("targetType", "rule")
                        .param("id", "spell:phb:fireball"))
                .andExpect(status().isBadRequest());
    }
}
