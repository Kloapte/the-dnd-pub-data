package com.thedndpub.data;

import com.thedndpub.data.dto.monster.MonsterSummaryDto;
import com.thedndpub.data.services.MonsterCatalog;
import com.thedndpub.data.services.MonsterNotFoundException;
import com.thedndpub.data.services.MonstersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DndPubDataApplicationTests {
    @Autowired
    private MonsterCatalog catalog;

    @Autowired
    private MonstersService monstersService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void contextLoadsWithTheCompleteCatalog() {
        assertEquals(4851, catalog.size());
        assertEquals(catalog.size(), catalog.monstersById().size());
        assertEquals(catalog.size(), catalog.classificationsById().size());
        assertTrue(catalog.classificationsById().values().stream()
                .anyMatch(classification -> !classification.actionTags().isEmpty()
                        || !classification.damageTags().isEmpty()
                        || !classification.traitTags().isEmpty()));
        assertThrows(UnsupportedOperationException.class, () -> catalog.summaries().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.monstersById().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.classificationsById().clear());
    }

    @Test
    void filtersAndPaginatesMonsterSummaries() {
        MonsterSummaryDto target = catalog.summaries().stream()
                .filter(summary -> summary.name().toLowerCase().contains("dragon"))
                .findFirst()
                .orElseThrow();

        Page<MonsterSummaryDto> filtered = monstersService.findMonsters(
                "  " + target.name().toUpperCase() + "  ",
                target.source().toLowerCase(),
                PageRequest.of(0, 100));

        assertTrue(filtered.getContent().stream().anyMatch(summary -> summary.id().equals(target.id())));
        assertTrue(filtered.getContent().stream()
                .allMatch(summary -> summary.source().equalsIgnoreCase(target.source())));

        Page<MonsterSummaryDto> firstPage = monstersService.findMonsters(
                null,
                null,
                PageRequest.of(0, 25));
        Page<MonsterSummaryDto> beyondLastPage = monstersService.findMonsters(
                null,
                null,
                PageRequest.of(1000, 25));

        assertEquals(25, firstPage.getContent().size());
        assertEquals(4851, firstPage.getTotalElements());
        assertTrue(beyondLastPage.isEmpty());
        assertEquals(4851, beyondLastPage.getTotalElements());
    }

    @Test
    void sortsResultsAndRejectsInvalidRequests() {
        Page<MonsterSummaryDto> ascending = monstersService.findMonsters(
                null,
                null,
                PageRequest.of(0, 100, Sort.by("name").ascending()));
        Page<MonsterSummaryDto> descending = monstersService.findMonsters(
                null,
                null,
                PageRequest.of(0, 100, Sort.by("name").descending()));
        Page<MonsterSummaryDto> sourceAscending = monstersService.findMonsters(
                null,
                null,
                PageRequest.of(0, 100, Sort.by("source").ascending()));
        Page<MonsterSummaryDto> sourceDescending = monstersService.findMonsters(
                null,
                null,
                PageRequest.of(0, 100, Sort.by("source").descending()));

        assertTrue(ascending.getContent().get(0).name().compareToIgnoreCase(
                ascending.getContent().get(1).name()) <= 0);
        assertTrue(descending.getContent().get(0).name().compareToIgnoreCase(
                descending.getContent().get(1).name()) >= 0);
        assertTrue(sourceAscending.getContent().get(0).source().compareToIgnoreCase(
                sourceAscending.getContent().get(1).source()) <= 0);
        assertTrue(sourceDescending.getContent().get(0).source().compareToIgnoreCase(
                sourceDescending.getContent().get(1).source()) >= 0);
        assertThrows(IllegalArgumentException.class, () -> monstersService.findMonsters(
                null,
                null,
                PageRequest.of(0, 101)));
        assertThrows(IllegalArgumentException.class, () -> monstersService.findMonsters(
                null,
                null,
                PageRequest.of(0, 25, Sort.by("challengeRating"))));
    }

    @Test
    void looksUpByIdAndExactNameAndSource() {
        MonsterSummaryDto target = catalog.summaries().get(0);
        assertEquals(target.id(), monstersService.findById(target.id().value()).identity().id());
        assertEquals(target.id(), monstersService.findByNameAndSource(
                target.name(),
                target.source()).identity().id());

        assertThrows(MonsterNotFoundException.class, () -> monstersService.findById("missing"));
        assertThrows(MonsterNotFoundException.class, () -> monstersService.findByNameAndSource("missing", "MM"));

        MonsterSummaryDto repeatedName = catalog.summaries().stream()
                .filter(candidate -> catalog.summaries().stream()
                        .filter(other -> other.name().equalsIgnoreCase(candidate.name()))
                        .map(MonsterSummaryDto::source)
                        .distinct()
                        .count() > 1)
                .findFirst()
                .orElseThrow();
        assertEquals(repeatedName.id(), monstersService.findByNameAndSource(
                repeatedName.name(),
                repeatedName.source()).identity().id());
    }

    @Test
    void exposesPagedSummariesAndDetailsOverHttp() throws Exception {
        MonsterSummaryDto target = catalog.summaries().get(0);
        String encodedName = UriUtils.encodePathSegment(target.name(), StandardCharsets.UTF_8);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/monsters")
                        .param("page", "0")
                        .param("size", "5")
                        .param("name", target.name())
                        .param("source", target.source())
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.content[0].id.value").value(target.id().value()));

        mockMvc.perform(get("/api/monsters/{id}", target.id().value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identity.id.value").value(target.id().value()));

        mockMvc.perform(get(URI.create("/api/monsters/by-name/" + encodedName))
                        .param("source", target.source()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identity.name").value(target.name()));

        mockMvc.perform(get(URI.create("/api/monsters/by-name/" + encodedName)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        mockMvc.perform(get("/api/monsters/not-a-monster"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/monsters").param("page", "-1"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/monsters").param("size", "101"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/monsters").param("sort", "challengeRating,asc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void supportsConcurrentCatalogReads() throws Exception {
        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(8)) {
            var tasks = IntStream.range(0, 32)
                    .mapToObj(index -> executor.submit(() -> monstersService.findMonsters(
                            index % 2 == 0 ? "dragon" : null,
                            null,
                            PageRequest.of(0, 25)).getContent().size()))
                    .toList();

            for (var task : tasks) {
                assertTrue(task.get() <= 25);
            }
            executor.shutdown();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }
}
