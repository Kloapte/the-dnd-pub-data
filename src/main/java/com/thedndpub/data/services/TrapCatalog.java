package com.thedndpub.data.services;

import com.thedndpub.data.dto.traphazard.TrapDefinition;
import com.thedndpub.data.dto.traphazard.TrapMapper;
import com.thedndpub.data.dto.traphazard.TrapSummaryDto;
import com.thedndpub.data.dte.traphazard.TrapDte;
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
public final class TrapCatalog {
    private final Map<String, TrapDefinition> byId;
    private final Map<String, List<TrapDefinition>> byName;
    private final List<TrapSummaryDto> summaries;

    @Autowired
    public TrapCatalog(TrapHazardsSource source, TrapMapper normalizer, SourceRegistry registry) {
        this(source.file().trap(), normalizer, registry);
    }

    public TrapCatalog(List<TrapDte> source, TrapMapper normalizer, SourceRegistry registry) {
        Objects.requireNonNull(source, "source");
        if (source.isEmpty()) {
            throw new IllegalStateException("Trap source contains no definitions");
        }
        Map<String, TrapDefinition> ids = new TreeMap<>();
        Map<String, List<TrapDefinition>> names = new TreeMap<>();
        List<TrapSummaryDto> summaryList = new ArrayList<>();
        for (TrapDte authored : source) {
            requireSource(registry, authored.source());
            validateReprints(registry, authored.reprintedAs());
            TrapDefinition definition = normalizer.map(authored);
            if (ids.putIfAbsent(definition.id().value(), definition) != null) {
                throw new IllegalStateException("Duplicate trap ID: " + definition.id().value());
            }
            names.computeIfAbsent(normalize(definition.name()), ignored -> new ArrayList<>()).add(definition);
            summaryList.add(TrapSummaryDto.from(definition));
        }
        summaries = sortedSummaries(summaryList);
        byId = Collections.unmodifiableMap(ids);
        byName = immutableNameIndex(names);
    }

    public List<TrapDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<TrapSummaryDto> summaries() {
        return summaries;
    }

    public TrapDefinition findById(String id) {
        return byId.get(id);
    }

    public List<TrapDefinition> findByName(String name) {
        return name == null || name.isBlank() ? List.of() : byName.getOrDefault(normalize(name), List.of());
    }

    public int size() {
        return byId.size();
    }

    private static void validateReprints(SourceRegistry registry, List<String> reprints) {
        if (reprints == null) {
            return;
        }
        for (String token : reprints) {
            String source = ReferenceUidParser.parse(token).source();
            if (source != null) {
                requireSource(registry, source);
            }
        }
    }

    private static void requireSource(SourceRegistry registry, String source) {
        if (registry.find(source) == null) {
            throw new IllegalStateException("Trap refers to an unknown authored source identity: " + source);
        }
    }

    private static List<TrapSummaryDto> sortedSummaries(List<TrapSummaryDto> values) {
        values.sort(Comparator.comparing(TrapSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value()));
        return List.copyOf(values);
    }

    private static Map<String, List<TrapDefinition>> immutableNameIndex(Map<String, List<TrapDefinition>> values) {
        Map<String, List<TrapDefinition>> result = new TreeMap<>();
        values.forEach((name, matches) -> result.put(name, matches.stream()
                .sorted(Comparator.comparing((TrapDefinition item) -> item.source().identity().code(),
                        String.CASE_INSENSITIVE_ORDER).thenComparing(item -> item.id().value()))
                .toList()));
        return Collections.unmodifiableMap(result);
    }

    private static String normalize(String name) {
        return Normalizer.normalize(name.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
