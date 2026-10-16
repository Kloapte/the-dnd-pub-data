package com.thedndpub.data.services;

import com.thedndpub.data.dto.classes.ClassDefinition;
import com.thedndpub.data.dto.classes.ClassDefinitionKind;
import com.thedndpub.data.dto.classes.ClassFeatureDefinition;
import com.thedndpub.data.dto.classes.ClassFeatureGraphEdge;
import com.thedndpub.data.dto.classes.ClassFeatureUnlock;
import com.thedndpub.data.dto.classes.ClassNormalizationResult;
import com.thedndpub.data.dto.classes.SubclassDefinition;
import com.thedndpub.data.dto.classes.SubclassFeatureDefinition;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Immutable ID and graph indexes shared by the four class definition catalogs. */
@Component
public final class ClassDomainIndex {
    private final Map<String, ClassDefinition> classes;
    private final Map<String, SubclassDefinition> subclasses;
    private final Map<String, ClassFeatureDefinition> classFeatures;
    private final Map<String, SubclassFeatureDefinition> subclassFeatures;
    private final Map<String, Integer> featureOrder;
    private final Map<String, List<ClassDefinition>> classesByName;
    private final Map<String, List<ClassDefinition>> classesBySource;
    private final Map<String, List<SubclassDefinition>> subclassesByName;
    private final Map<String, List<SubclassDefinition>> subclassesBySource;
    private final Map<String, List<ClassFeatureDefinition>> classFeaturesByName;
    private final Map<String, List<ClassFeatureDefinition>> classFeaturesBySource;
    private final Map<String, List<SubclassFeatureDefinition>> subclassFeaturesByName;
    private final Map<String, List<SubclassFeatureDefinition>> subclassFeaturesBySource;

    public ClassDomainIndex(ClassDomainSnapshot snapshot, SourceRegistry registry) {
        ClassNormalizationResult result = snapshot.result();
        classes = index(result.classes(), value -> value.id().value());
        subclasses = index(result.subclasses(), value -> value.id().value());
        classFeatures = index(result.classFeatures(), value -> value.id().value());
        subclassFeatures = index(result.subclassFeatures(), value -> value.id().value());
        classesByName = group(classes(), ClassDefinition::name);
        classesBySource = group(classes(), value -> value.source().identity().code());
        subclassesByName = group(subclasses(), SubclassDefinition::name);
        subclassesBySource = group(subclasses(), value -> value.source().identity().code());
        classFeaturesByName = group(classFeatures(), ClassFeatureDefinition::name);
        classFeaturesBySource = group(classFeatures(), value -> value.source().identity().code());
        subclassFeaturesByName = group(subclassFeatures(), SubclassFeatureDefinition::name);
        subclassFeaturesBySource = group(subclassFeatures(), value -> value.source().identity().code());
        Map<String, Integer> order = new HashMap<>();
        result.classes().forEach(owner -> owner.features().forEach(reference ->
                order.put(reference.feature().targetId(), reference.order())));
        result.subclasses().forEach(owner -> owner.features().forEach(reference ->
                order.put(reference.feature().targetId(), reference.order())));
        featureOrder = Collections.unmodifiableMap(order);
        validateSources(result, registry);
        validateGraph(result);
    }

    public List<ClassDefinition> classes() {
        return sorted(classes.values(), ClassDefinition::name, ClassDefinition::source, value -> value.id().value());
    }

    public List<SubclassDefinition> subclasses() {
        return sorted(subclasses.values(), SubclassDefinition::name, SubclassDefinition::source,
                value -> value.id().value());
    }

    public List<ClassFeatureDefinition> classFeatures() {
        return sorted(classFeatures.values(), ClassFeatureDefinition::name, ClassFeatureDefinition::source,
                value -> value.id().value());
    }

    public List<SubclassFeatureDefinition> subclassFeatures() {
        return sorted(subclassFeatures.values(), SubclassFeatureDefinition::name,
                SubclassFeatureDefinition::source, value -> value.id().value());
    }

    public ClassDefinition classById(String id) {
        return classes.get(id);
    }

    public SubclassDefinition subclassById(String id) {
        return subclasses.get(id);
    }

    public ClassFeatureDefinition classFeatureById(String id) {
        return classFeatures.get(id);
    }

    public SubclassFeatureDefinition subclassFeatureById(String id) {
        return subclassFeatures.get(id);
    }

    public List<ClassDefinition> classesNamed(String name) {
        return lookup(classesByName, name);
    }

    public List<ClassDefinition> classesFromSource(String source) {
        return lookup(classesBySource, source);
    }

    public List<SubclassDefinition> subclassesNamed(String name) {
        return lookup(subclassesByName, name);
    }

    public List<SubclassDefinition> subclassesFromSource(String source) {
        return lookup(subclassesBySource, source);
    }

    public List<ClassFeatureDefinition> classFeaturesNamed(String name) {
        return lookup(classFeaturesByName, name);
    }

    public List<ClassFeatureDefinition> classFeaturesFromSource(String source) {
        return lookup(classFeaturesBySource, source);
    }

    public List<SubclassFeatureDefinition> subclassFeaturesNamed(String name) {
        return lookup(subclassFeaturesByName, name);
    }

    public List<SubclassFeatureDefinition> subclassFeaturesFromSource(String source) {
        return lookup(subclassFeaturesBySource, source);
    }

    public Integer order(String id) {
        return featureOrder.get(id);
    }

    public int orderedFeatureCount() {
        return featureOrder.size();
    }

