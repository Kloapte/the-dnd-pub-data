package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpellFieldCoverageTests {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void sourceCorpusContainsEveryStageAFieldPath() throws Exception {
        Set<String> expectedPaths = expectedPaths();
        Set<String> actualPaths = new TreeSet<>();

        Path sourceDirectory = Path.of("src/main/resources/data/spells");
        try (var paths = Files.walk(sourceDirectory)) {
            for (Path sourceFile : paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .sorted()
                    .toList()) {
                JsonNode root = mapper.readTree(sourceFile.toFile());
                root.fields().forEachRemaining(field -> collectPaths(field.getValue(), field.getKey(), actualPaths));
            }
        }

        assertEquals(148, expectedPaths.size(), "Stage A field path baseline changed; review the inventory");
        assertEquals(expectedPaths, actualPaths,
                "The complete spell corpus no longer matches the Stage A field inventory");
    }

    private Set<String> expectedPaths() throws Exception {
        Path inventory = Path.of("analysis/spells/normalized-field-inventory.json");
        JsonNode root = mapper.readTree(inventory.toFile());
        Set<String> paths = new TreeSet<>();
        for (JsonNode field : root.path("fields")) {
            paths.add(field.path("path").asText());
        }
        return paths;
    }

    private void collectPaths(JsonNode value, String currentPath, Set<String> paths) {
        paths.add(currentPath);
        if (value.isArray()) {
            for (JsonNode child : value) {
                collectPaths(child, currentPath + "[]", paths);
            }
            return;
        }
        if (value.isObject()) {
            value.fields().forEachRemaining(field ->
                    collectPaths(field.getValue(), currentPath + "." + field.getKey(), paths));
        }
    }
}
