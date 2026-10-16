package com.thedndpub.data.importer.classes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.classes.ClassExternalKind;
import com.thedndpub.data.dto.classes.ClassExternalReference;
import com.thedndpub.data.dto.classes.ClassReferenceStatus;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dte.item.ItemFileDte;
import com.thedndpub.data.services.FeatCatalog;
import com.thedndpub.data.services.ItemCatalog;
import com.thedndpub.data.services.OptionalFeatureCatalog;
import com.thedndpub.data.services.MonsterCatalog;
import com.thedndpub.data.services.SourceRegistry;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/** Binds reliable identities to existing catalogs, without creating a class catalog or guessing names. */
public final class ClassExternalReferenceResolver {
    private final SourceRegistry registry;
    private final ItemCatalog items;
    private final OptionalFeatureCatalog optionalFeatures;
    private final FeatCatalog feats;
    private final MonsterCatalog monsters;
    private final Set<String> itemGroups;

    public ClassExternalReferenceResolver(
            SourceRegistry registry,
            ItemCatalog items,
            OptionalFeatureCatalog optionalFeatures,
            FeatCatalog feats,
            MonsterCatalog monsters,
            Set<String> itemGroups) {
        this.registry = registry;
        this.items = items;
        this.optionalFeatures = optionalFeatures;
        this.feats = feats;
        this.monsters = monsters;
        this.itemGroups = Set.copyOf(itemGroups);
    }

    public static ClassExternalReferenceResolver fromExistingCatalogs(
            ObjectMapper mapper,
            SourceRegistry registry,
            ItemCatalog items,
            OptionalFeatureCatalog optionalFeatures,
            FeatCatalog feats,
            MonsterCatalog monsters) throws IOException {
        try (InputStream input = new ClassPathResource("data/items.json").getInputStream()) {
            ItemFileDte source = mapper.readerFor(ItemFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
            Set<String> groups = source.itemGroup().stream()
                    .map(group -> key(group.name(), group.source())).collect(Collectors.toSet());
            return new ClassExternalReferenceResolver(registry, items, optionalFeatures, feats, monsters, groups);
        }
    }

    public ClassExternalReference resolve(ClassExternalKind kind, String uid) {
        if (uid == null || uid.isBlank()) {
            throw new IllegalArgumentException("External class reference is empty");
        }
        String[] parts = uid.split("\\|", -1);
        if (parts.length > 2 || parts[0].isBlank()) {
            throw new IllegalArgumentException("Invalid external class UID: " + uid);
        }
        String source = parts.length == 2 && !parts[1].isBlank() ? parts[1] : null;
        if (source == null && kind == ClassExternalKind.OPTIONAL_FEATURE) {
            source = "PHB";
        }
        if (source == null) {
            return new ClassExternalReference(kind, parts[0], null, null, ClassReferenceStatus.UNQUALIFIED);
        }
        if (registry.find(source) == null) {
            throw new IllegalStateException("Unknown external class reference source: " + source);
        }
        SourceIdentity identity = new SourceIdentity(source);
        if (kind == ClassExternalKind.ITEM && itemGroups.contains(key(parts[0], source))) {
            return new ClassExternalReference(ClassExternalKind.ITEM_GROUP, parts[0], identity,
                    null, ClassReferenceStatus.DEFERRED_ITEM_GROUP);
        }
        final String sourceCode = source;
        List<String> matches = switch (kind) {
            case ITEM -> items == null ? List.of() : items.findByName(parts[0]).stream()
                    .filter(value -> value.source().identity().code().equalsIgnoreCase(sourceCode))
                    .map(value -> value.id()).toList();
            case OPTIONAL_FEATURE -> optionalFeatures == null ? List.of() : optionalFeatures.findByName(parts[0]).stream()
                    .filter(value -> value.source().identity().code().equalsIgnoreCase(sourceCode))
                    .map(value -> value.id()).toList();
            case FEAT -> feats == null ? List.of() : feats.findByName(parts[0]).stream()
                    .filter(value -> value.source().identity().code().equalsIgnoreCase(sourceCode))
                    .map(value -> value.id().value()).toList();
            case MONSTER -> {
                var monster = monsters == null ? null : monsters.findByNameAndSource(parts[0], sourceCode);
                yield monster == null ? List.of() : List.of(monster.identity().id().value());
            }
            default -> List.of();
        };
        if (matches.size() > 1) {
            throw new IllegalStateException("Ambiguous external class reference: " + kind + " " + uid);
        }
        return new ClassExternalReference(kind, parts[0], identity,
                matches.isEmpty() ? null : matches.getFirst(),
                matches.isEmpty() ? ClassReferenceStatus.UNRESOLVED : ClassReferenceStatus.RESOLVED);
    }

    private static String key(String name, String source) {
        return (name + "\u0000" + source).toLowerCase(Locale.ROOT);
    }
}
