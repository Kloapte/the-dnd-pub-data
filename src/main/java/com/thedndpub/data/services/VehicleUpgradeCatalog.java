package com.thedndpub.data.services;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.vehicle.VehicleUpgradeDefinition;
import com.thedndpub.data.dto.vehicle.VehicleUpgradeId;
import com.thedndpub.data.dto.vehicle.VehicleUpgradeSummaryDto;
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
public final class VehicleUpgradeCatalog {
    private final Map<String, VehicleUpgradeDefinition> byId;
    private final Map<String, List<VehicleUpgradeDefinition>> byName;
    private final List<VehicleUpgradeSummaryDto> summaries;

    @Autowired
    public VehicleUpgradeCatalog(VehiclesSource source, SourceRegistry registry) {
        this(source.file(), registry);
    }

    public VehicleUpgradeCatalog(VehicleFileDte source, SourceRegistry registry) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(registry, "registry");
        if (source.vehicleUpgrade() == null || source.vehicleUpgrade().isEmpty()) {
            throw new IllegalStateException("Vehicle Upgrade source collection must not be empty");
        }
        Map<String, VehicleUpgradeDefinition> definitions = new TreeMap<>();
        Map<String, List<VehicleUpgradeDefinition>> names = new TreeMap<>();
        List<VehicleUpgradeSummaryDto> summaryValues = new ArrayList<>();
        for (VehicleUpgradeDte authored : source.vehicleUpgrade()) {
            validateIdentity(authored.name(), authored.source(), registry);
            String idValue = "vehicle-upgrade:" + authored.source() + ":" + slug(authored.name());
            VehicleUpgradeDefinition definition = new VehicleUpgradeDefinition(new VehicleUpgradeId(idValue),
                    authored.name(), new SourceReference(authored.source(), authored.page()), authored.upgradeType(),
                    authored.page(), authored.entries());
            if (definitions.putIfAbsent(idValue, definition) != null) {
                throw new IllegalStateException("Duplicate Vehicle Upgrade ID: " + idValue);
            }
            names.computeIfAbsent(normalize(authored.name()), ignored -> new ArrayList<>()).add(definition);
            summaryValues.add(VehicleUpgradeSummaryDto.from(definition));
        }
        summaries = sortSummaries(summaryValues);
        byId = Collections.unmodifiableMap(definitions);
        byName = immutableNameIndex(names);
    }

    public List<VehicleUpgradeDefinition> definitions() {
        return List.copyOf(byId.values());
    }

    public List<VehicleUpgradeSummaryDto> summaries() {
        return summaries;
    }

    public VehicleUpgradeDefinition findById(String id) {
        return byId.get(id);
    }

    public List<VehicleUpgradeDefinition> findByName(String name) {
        return name == null || name.isBlank() ? List.of() : byName.getOrDefault(normalize(name), List.of());
    }

    public int size() {
        return byId.size();
    }

    private static void validateIdentity(String name, String source, SourceRegistry registry) {
        if (name == null || name.isBlank() || source == null || source.isBlank()) {
            throw new IllegalStateException("Vehicle Upgrade source has a record without name/source identity");
        }
        if (registry.find(source) == null) {
            throw new IllegalStateException("Vehicle Upgrade references unknown source identity: " + source);
        }
    }

    private static List<VehicleUpgradeSummaryDto> sortSummaries(List<VehicleUpgradeSummaryDto> values) {
        values.sort(Comparator.comparing(VehicleUpgradeSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.source().identity().code(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(value -> value.id().value()));
        return List.copyOf(values);
    }

    private static Map<String, List<VehicleUpgradeDefinition>> immutableNameIndex(
            Map<String, List<VehicleUpgradeDefinition>> values) {
        Map<String, List<VehicleUpgradeDefinition>> result = new TreeMap<>();
        values.forEach((name, matches) -> result.put(name, matches.stream()
                .sorted(Comparator.comparing((VehicleUpgradeDefinition value) -> value.source().identity().code(),
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
