package com.thedndpub.data.services;

import com.thedndpub.data.dto.traphazard.HazardDefinition;
import com.thedndpub.data.dto.traphazard.HazardMapper;
import com.thedndpub.data.dto.traphazard.HazardSummaryDto;
import com.thedndpub.data.dte.traphazard.HazardDte;
import com.thedndpub.data.references.ReferenceUidParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public final class HazardCatalog {
    private final Map<String, HazardDefinition> byId;
    private final Map<String, List<HazardDefinition>> byName;
    private final List<HazardSummaryDto> summaries;

    @Autowired
    public HazardCatalog(TrapHazardsSource source, HazardMapper normalizer, SourceRegistry registry) {
        this(source.file().hazard(), normalizer, registry);
    }

    public HazardCatalog(List<HazardDte> source, HazardMapper normalizer, SourceRegistry registry) {
        Objects.requireNonNull(source, "source");
        if (source.isEmpty()) {
            throw new IllegalStateException("Hazard source contains no definitions");
        }
        Map<String, HazardDefinition> ids = new TreeMap<>();
        Map<String, List<HazardDefinition>> names = new TreeMap<>();
        List<HazardSummaryDto> summaryList = new ArrayList<>();
        for (HazardDte authored : source) {
            requireSource(registry, authored.source());
            validateMetadata(registry, authored);
            HazardDefinition definition = normalizer.map(authored);
            if (ids.putIfAbsent(definition.id().value(), definition) != null) {
                throw new IllegalStateException("Duplicate hazard ID: " + definition.id().value());
            }
            names.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(HazardSummaryDto.from(definition));
        }
        summaryList.sort(Comparator.comparing(HazardSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value()));
        summaries = List.copyOf(summaryList);
        byId = Collections.unmodifiableMap(ids);
        byName = immutableNameIndex(names);
    }

    public List<HazardDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<HazardSummaryDto> summaries() {
        return summaries;
    }

    public HazardDefinition findById(String id) {
        return byId.get(id);
    }

    public List<HazardDefinition> findByName(String name) {
        return name == null || name.isBlank() ? List.of() : byName.getOrDefault(normalize(name), List.of());
    }

    public int size() {
        return byId.size();
    }

    private static void validateMetadata(SourceRegistry registry, HazardDte authored) {
        if (authored.otherSources() != null) {
            authored.otherSources().forEach(reference -> requireSource(registry, reference.source()));
        }
        if (authored.reprintedAs() != null) {
            authored.reprintedAs().forEach(token -> {
                String source = ReferenceUidParser.parse(token).source();
                if (source != null) {
                    requireSource(registry, source);
                }
            });
        }
    }

    private static void requireSource(SourceRegistry registry, String source) {
        if (registry.find(source) == null) {
            throw new IllegalStateException("Hazard refers to an unknown authored source identity: " + source);
        }
    }

    private static Map<String, List<HazardDefinition>> immutableNameIndex(Map<String, List<HazardDefinition>> values) {
        Map<String, List<HazardDefinition>> result = new TreeMap<>();
        values.forEach((name, matches) -> result.put(name, matches.stream()
                .sorted(Comparator.comparing((HazardDefinition item) -> item.source().identity().code(),
                        String.CASE_INSENSITIVE_ORDER).thenComparing(item -> item.id().value()))
                .toList()));
        return Collections.unmodifiableMap(result);
    }

    private static String normalize(String name) {
        return Normalizer.normalize(name.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