    private static <T> Map<String, T> index(List<T> values, java.util.function.Function<T, String> id) {
        Map<String, T> result = new TreeMap<>();
        for (T value : values) {
            String key = id.apply(value);
            if (result.putIfAbsent(key, value) != null) {
                throw new IllegalStateException("Duplicate public class-domain ID: " + key);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static <T> Map<String, List<T>> group(
            List<T> values, java.util.function.Function<T, String> keyFunction) {
        Map<String, List<T>> mutable = new TreeMap<>();
        for (T value : values) {
            mutable.computeIfAbsent(normalize(keyFunction.apply(value)), ignored -> new ArrayList<>()).add(value);
        }
        Map<String, List<T>> result = new TreeMap<>();
        mutable.forEach((key, matches) -> result.put(key, List.copyOf(matches)));
        return Collections.unmodifiableMap(result);
    }

    private static <T> List<T> lookup(Map<String, List<T>> index, String value) {
        return index.getOrDefault(normalize(value), List.of());
    }

    private static void validateSources(ClassNormalizationResult result, SourceRegistry registry) {
        List<String> sources = new ArrayList<>();
        result.classes().forEach(value -> addSources(sources, value.source().identity().code(),
                value.sources().otherSources().stream().map(source -> source.identity().code()).toList()));
        result.subclasses().forEach(value -> addSources(sources, value.source().identity().code(),
                value.sources().otherSources().stream().map(source -> source.identity().code()).toList()));
        result.classFeatures().forEach(value -> addSources(sources, value.source().identity().code(),
                value.sources().otherSources().stream().map(source -> source.identity().code()).toList()));
        result.subclassFeatures().forEach(value -> addSources(sources, value.source().identity().code(),
                value.sources().otherSources().stream().map(source -> source.identity().code()).toList()));
        sources.forEach(source -> {
            if (registry.find(source) == null) {
                throw new IllegalStateException("Unknown class public source: " + source);
            }
        });
    }

    private static void addSources(List<String> target, String primary, List<String> secondary) {
        target.add(primary);
        target.addAll(secondary);
    }

    private void validateGraph(ClassNormalizationResult result) {
        if (classes.size() != 30 || subclasses.size() != 330
                || classFeatures.size() != 677 || subclassFeatures.size() != 1499) {
            throw new IllegalStateException("Unexpected class-domain definition counts");
        }
        if (result.featureGraph().size() != 2725) {
            throw new IllegalStateException("Unexpected effective class feature reference count: "
                    + result.featureGraph().size());
        }
        Set<String> allIds = new HashSet<>();
        allIds.addAll(classes.keySet());
        allIds.addAll(subclasses.keySet());
        allIds.addAll(classFeatures.keySet());
        allIds.addAll(subclassFeatures.keySet());
        if (allIds.size() != 2536) {
            throw new IllegalStateException("Class-domain IDs collide across definition kinds");
        }
        for (SubclassDefinition subclass : subclasses.values()) {
            if (!classes.containsKey(subclass.parentClassId().value())) {
                throw new IllegalStateException("Broken parent class: " + subclass.id().value());
            }
        }
        for (ClassFeatureDefinition feature : classFeatures.values()) {
            if (!classes.containsKey(feature.parentClassId().value())) {
                throw new IllegalStateException("Broken class feature parent: " + feature.id().value());
            }
        }
        for (SubclassFeatureDefinition feature : subclassFeatures.values()) {
            SubclassDefinition parent = subclasses.get(feature.parentSubclassId().value());
            if (parent == null || !parent.parentClassId().equals(feature.parentClassId())) {
                throw new IllegalStateException("Broken subclass feature parent: " + feature.id().value());
            }
        }
        for (ClassDefinition owner : classes.values()) {
            owner.subclasses().forEach(id -> {
                SubclassDefinition child = subclasses.get(id.value());
                if (child == null || !child.parentClassId().equals(owner.id())) {
                    throw new IllegalStateException("Broken subclass relationship: " + id.value());
                }
            });
            owner.features().forEach(ref -> requireFeature(ref, classFeatures, "class"));
        }
        for (SubclassDefinition owner : subclasses.values()) {
            owner.features().forEach(ref -> requireFeature(ref, subclassFeatures, "subclass"));
        }
        for (ClassFeatureGraphEdge edge : result.featureGraph()) {
            boolean found = edge.target().kind() == ClassDefinitionKind.CLASS_FEATURE
                    ? classFeatures.containsKey(edge.target().targetId())
                    : subclassFeatures.containsKey(edge.target().targetId());
            if (!found) {
                throw new IllegalStateException("Broken public feature reference: " + edge.target().targetId());
            }
            if (!allIds.contains(edge.ownerId())) {
                throw new IllegalStateException("Broken feature graph owner: " + edge.ownerId());
            }
        }
    }

    private static void requireFeature(ClassFeatureUnlock ref, Map<String, ?> index, String owner) {
        if (!index.containsKey(ref.feature().targetId())) {
            throw new IllegalStateException("Broken " + owner + " feature reference: "
                    + ref.feature().targetId());
        }
    }

    private static <T> List<T> sorted(Collection<T> values, java.util.function.Function<T, String> name,
            java.util.function.Function<T, com.thedndpub.data.dto.source.SourceReference> source,
            java.util.function.Function<T, String> id) {
        return values.stream().sorted(Comparator.comparing((T value) -> normalize(name.apply(value)))
                .thenComparing(value -> normalize(source.apply(value).identity().code()))
                .thenComparing(id)).toList();
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }
}
