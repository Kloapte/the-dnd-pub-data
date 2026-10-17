package com.thedndpub.data.services;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.vehicle.VehicleDefinition;
import com.thedndpub.data.dto.vehicle.VehicleId;
import com.thedndpub.data.dto.vehicle.VehicleMechanics;
import com.thedndpub.data.dto.vehicle.VehicleSummaryDto;
import com.thedndpub.data.dte.vehicle.VehicleDte;
import com.thedndpub.data.dte.vehicle.VehicleOtherSourceDte;
import com.thedndpub.data.dte.vehicle.VehicleFileDte;
import com.thedndpub.data.dte.vehicle.VehicleUpgradeDte;
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
public final class VehicleCatalog {
    private final Map<String, VehicleDefinition> byId;
    private final Map<String, List<VehicleDefinition>> byName;
    private final List<VehicleSummaryDto> summaries;

    @Autowired
    public VehicleCatalog(VehiclesSource source, SourceRegistry registry) {
        this(source.file(), registry);
    }

    public VehicleCatalog(VehicleFileDte source, SourceRegistry registry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(registry, "registry");
        if (source.vehicle() == null || source.vehicle().isEmpty()) {
            throw new IllegalStateException("Vehicle source collection must not be empty");
        }
        Map<String, VehicleDefinition> definitions = new TreeMap<>();
        Map<String, List<VehicleDefinition>> names = new TreeMap<>();
        List<VehicleSummaryDto> summaryValues = new ArrayList<>();
        for (VehicleDte authored : source.vehicle()) {
            validateIdentity(authored.name(), authored.source(), registry);
            validateProvenance(authored, registry);
            String idValue = "vehicle:" + authored.source() + ":" + slug(authored.name());
            VehicleDefinition definition = new VehicleDefinition(new VehicleId(idValue), authored.name(),
                    new SourceReference(authored.source(), authored.page()), authored.vehicleType(),
                    VehicleMechanics.fromSource(authored), authored);
            if (definitions.putIfAbsent(idValue, definition) != null) {
                throw new IllegalStateException("Duplicate Vehicle ID: " + idValue);
            }
            names.computeIfAbsent(normalize(authored.name()), ignored -> new ArrayList<>()).add(definition);
            summaryValues.add(VehicleSummaryDto.from(definition));
        }
        summaries = sortSummaries(summaryValues);
        byId = Collections.unmodifiableMap(definitions);
        byName = immutableNameIndex(names);
    }

    public List<VehicleDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<VehicleSummaryDto> summaries() {
        return summaries;
    }

    public VehicleDefinition findById(String id) {
        return byId.get(id);
    }

    public List<VehicleDefinition> findByName(String name) {
        return name == null || name.isBlank() ? List.of() : byName.getOrDefault(normalize(name), List.of());
    }

    public int size() {
        return byId.size();
    }

    private static void validateProvenance(VehicleDte vehicle, SourceRegistry registry) {
        if (vehicle.otherSources() != null) {
            for (VehicleOtherSourceDte reference : vehicle.otherSources()) {
                requireSource(reference.source(), registry);
            }
        }
        if (vehicle.reprintedAs() != null) {
            for (String token : vehicle.reprintedAs()) {
                String[] parts = token.split("\\|", -1);
                if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                    throw new IllegalStateException("Malformed vehicle reprint UID: " + token);
                }
                requireSource(parts[1], registry);
            }
        }
    }

    private static void validateIdentity(String name, String source, SourceRegistry registry) {
        if (name == null || name.isBlank() || source == null || source.isBlank()) {
            throw new IllegalStateException("Vehicle source contains a record without name/source identity");
        }
        requireSource(source, registry);
    }

    private static void requireSource(String source, SourceRegistry registry) {
        if (source == null || registry.find(source) == null) {
            throw new IllegalStateException("Vehicle references unknown authored source identity: " + source);
        }
    }

    private static List<VehicleSummaryDto> sortSummaries(List<VehicleSummaryDto> values) {
        values.sort(Comparator.comparing(VehicleSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value()));
        return List.copyOf(values);
    }

    private static Map<String, List<VehicleDefinition>> immutableNameIndex(
            Map<String, List<VehicleDefinition>> values) {
        Map<String, List<VehicleDefinition>> result = new TreeMap<>();
        values.forEach((name, matches) -> result.put(name, matches.stream()
                .sorted(Comparator.comparing((VehicleDefinition value) -> value.source().identity().code(),
                        String.CASE_INSENSITIVE_ORDER).thenComparing(value -> value.id().value()))
                .toList()));
        return Collections.unmodifiableMap(result);
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
        return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
