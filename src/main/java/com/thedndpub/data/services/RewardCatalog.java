package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.reward.RewardDefinition;
import com.thedndpub.data.dto.reward.RewardMapper;
import com.thedndpub.data.dto.reward.RewardSummaryDto;
import com.thedndpub.data.dte.reward.RewardDte;
import com.thedndpub.data.dte.reward.RewardReprintTokenDte;
import com.thedndpub.data.dte.reward.RewardReprintUidDte;
import com.thedndpub.data.references.ReferenceUidParser;
import com.thedndpub.data.dte.reward.RewardsFileDte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Component
public final class RewardCatalog {
    private static final String RESOURCE = "data/rewards.json";
    private final Map<String, RewardDefinition> byId;
    private final Map<String, List<RewardDefinition>> byName;
    private final List<RewardSummaryDto> summaries;

    @Autowired
    public RewardCatalog(ObjectMapper mapper, RewardMapper rewardMapper, SourceRegistry sourceRegistry) {
        this(load(mapper), rewardMapper, sourceRegistry);
    }

    public RewardCatalog(RewardsFileDte source, RewardMapper mapper, SourceRegistry registry) {
        Objects.requireNonNull(source, "source");
        if (source.reward() == null || source.reward().isEmpty()) {
            throw new IllegalStateException("Reward source contains no definitions");
        }
        Map<String, RewardDefinition> ids = new TreeMap<>();
        Map<String, List<RewardDefinition>> names = new TreeMap<>();
        List<RewardSummaryDto> summaryList = new ArrayList<>();
        for (RewardDte authored : source.reward()) {
            if (authored == null || registry.find(authored.source()) == null) {
                throw new IllegalStateException("Reward has missing or unknown authored source identity");
            }
            validateSecondarySources(authored, registry);
            RewardDefinition definition = mapper.map(authored);
            if (ids.putIfAbsent(definition.id().value(), definition) != null) {
                throw new IllegalStateException("Duplicate reward ID: " + definition.id().value());
            }
            names.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(RewardSummaryDto.from(definition));
        }
        summaryList.sort(Comparator.comparing(RewardSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value()));
        byId = Collections.unmodifiableMap(ids);
        byName = immutableNameIndex(names);
        summaries = List.copyOf(summaryList);
    }

    private static void validateSecondarySources(RewardDte reward, SourceRegistry registry) {
        if (reward.otherSources() != null) {
            reward.otherSources().forEach(source -> requireRegisteredSource(registry, source.source()));
        }
        if (reward.seeAlsoFacility() != null) {
            reward.seeAlsoFacility().forEach(token -> validateUidSource(registry, token));
        }
        if (reward.reprintedAs() != null) {
            reward.reprintedAs().forEach(reprint -> {
                String token = reprint instanceof RewardReprintTokenDte text
                        ? text.token()
                        : ((RewardReprintUidDte) reprint).uid();
                validateUidSource(registry, token);
            });
        }
    }

    private static void validateUidSource(SourceRegistry registry, String token) {
        String source = ReferenceUidParser.parse(token).source();
        if (source != null) {
            requireRegisteredSource(registry, source);
        }
    }

    private static void requireRegisteredSource(SourceRegistry registry, String source) {
        if (registry.find(source) == null) {
            throw new IllegalStateException("Reward refers to an unknown authored source identity: " + source);
        }
    }

    public List<RewardDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<RewardSummaryDto> summaries() {
        return summaries;
    }

    public RewardDefinition findById(String id) {
        return byId.get(id);
    }

    public List<RewardDefinition> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return byName.getOrDefault(normalize(name), List.of());
    }

    public int size() {
        return byId.size();
    }

    private static RewardsFileDte load(ObjectMapper mapper) {
        try (InputStream input = new ClassPathResource(RESOURCE).getInputStream()) {
            return mapper.readerFor(RewardsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load reward resource " + RESOURCE, exception);
        }
    }

    private static Map<String, List<RewardDefinition>> immutableNameIndex(
            Map<String, List<RewardDefinition>> values) {
        Map<String, List<RewardDefinition>> result = new TreeMap<>();
        values.forEach((name, matches) -> result.put(name, matches.stream().sorted(
                Comparator.comparing((RewardDefinition item) -> item.source().identity().code(),
                        String.CASE_INSENSITIVE_ORDER).thenComparing(item -> item.id().value())).toList()));
        return Collections.unmodifiableMap(result);
    }

    private static String normalize(String name) {
        return Normalizer.normalize(name.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
