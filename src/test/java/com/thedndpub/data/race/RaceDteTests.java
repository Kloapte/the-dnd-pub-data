package com.thedndpub.data.race;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.RaceModificationDte;
import com.thedndpub.data.dte.race.RaceSpeedDte;
import com.thedndpub.data.dte.race.RacesFileDte;
import com.thedndpub.data.dto.race.RaceDefinitionKind;
import com.thedndpub.data.dto.race.RaceDefinitionNormalizer;
import com.thedndpub.data.dto.race.RaceIdentityFactory;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RaceDteTests {
    @Autowired
    private SourceRegistry sourceRegistry;

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Test
    void deserializesFullRaceCorpusWithUnknownFieldsRejected() throws Exception {
        RacesFileDte races = load();
        assertEquals(160, races.race().size());
        assertEquals(98, races.subrace().size());
        assertEquals(5L, races.subrace().stream().filter(race -> race.name() == null).count());
        assertEquals(16, races.race().stream().filter(race -> race.copy() != null).count());
        assertEquals(1, races.subrace().stream().filter(race -> race.copy() != null).count());
        assertEquals(41, races.race().stream().mapToInt(race -> size(race.versions())).sum());
        assertEquals(14, races.subrace().stream().mapToInt(race -> size(race.versions())).sum());
        assertEquals(25, raceImplementations(races.race()));
        assertEquals(30, subraceImplementations(races.subrace()));
        assertTrue(races.race().stream().anyMatch(race -> race.ability() != null
                && race.ability().stream().anyMatch(ability -> ability.choice() != null)));
        assertTrue(races.race().stream().anyMatch(race -> race.additionalSpells() != null));
        assertTrue(races.subrace().stream().anyMatch(race -> race.overwrite() != null));
        assertTrue(races.race().stream().anyMatch(race -> race.lineage() != null
                && race.lineage().flag() != null));
        var aarakocra = races.race().stream()
                .filter(race -> race.name().equals("Aarakocra") && race.source().equals("MPMM"))
                .findFirst()
                .orElseThrow();
        assertEquals(Boolean.TRUE, aarakocra.speed().fly().sourceFlag());
        assertEquals(Integer.valueOf(30), aarakocra.speed().walk().feet());
        assertEquals(java.util.Set.of("appendArr", "replaceArr", "removeArr", "prependArr", "replaceTxt"),
                observedModificationModes(races));
    }

    @Test
    void normalizesRootsAndExpandedVersionsWithUniqueParentAwareIds() throws Exception {
        var definitions = new RaceDefinitionNormalizer().normalize(load(), sourceRegistry);
        assertEquals(356, definitions.size());
        assertEquals(103L, definitions.stream()
                .filter(definition -> definition.provenance().expandedVersion())
                .count());
        assertEquals(253L, definitions.stream()
                .filter(definition -> !definition.provenance().expandedVersion())
                .count());
        assertEquals(356L, definitions.stream()
                .map(definition -> definition.id().value())
                .distinct()
                .count());
        assertTrue(definitions.stream().filter(definition -> definition.provenance().expandedVersion())
                .allMatch(definition -> !definition.effectiveEntries().toString().contains("{{")),
                () -> definitions.stream()
                        .filter(definition -> definition.provenance().expandedVersion())
                        .filter(definition -> definition.effectiveEntries().toString().contains("{{"))
                        .map(definition -> definition.name() + "|" + definition.source().identity().code())
                        .toList().toString());
        assertNotEquals(RaceIdentityFactory.race("Dwarf", "PHB"), RaceIdentityFactory.race("Dwarf", "XPHB"));
        assertTrue(definitions.stream().anyMatch(definition -> definition.id().value().equals(
                "race:phb:dwarf")));
        assertTrue(definitions.stream().anyMatch(definition -> definition.id().value().equals(
                "race:xphb:dwarf")));

        var shifter = definitions.stream()
                .filter(definition -> definition.name().equals("Shifter")
                        && definition.source().identity().code().equals("MPMM"))
                .findFirst()
                .orElseThrow();
        assertEquals(RaceDefinitionKind.RACE, shifter.kind());
        assertEquals(1, shifter.mechanics().skillProficiencies().size());
        assertThrows(UnsupportedOperationException.class,
                () -> shifter.effectiveEntries().clear());

        var dragonbornLineage = definitions.stream()
                .filter(definition -> definition.name().equals("Dragonborn (Black)"))
                .filter(definition -> definition.kind() == RaceDefinitionKind.SUBRACE)
                .filter(definition -> definition.source().identity().code().equals("PHB"))
                .findFirst()
                .orElseThrow();
        assertEquals("race:phb:dragonborn", dragonbornLineage.parentId().value());
        assertTrue(dragonbornLineage.mechanics().resistances().stream()
                .anyMatch(resistance -> resistance.toString().contains("acid")));

        var dankwoodGoblin = definitions.stream()
                .filter(definition -> definition.name().equals("Goblin (Dankwood)"))
                .filter(definition -> definition.source().identity().code().equals("AWM"))
                .findFirst()
                .orElseThrow();
        assertTrue(dankwoodGoblin.effectiveEntries().toString().contains("Dankwood goblins"));
    }

    @Test
    void rejectsUnknownModificationOperationsAndProperties() throws Exception {
        assertThrows(Exception.class, () -> mapper.readValue(
                "{\"entries\":{\"mode\":\"setUnknown\",\"replace\":\"x\",\"items\":{\"type\":\"entries\",\"name\":\"x\"}}}",
                RaceModificationDte.class));
        assertThrows(Exception.class, () -> mapper.readValue(
                "{\"entries\":{\"mode\":\"replaceTxt\",\"replace\":\"x\",\"with\":\"y\",\"mystery\":true}}",
                RaceModificationDte.class));
        assertThrows(JsonMappingException.class, () -> mapper.readValue(
                "{\"race\":[{\"name\":\"Test\",\"source\":\"PHB\",\"unknown\":true}]}",
                RacesFileDte.class));
        assertThrows(Exception.class, () -> mapper.readValue(
                "{\"walk\":30,\"hover\":true}", RaceSpeedDte.class));
        RaceSpeedDte explicitValues = mapper.readValue(
                "{\"walk\":0,\"fly\":false}", RaceSpeedDte.class);
        assertEquals(Integer.valueOf(0), explicitValues.walk().feet());
        assertEquals(Boolean.FALSE, explicitValues.fly().sourceFlag());
    }

    @Test
    void preservesSourceSpecificSubraceIdentityForRepeatedNames() throws Exception {
        var definitions = new RaceDefinitionNormalizer().normalize(load(), sourceRegistry);
        var ixalan = definitions.stream()
                .filter(definition -> definition.name().equals("Ixalan")
                        && definition.source().identity().code().equals("PSX"))
                .toList();
        assertEquals(2, ixalan.size());
        assertNotEquals(ixalan.get(0).id(), ixalan.get(1).id());
        assertNotEquals(ixalan.get(0).parentId(), ixalan.get(1).parentId());
    }

    private static int size(java.util.List<?> values) {
        return values == null ? 0 : values.size();
    }

    private static int raceImplementations(java.util.List<com.thedndpub.data.dte.race.RaceDte> races) {
        return races.stream()
                .flatMap(race -> race.versions() == null ? java.util.stream.Stream.empty() : race.versions().stream())
                .mapToInt(version -> version.implementations() == null ? 0 : version.implementations().size())
                .sum();
    }

    private static int subraceImplementations(java.util.List<com.thedndpub.data.dte.race.SubraceDte> races) {
        return races.stream()
                .flatMap(race -> race.versions() == null ? java.util.stream.Stream.empty() : race.versions().stream())
                .mapToInt(version -> version.implementations() == null ? 0 : version.implementations().size())
                .sum();
    }

    private static java.util.Set<String> observedModificationModes(RacesFileDte races) {
        java.util.Set<String> modes = new java.util.TreeSet<>();
        races.race().forEach(race -> {
            if (race.copy() != null && race.copy().modification() != null) {
                race.copy().modification().entries().forEach(operation -> modes.add(operation.mode()));
            }
            if (race.versions() != null) {
                race.versions().forEach(version -> collectModes(version, modes));
            }
        });
        races.subrace().forEach(subrace -> {
            if (subrace.copy() != null && subrace.copy().modification() != null) {
                subrace.copy().modification().entries().forEach(operation -> modes.add(operation.mode()));
            }
            if (subrace.versions() != null) {
                subrace.versions().forEach(version -> collectModes(version, modes));
            }
        });
        return java.util.Set.copyOf(modes);
    }

    private static void collectModes(
            com.thedndpub.data.dte.race.RaceVersionDte version,
            java.util.Set<String> modes) {
        if (version.modification() != null) {
            version.modification().entries().forEach(operation -> modes.add(operation.mode()));
        }
        if (version.abstractTemplate() != null && version.abstractTemplate().modification() != null) {
            version.abstractTemplate().modification().entries().forEach(operation -> modes.add(operation.mode()));
        }
    }

    private RacesFileDte load() throws Exception {
        try (var input = new ClassPathResource("data/races.json").getInputStream()) {
            return mapper.readValue(input, RacesFileDte.class);
        }
    }
}
