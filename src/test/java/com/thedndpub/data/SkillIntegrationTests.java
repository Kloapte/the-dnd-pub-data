package com.thedndpub.data;

import com.thedndpub.data.dto.skill.SkillRecord;
import com.thedndpub.data.dto.skill.enums.SkillAbility;
import com.thedndpub.data.repositories.skill.SkillRepository;
import com.thedndpub.data.services.skill.SkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SkillIntegrationTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private SkillService skillService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("classpath:normalized/core-rules/skills.json")
    private Resource skillsDataset;

    @Test
    void repositoryLoadsEverySkillWithUniqueIds() {
        assertEquals(36, skillRepository.findAll().size());
        assertEquals(36, skillRepository.findAll().stream().map(SkillRecord::id).distinct().count());
    }

    @Test
    void deserializesAndSerializesEverySkillWithoutChangingJsonMeaning() throws Exception {
        byte[] sourceBytes;
        try (InputStream input = skillsDataset.getInputStream()) {
            sourceBytes = input.readAllBytes();
        }
        JsonNode source = objectMapper.readTree(sourceBytes);
        SkillRecord[] skills = objectMapper.readValue(sourceBytes, SkillRecord[].class);

        assertEquals(36, skills.length);
        assertEquals(source, objectMapper.readTree(objectMapper.writeValueAsString(skills)));
        assertInstanceOf(SkillAbility.class, skills[0].ability());
    }

    @Test
    void mapsOptionalCheckReferencesAndRejectsInvalidSkillValues() throws Exception {
        String json = "{\"id\":\"skill:test:fixture\",\"name\":\"Fixture\","
                + "\"sourceMetadata\":{\"source\":\"TEST\"},\"ability\":\"wisdom\","
                + "\"checkReferences\":[{\"targetType\":\"rule\",\"id\":\"rule:phb:ability-check\"}]}";
        SkillRecord skill = objectMapper.readValue(json, SkillRecord.class);
        assertEquals("rule:phb:ability-check", skill.checkReferences().getFirst().id());
        assertEquals(objectMapper.readTree(json), objectMapper.readTree(objectMapper.writeValueAsString(skill)));

        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"id\":\"skill:test:missing-ability\",\"name\":\"Missing\","
                        + "\"sourceMetadata\":{\"source\":\"TEST\"}}", SkillRecord.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"id\":\"skill:test:bad-ability\",\"name\":\"Bad\","
                        + "\"sourceMetadata\":{\"source\":\"TEST\"},\"ability\":\"luck\"}",
                SkillRecord.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"id\":\"skill:test:empty-checks\",\"name\":\"Empty checks\","
                        + "\"sourceMetadata\":{\"source\":\"TEST\"},\"ability\":\"wisdom\","
                        + "\"checkReferences\":[]}", SkillRecord.class));
    }

    @Test
    void findsSkillsByIdAndExactName() {
        SkillRecord acrobatics = skillService.getById("skill:phb:acrobatics").orElseThrow();
        assertEquals("Acrobatics", acrobatics.name());
        assertTrue(skillService.getById("skill:missing:missing").isEmpty());
        assertEquals(2, skillService.getByName("Acrobatics").size());
        assertTrue(skillService.getByName("Unknown Skill").isEmpty());
    }

    @Test
    void exposesAllThreeReadEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(36));

        mockMvc.perform(get("/api/skills/{id}", "skill:phb:acrobatics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("skill:phb:acrobatics"));

        mockMvc.perform(get("/api/skills/name/{name}", "Acrobatics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/skills/{id}", "skill:missing:missing"))
                .andExpect(status().isNotFound());
    }
}
