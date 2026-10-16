package com.thedndpub.data.importer.classes;

import com.thedndpub.data.services.SourceRegistry;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** Registry validation covers explicit source slots in both structured content and inline markup. */
public final class ClassSourceValidator {
    private static final Pattern TAG = Pattern.compile("\\{@(\\w+)\\s+([^{}]*)}");
    private static final Pattern SOURCE_FILTER = Pattern.compile("(?:^|\\|)source=([^|{}]+)", Pattern.CASE_INSENSITIVE);
    private static final Map<String, int[]> SPECIAL_SLOTS = Map.of(
            "classFeature", new int[]{2, 4}, "subclassFeature", new int[]{2, 4, 6},
            "subclass", new int[]{2, 3}, "deity", new int[]{2});
    private static final Set<String> SIMPLE_TAGS = Set.of(
            "spell", "item", "itemProperty", "itemMastery", "feat", "optfeature", "background", "race",
            "action", "condition", "disease", "sense", "skill", "language", "class", "creature", "book",
            "variantrule", "table", "deck", "reward", "facility", "vehicle", "object", "trap", "hazard");
    private final SourceRegistry registry;

    public ClassSourceValidator(SourceRegistry registry) {
        this.registry = registry;
    }

    public Set<String> validate(ClassSourceCorpus corpus) {
        Set<String> sources = new HashSet<>();
        for (ClassSourceRecord record : corpus.records()) {
            ClassIdentities.identity(record.data());
            ClassSourceStrings.visit(record.data(), record.location().pointer(), (path, value) -> {
                if (value.contains("{{")) {
                    throw new IllegalStateException("Unresolved class source placeholder at " + path);
                }
                if (value.contains("<$") && !path.endsWith(".preparedSpells")) {
                    throw new IllegalStateException("Unexpected class template placeholder at " + path);
                }
                if (path.matches(".*\\.(source|classSource|subclassSource)$")) {
                    require(value, path, sources);
                }
                if (path.contains(".reprintedAs[")) {
                    String[] parts = value.split("\\|", -1);
                    validateSlots(value, parts.length == 4 ? new int[]{2, 3} : new int[]{1}, path, sources);
                }
                if (path.endsWith(".classFeature")) {
                    validateSlots(value, SPECIAL_SLOTS.get("classFeature"), path, sources);
                } else if (path.endsWith(".subclassFeature") || path.contains(".subclassFeatures[")) {
                    validateSlots(value, SPECIAL_SLOTS.get("subclassFeature"), path, sources);
                } else if (path.endsWith(".item") || path.endsWith(".optionalfeature") || path.endsWith(".feat")
                        || path.endsWith(".spell") || (path.contains(".additionalSpells[") && path.contains(".from["))
                        || path.contains(".required.")) {
                    validateSlots(value, new int[]{1}, path, sources);
                }
                var tags = TAG.matcher(value);
                while (tags.find()) {
                    String kind = tags.group(1);
                    int[] slots = SPECIAL_SLOTS.get(kind);
                    if (slots == null && SIMPLE_TAGS.contains(kind)) {
                        slots = new int[]{1};
                    }
                    if (slots != null) {
                        validateSlots(tags.group(2), slots, path, sources);
                    }
                    if (kind.equals("class")) {
                        validateSlots(tags.group(2), new int[]{4}, path, sources);
                    }
                }
                var filters = SOURCE_FILTER.matcher(value);
                while (filters.find()) {
                    for (String code : filters.group(1).split(";")) {
                        require(code.replaceFirst("^!", ""), path, sources);
                    }
                }
            });
        }
        return Set.copyOf(sources);
    }

    private void validateSlots(String uid, int[] slots, String path, Set<String> sources) {
        String[] parts = uid.replaceFirst("#.*$", "").split("\\|", -1);
        for (int slot : slots) {
            if (parts.length > slot && !parts[slot].isEmpty()) {
                require(parts[slot], path, sources);
            }
        }
    }

    private void require(String code, String path, Set<String> sources) {
        if (code == null || code.isBlank() || registry.find(code) == null) {
            throw new IllegalStateException("Unknown class source '" + code + "' at " + path);
        }
        sources.add(registry.find(code).identity().code().toLowerCase(Locale.ROOT));
    }
}
