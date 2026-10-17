package com.thedndpub.data.recipe;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.controllers.RecipesApiExceptionHandler;
import com.thedndpub.data.controllers.RecipesController;
import com.thedndpub.data.dto.recipe.RecipeDefinition;
import com.thedndpub.data.dto.recipe.RecipeDetailDto;
import com.thedndpub.data.dto.recipe.RecipeMapper;
import com.thedndpub.data.dte.recipe.RecipeDte;
import com.thedndpub.data.dte.recipe.RecipeIngredientAmountDte;
import com.thedndpub.data.dte.recipe.RecipeIngredientGroupDte;
import com.thedndpub.data.dte.recipe.RecipeIngredientTextDte;
import com.thedndpub.data.dte.recipe.RecipeMakesQuantityDte;
import com.thedndpub.data.dte.recipe.RecipeMakesTextDte;
import com.thedndpub.data.dte.recipe.RecipesFileDte;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.services.RecipeCatalog;
import com.thedndpub.data.services.RecipeNotFoundException;
import com.thedndpub.data.services.RecipesService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RecipesCatalogTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeCatalog catalog;

    @Autowired
    private RecipesService service;

    @Autowired
    private RecipeMapper mapper;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private CrossCatalogResolutionCoordinator resolutionCoordinator;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RecipesController(service))
                .setControllerAdvice(new RecipesApiExceptionHandler())
                .build();
    }

    @Test
    void loadsEveryAuthoredRecipeAndValidatesSources() {
        assertEquals(316, catalog.definitions().size());
        Map<String, Long> sourceCounts = catalog.definitions().stream()
                .collect(Collectors.groupingBy(
                        definition -> definition.source().identity().code(),
                        Collectors.counting()));
        assertEquals(Map.of("HF", 79L, "HFDoMM", 10L, "HFFotM", 76L, "HFLotT", 75L, "PaF", 76L),
                sourceCounts);
        assertEquals(181, sourceRegistry.size());
        assertNotNull(sourceRegistry.find("HFDoMM"));
        assertNull(sourceRegistry.find("HFDoMM").publication());
        assertEquals(0, catalog.definitions().stream()
                .filter(definition -> definition.source().page() == null)
                .filter(definition -> !definition.source().identity().code().equals("HFDoMM"))
                .count());
    }

    @Test
    void sourceAndNormalizedDtesRoundTripAllObservedStructures() throws Exception {
        RecipesFileDte source;
        try (InputStream input = new ClassPathResource("data/recipes.json").getInputStream()) {
            source = objectMapper.readerFor(RecipesFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
        JsonNode original;
        try (InputStream input = new ClassPathResource("data/recipes.json").getInputStream()) {
            original = objectMapper.readTree(input);
        }
        ObjectMapper sourceCompatibleMapper = objectMapper.copy()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JsonNode normalized = sourceCompatibleMapper.valueToTree(source);
        String firstDifference = firstDifference(original, normalized, "$");
        assertTrue(firstDifference == null, () -> "Source/DTE mismatch at " + firstDifference);
        assertEquals(316, source.recipe().size());

        long ingredientTextCount = source.recipe().stream()
                .flatMap(recipe -> recipe.ingredients().stream())
                .filter(RecipeIngredientTextDte.class::isInstance)
                .count();
        long ingredientGroupCount = source.recipe().stream()
                .flatMap(recipe -> recipe.ingredients().stream())
                .filter(RecipeIngredientGroupDte.class::isInstance)
                .count();
        long amountCount = source.recipe().stream()
                .flatMap(recipe -> recipe.ingredients().stream())
                .filter(RecipeIngredientAmountDte.class::isInstance)
                .count();
        assertEquals(357, ingredientTextCount);
        assertEquals(95, ingredientGroupCount);
        assertEquals(2_350, amountCount);
        assertTrue(source.recipe().stream().anyMatch(recipe -> recipe.makes() instanceof RecipeMakesTextDte));
        assertTrue(source.recipe().stream().anyMatch(recipe -> recipe.makes() instanceof RecipeMakesQuantityDte));
        assertEquals(222, source.recipe().stream().filter(recipe -> recipe.makes() == null).count());
    }

    @Test
    void identityIsSourceAwareUniqueAndDeterministicallyOrdered() {
        var ids = catalog.definitions().stream()
                .map(definition -> definition.id().value())
                .toList();
        assertEquals(316, new HashSet<>(ids).size());
        assertTrue(ids.stream().allMatch(id -> id.matches("recipe:[^:]+:[a-z0-9-]+")));
        assertEquals(catalog.summaries().stream().map(summary -> summary.id().value()).toList(),
                catalog.summaries().stream().sorted(java.util.Comparator
                        .comparing(com.thedndpub.data.dto.recipe.RecipeSummaryDto::name,
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(summary -> summary.source().code(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(summary -> summary.id().value()))
                        .map(summary -> summary.id().value()).toList());

        RecipeDefinition recipe = catalog.findById("recipe:HFDoMM:exploding-cheese-puffs");
        assertNotNull(recipe);
        assertNull(recipe.source().page());
        assertEquals("HFDoMM", recipe.source().identity().code());
    }

    @Test
    void strictUnknownFieldsAndUnknownPolymorphicVariantsAreRejected() {
        String unknownRoot = """
                {"recipe":[{"name":"Example","source":"HF","ingredients":[],
                "instructions":[],"unexpected":true}]}
                """;
        String unknownIngredientField = """
                {"recipe":[{"name":"Example","source":"HF","ingredients":[
                {"type":"ingredient","entry":"salt","amount1":1,"unexpected":true}],
                "instructions":[]}]}
                """;
        String unknownIngredientType = """
                {"recipe":[{"name":"Example","source":"HF","ingredients":[
                {"type":"other","entry":"salt"}],"instructions":[]}]}
                """;
        assertThrows(Exception.class, () -> objectMapper.readerFor(RecipesFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownRoot));
        assertThrows(Exception.class, () -> objectMapper.readerFor(RecipesFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownIngredientField));
        assertThrows(Exception.class, () -> objectMapper.readerFor(RecipesFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownIngredientType));
    }

    @Test
    void meaningfulZeroAndFalseValuesSurviveTypedDeserializationAndSerialization() throws Exception {
        String source = """
                {"recipe":[{"name":"Zero Test","source":"HF","makes":0,
                "hasFluff":false,"hasFluffImages":false,"ingredients":[
                {"type":"ingredient","entry":"zero quantity marker","amount1":0}],
                "instructions":[]}]}
                """;
        RecipesFileDte parsedSource = objectMapper.readerFor(RecipesFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(source);
        RecipeDte recipe = parsedSource.recipe().getFirst();
        RecipeIngredientAmountDte ingredient = (RecipeIngredientAmountDte) recipe.ingredients().getFirst();
        assertEquals(0, ingredient.amount1().compareTo(java.math.BigDecimal.ZERO));
        assertEquals(new java.math.BigDecimal("0"), ((RecipeMakesQuantityDte) recipe.makes()).value());
        assertEquals(Boolean.FALSE, recipe.hasFluff());
        assertEquals(Boolean.FALSE, recipe.hasFluffImages());

        String serialized = objectMapper.writeValueAsString(recipe);
        assertTrue(serialized.contains("\"amount1\":0"));
        assertTrue(serialized.contains("\"makes\":0"));
        assertTrue(serialized.contains("\"hasFluff\":false"));
        assertTrue(serialized.contains("\"hasFluffImages\":false"));
    }

    @Test
    void nestedContentAndSourceProvenanceAreRetainedInternally() {
        RecipeDefinition recipeWithGroups = catalog.findById("recipe:PaF:captain-s-fancy");
        assertNotNull(recipeWithGroups);
        assertTrue(recipeWithGroups.authoredContent().ingredients().stream()
                .anyMatch(RecipeIngredientGroupDte.class::isInstance));
        assertTrue(recipeWithGroups.authoredContent().instructions().stream()
                .anyMatch(com.thedndpub.data.dte.recipe.RecipeInstructionSectionDte.class::isInstance));

        RecipeDefinition cited = catalog.findById("recipe:HFDoMM:moonwood-artichoke-spread");
        assertNotNull(cited);
        assertEquals("HFLotT", cited.authoredContent().otherSources().getFirst().source());
        assertEquals(56, cited.authoredContent().otherSources().getFirst().page());
        assertTrue(catalog.definitions().stream().flatMap(definition -> definition.authoredContent()
                        .otherSources() == null ? java.util.stream.Stream.empty()
                                : definition.authoredContent().otherSources().stream())
                .allMatch(reference -> sourceRegistry.find(reference.source()) != null));
    }

    @Test
    void safePublicProjectionWithholdsRecipeContent() throws Exception {
        RecipeDetailDto detail = service.findById("recipe:HF:orc-bacon");
        assertEquals(5, detail.ingredientEntryCount());
        String json = objectMapper.writeValueAsString(detail);
        assertTrue(json.contains("\"name\":\"\\\"Orc\\\" Bacon\""));
        assertTrue(json.contains("\"source\":\"HF\""));
        assertFalse(json.contains("ingredients"));
        assertFalse(json.contains("instructions"));
        assertFalse(json.contains("allergenGroups"));
        assertFalse(json.contains("aliases"));
        assertFalse(json.contains("Preheat the oven"));
        assertFalse(json.contains("{@"));
        assertFalse(json.contains("hasFluff"));
    }

    @Test
    void routesSupportMetadataFiltersLookupAndNotFoundBehavior() throws Exception {
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(316));
        mockMvc.perform(get("/api/recipes").param("source", "HF").param("dishType", "snack"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source").value("HF"));
        mockMvc.perform(get("/api/recipes").param("type", "The Low Lantern"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("The Low Lantern"));
        mockMvc.perform(get("/api/recipes/by-name/{name}", "Amphail Braised Beef").param("source", "HF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("recipe:HF:amphail-braised-beef"));
        mockMvc.perform(get("/api/recipes/recipe:missing:unknown"))
                .andExpect(status().isNotFound());
        assertThrows(RecipeNotFoundException.class, () -> service.findById("recipe:missing:unknown"));
    }

    @Test
    void recipesAddNoResolverOccurrencesAndDoNotChangeExistingOutcomes() {
        var results = resolutionCoordinator.snapshot().results();
        assertEquals(0, results.stream()
                .filter(result -> result.reference().owner().catalog().equals("recipe"))
                .count());
        assertEquals(6_246, results.size());
        assertEquals(2_517, countOutcome(results, ResolutionOutcome.RESOLVED));
        assertEquals(2_054, countOutcome(results, ResolutionOutcome.AMBIGUOUS));
        assertEquals(181, countOutcome(results, ResolutionOutcome.UNRESOLVED));
        assertEquals(1_494, countOutcome(results, ResolutionOutcome.DEFERRED));

        long embeddedRecipeReferences = catalog.definitions().stream()
                .flatMap(definition -> allText(definition).stream())
                .mapToLong(text -> java.util.regex.Pattern.compile("\\{@recipe\\s+").matcher(text).results().count())
                .sum();
        assertEquals(8, embeddedRecipeReferences);
    }

    @Test
    void resourceLoadsFromPackagedJarClasspath(@TempDir Path temporaryDirectory) throws Exception {
        Path jarPath = temporaryDirectory.resolve("recipes-resource.jar");
        try (JarOutputStream jar = new JarOutputStream(java.nio.file.Files.newOutputStream(jarPath));
                InputStream input = new ClassPathResource("data/recipes.json").getInputStream()) {
            jar.putNextEntry(new JarEntry("data/recipes.json"));
            input.transferTo(jar);
            jar.closeEntry();
        }

        try (var loader = new java.net.URLClassLoader(
                new java.net.URL[] { jarPath.toUri().toURL() }, null)) {
            try (InputStream input = loader.getResourceAsStream("data/recipes.json")) {
                assertNotNull(input);
                RecipesFileDte packagedSource = objectMapper.readerFor(RecipesFileDte.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(input);
                RecipeCatalog packagedCatalog = new RecipeCatalog(packagedSource, mapper, sourceRegistry);
                assertEquals(316, packagedCatalog.definitions().size());
            }
        }
    }

    private static long countOutcome(
            java.util.List<com.thedndpub.data.references.ResolutionResult> results,
            ResolutionOutcome outcome) {
        return results.stream().filter(result -> result.outcome() == outcome).count();
    }

    private static java.util.List<String> allText(RecipeDefinition definition) {
        java.util.List<String> values = new java.util.ArrayList<>();
        RecipeDte recipe = new RecipeDte(
                definition.name(), definition.authoredContent().aliases(), definition.source().identity().code(),
                definition.source().page(), definition.type(), definition.dishTypes(),
                definition.authoredContent().diet(), definition.authoredContent().allergenGroups(),
                definition.authoredContent().makes(), definition.authoredContent().serves(),
                definition.authoredContent().ingredients(), definition.authoredContent().equipment(),
                definition.authoredContent().instructions(), definition.authoredContent().cookNotes(),
                definition.authoredContent().miscTags(), definition.authoredContent().hasFluff(),
                definition.authoredContent().hasFluffImages(), definition.authoredContent().otherSources());
        values.add(recipe.toString());
        return values;
    }

    private static String firstDifference(JsonNode source, JsonNode mapped, String path) {
        if (source.getNodeType() != mapped.getNodeType()) {
            return path + " types " + source.getNodeType() + " != " + mapped.getNodeType();
        }
        if (source.isObject()) {
            java.util.Iterator<String> fieldNames = source.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                if (!mapped.has(fieldName)) {
                    return path + "." + fieldName + " is missing";
                }
                String childDifference = firstDifference(source.get(fieldName), mapped.get(fieldName),
                        path + "." + fieldName);
                if (childDifference != null) {
                    return childDifference;
                }
            }
            return source.size() == mapped.size() ? null : path + " has a different property count";
        }
        if (source.isArray()) {
            if (source.size() != mapped.size()) {
                return path + " has array sizes " + source.size() + " != " + mapped.size();
            }
            for (int index = 0; index < source.size(); index++) {
                String childDifference = firstDifference(source.get(index), mapped.get(index),
                        path + "[" + index + "]");
                if (childDifference != null) {
                    return childDifference;
                }
            }
            return null;
        }
        if (source.isNumber() && mapped.isNumber()) {
            return source.decimalValue().compareTo(mapped.decimalValue()) == 0
                    ? null
                    : path + " numeric values differ";
        }
        return source.equals(mapped) ? null : path + " values differ";
    }
}
