package com.thedndpub.data.dto.monster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.monster.content.ContentBoolean;
import com.thedndpub.data.dto.monster.content.ContentNumber;
import com.thedndpub.data.dto.monster.content.MonsterContentDto;
import com.thedndpub.data.dto.monster.content.PlainText;
import com.thedndpub.data.dto.monster.content.RollCell;
import com.thedndpub.data.dto.monster.content.Section;
import com.thedndpub.data.dto.monster.content.TableContent;
import com.thedndpub.data.dto.monster.identity.Identity;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dto.monster.identity.Source;
import com.thedndpub.data.dto.monster.internal.MonsterClassification;
import com.thedndpub.data.dto.monster.statistics.NullableList;
import com.thedndpub.data.dto.monster.statistics.Statistics;
import com.thedndpub.data.dte.monster.MonsterIdentity;
import com.thedndpub.data.dte.monster.MonsterSourceLocation;
import com.thedndpub.data.dte.monster.ResolutionProvenance;
import com.thedndpub.data.dte.monster.ResolvedMonster;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonsterDtoSerializationTests {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void omitsNullAndEmptyComponentsButPreservesExplicitNullStateInternally() {
        var id = new MonsterId("5etools:test");
        var identity = new Identity(id, "Goblin", List.of(),
                new Source("MM", null, List.of(), List.of(), List.of(), null),
                List.of(), List.of(), null, List.of(), null, null, null, null, null, null, null, null);
        var statistics = new Statistics(null, null, List.of(), null, null, null, null,
                Map.of(), Map.of(), null, List.of(), new NullableList<>(true, List.of()), null,
                List.of(), List.of(), List.of(), null);
        var dto = new MonsterDto(identity, statistics, null, null, null, List.of(), null);

        JsonNode json = objectMapper.valueToTree(dto);

        assertTrue(json.has("identity"));
        assertFalse(json.has("defenses"));
        assertEquals("Goblin", json.path("identity").path("name").asText());
        assertFalse(json.path("identity").has("alignmentPrefix"));
        assertFalse(json.path("identity").path("source").has("page"));
        assertFalse(json.path("identity").has("aliases"));
        assertFalse(json.path("identity").path("source").has("availability"));
        assertFalse(json.path("identity").path("source").has("otherSources"));
        assertTrue(json.path("statistics").path("languages").path("explicitNull").asBoolean());
        assertFalse(json.path("statistics").path("languages").has("values"));
        assertFalse(json.path("statistics").has("senses"));
        assertFalse(json.path("statistics").has("savingThrows"));
        assertFalse(json.path("statistics").has("skills"));
        assertFalse(json.has("spellcasting"));
    }

    @Test
    void nestedAndRecursiveContentRecordsOmitNullsWithoutChangingShape() {
        List<MonsterContentDto> entries = List.of(
                new Section(null, "Rules", null, List.of(new PlainText("{@damage 1d6} damage")),
                        null, null, Map.of()),
                new TableContent("Results", List.of("Roll"), List.of(),
                        List.of(List.of(new RollCell(1, null, null, null))), null, null, Map.of()));

        JsonNode json = objectMapper.valueToTree(entries);

        assertEquals("Rules", json.get(0).path("name").asText());
        assertFalse(json.get(0).has("sourceType"));
        assertEquals("{@damage 1d6} damage", json.get(0).path("entries").get(0).path("text").asText());
        assertEquals("Results", json.get(1).path("caption").asText());
        assertFalse(json.get(1).has("columnStyles"));
        assertEquals(1, json.get(1).path("rows").get(0).get(0).path("exact").asInt());
        assertFalse(json.get(1).path("rows").get(0).get(0).has("minimum"));
        assertFalse(json.get(0).has("sourceProperties"));
        assertFalse(json.get(0).has("type"));
    }

    @Test
    void preservesZeroAndFalseAndExcludesPublicMediaAndClassificationMetadata() throws Exception {
        ObjectNode source = (ObjectNode) objectMapper.readTree("""
                {
                  "name": "Probe",
                  "source": "TST",
                  "str": 0,
                  "isNpc": false,
                  "familiar": false,
                  "actionTags": ["MW"],
                  "damageTags": ["F"],
                  "hasFluff": true,
                  "hasFluffImages": false,
                  "hasToken": true,
                  "tokenCustom": true,
                  "tokenCredit": "artist",
                  "foundryTokenScale": 1.0,
                  "altArt": [{"name": "Artwork", "source": "TST"}],
                  "soundClip": "monster.mp3",
                  "environment": [],
                  "gear": [],
                  "attachedItems": [],
                  "treasure": [],
                  "trait": [{"name": "Strike", "entries": ["{@damage 1d6}"]}]
                }
                """);
        NormalizedMonsterRecord record = new MonsterMapper(objectMapper).mapRecord(monsterMapperInput(source));
        JsonNode json = objectMapper.valueToTree(record.monster());
        MonsterClassification classification = record.classification();

        assertEquals(0, json.path("statistics").path("abilities").path("strength").asInt());
        assertFalse(json.path("identity").path("npc").asBoolean(true));
        assertFalse(json.path("catalog").path("familiar").asBoolean(true));
        assertFalse(json.path("catalog").has("hasFluff"));
        assertFalse(json.path("catalog").has("hasFluffImages"));
        assertFalse(json.path("catalog").has("hasToken"));
        assertFalse(json.path("catalog").has("customToken"));
        assertFalse(json.path("catalog").has("tokenCredit"));
        assertFalse(json.path("catalog").has("foundryTokenScale"));
        assertFalse(json.path("catalog").has("alternateArt"));
        assertFalse(json.path("catalog").has("sound"));
        assertFalse(json.path("catalog").has("environments"));
        assertFalse(json.path("catalog").has("gear"));
        assertFalse(json.path("catalog").has("attachedItems"));
        assertFalse(json.path("catalog").has("treasure"));
        assertFalse(json.has("classification"));
        assertEquals(Set.of("MW"), classification.actionTags());
        assertEquals(Set.of("F"), classification.damageTags());
        assertTrue(classification.traitTags().isEmpty());
        assertEquals("{@damage 1d6}", json.path("features").path("traits").get(0)
                .path("entries").get(0).path("text").asText());
        assertTrue(source.has("hasFluff"));
        assertTrue(source.has("soundClip"));
        assertFalse(json.toString().contains("\"traitTags\""));
        assertFalse(json.toString().contains("\"actionTags\""));
        JsonNode falseValue = objectMapper.valueToTree(new ContentBoolean(false));
        JsonNode zeroValue = objectMapper.valueToTree(new ContentNumber(BigDecimal.ZERO));
        assertTrue(falseValue.has("value"));
        assertFalse(falseValue.path("value").asBoolean());
        assertTrue(zeroValue.has("value"));
        assertEquals(0, zeroValue.path("value").asInt());
    }

    @Test
    void mapperDoesNotCreateEmptyOptionalNestedRecords() throws Exception {
        ObjectNode source = (ObjectNode) objectMapper.readTree("""
                {
                  "name": "Sparse",
                  "source": "TST",
                  "alignmentPrefix": "",
                  "type": {},
                  "initiative": {},
                  "hp": {},
                  "speed": {},
                  "cr": {},
                  "legendaryGroup": {},
                  "summonedBySpell": "",
                  "action": [],
                  "legendaryActions": null,
                  "legendaryActionsLair": null
                }
                """);
        JsonNode json = objectMapper.valueToTree(new MonsterMapper(objectMapper).map(monsterMapperInput(source)));

        assertFalse(json.path("identity").has("creatureType"));
        assertFalse(json.path("identity").has("alignmentPrefix"));
        assertFalse(json.path("identity").has("summoning"));
        assertFalse(json.path("identity").has("legendaryGroup"));
        assertFalse(json.path("statistics").has("abilities"));
        assertFalse(json.path("statistics").has("initiative"));
        assertFalse(json.path("statistics").has("hitPoints"));
        assertFalse(json.path("statistics").has("movement"));
        assertFalse(json.path("statistics").has("challengeRating"));
        assertFalse(json.has("defenses"));
        assertFalse(json.has("features"));
        assertFalse(json.has("combat"));
        assertFalse(json.has("catalog"));
        assertTrue(source.has("legendaryGroup"));
    }

    @Test
    void sourceNullAndAbsentValuesRemainDistinctInsideNullableLists() throws Exception {
        ObjectNode explicitNullSource = (ObjectNode) objectMapper.readTree(
                "{\"name\":\"Test Monster\",\"source\":\"TST\",\"languages\":null,\"vulnerable\":null}");
        ObjectNode absentSource = (ObjectNode) objectMapper.readTree("{\"name\":\"Test Monster\",\"source\":\"TST\"}");
        var explicit = monsterMapperInput(explicitNullSource);
        var absent = monsterMapperInput(absentSource);
        MonsterMapper mapper = new MonsterMapper(objectMapper);

        MonsterDto explicitDto = mapper.map(explicit);
        MonsterDto absentDto = mapper.map(absent);

        assertTrue(explicitDto.statistics().languages().explicitNull());
        assertTrue(explicitDto.defenses().vulnerabilities().explicitNull());
        assertNull(absentDto.statistics().languages());
        assertNull(absentDto.defenses());

        JsonNode serialized = objectMapper.valueToTree(explicitDto);
        assertTrue(serialized.path("statistics").path("languages").path("explicitNull").asBoolean());
        assertTrue(serialized.path("defenses").path("vulnerabilities").path("explicitNull").asBoolean());
        assertFalse(objectMapper.valueToTree(absentDto).path("statistics").has("languages"));
    }

    @Test
    void summaryKeepsExistingPropertyNamesAndEmptyCollections() {
        var summary = new MonsterSummaryDto(new MonsterId("5etools:test"), "Goblin", "MM",
                Set.of(), null, List.of(), null, List.of(), null, null, Set.of());

        JsonNode json = objectMapper.valueToTree(summary);

        assertEquals("5etools:test", json.path("id").path("value").asText());
        assertEquals("Goblin", json.path("name").asText());
        assertEquals("MM", json.path("source").asText());
        assertFalse(json.has("challengeRating"));
        assertFalse(json.has("editionIds"));
        assertFalse(json.has("sizes"));
        assertFalse(json.has("environments"));
    }

    @Test
    void pagedResponseOmitsEmptyContentAndKeepsMeaningfulZeroes() {
        JsonNode json = objectMapper.valueToTree(new PagedResponse<>(List.of(), 0, 25, 0, 0));

        assertFalse(json.has("content"));
        assertEquals(0, json.path("page").asInt(-1));
        assertEquals(25, json.path("size").asInt());
        assertEquals(0, json.path("totalElements").asLong(-1));
        assertEquals(0, json.path("totalPages").asInt(-1));
    }

    private ResolvedMonster monsterMapperInput(ObjectNode content) {
        return new ResolvedMonster(MonsterIdentity.from("Test Monster", "TST"),
                new MonsterSourceLocation("test.json", 0), null, List.of(), content,
                new ResolutionProvenance("test", "test", null, null, true, Set.of()));
    }
}
