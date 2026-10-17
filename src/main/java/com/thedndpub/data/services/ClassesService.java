package com.thedndpub.data.services;

import com.thedndpub.data.dto.classes.ClassDefinition;
import com.thedndpub.data.dto.classes.ClassDto;
import com.thedndpub.data.dto.classes.ClassFeatureDefinition;
import com.thedndpub.data.dto.classes.ClassFeatureDto;
import com.thedndpub.data.dto.classes.ClassFeatureSummaryDto;
import com.thedndpub.data.dto.classes.ClassPublicMapper;
import com.thedndpub.data.dto.classes.ClassSummaryDto;
import com.thedndpub.data.dto.classes.SubclassDefinition;
import com.thedndpub.data.dto.classes.SubclassDto;
import com.thedndpub.data.dto.classes.SubclassFeatureDefinition;
import com.thedndpub.data.dto.classes.SubclassFeatureDto;
import com.thedndpub.data.dto.classes.SubclassFeatureSummaryDto;
import com.thedndpub.data.dto.classes.SubclassSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public final class ClassesService {
    private final ClassCatalog classes;
    private final SubclassCatalog subclasses;
    private final ClassFeatureCatalog classFeatures;
    private final SubclassFeatureCatalog subclassFeatures;
    private final ClassDomainIndex index;
    private final ClassPublicMapper mapper;
    private final PublicReferenceProjection references;

    public ClassesService(ClassCatalog classes, SubclassCatalog subclasses, ClassFeatureCatalog classFeatures,
            SubclassFeatureCatalog subclassFeatures, ClassDomainIndex index, ClassPublicMapper mapper,
            PublicReferenceProjection references) {
        this.classes = classes;
        this.subclasses = subclasses;
        this.classFeatures = classFeatures;
        this.subclassFeatures = subclassFeatures;
        this.index = index;
        this.mapper = mapper;
        this.references = references;
    }

    public List<ClassSummaryDto> findClasses(String name, String source) {
        return classCandidates(source).stream()
                .filter(value -> matches(value.name(), value.source().identity().code(), name, source))
                .map(mapper::summary).toList();
    }

    public ClassDto classById(String id) {
        ClassDefinition value = required(classes.findById(id), "class", id);
        return mapper.detail(value).withCrossCatalogReferences(references.forOwner("class", id));
    }

    public List<ClassDto> classesByName(String name, String source) {
        return requireMatches(classes.findByName(name).stream().filter(value -> exact(value.name(), name)
                && (source == null || value.source().identity().code().equalsIgnoreCase(source)))
                .map(value -> mapper.detail(value).withCrossCatalogReferences(
                        references.forOwner("class", value.id().value()))).toList(), "class", name);
    }

    public List<SubclassSummaryDto> findSubclasses(String name, String source, String classId) {
        return subclassCandidates(source).stream()
                .filter(value -> matches(value.name(), value.source().identity().code(), name, source))
                .filter(value -> classId == null || value.parentClassId().value().equals(classId))
                .map(mapper::summary).toList();
    }

    public SubclassDto subclassById(String id) {
        SubclassDefinition value = required(subclasses.findById(id), "subclass", id);
        return mapper.detail(value).withCrossCatalogReferences(references.forOwner("subclass", id));
    }

    public List<SubclassDto> subclassesByName(String name, String source) {
        return requireMatches(subclasses.findByName(name).stream().filter(value -> exact(value.name(), name)
                && (source == null || value.source().identity().code().equalsIgnoreCase(source)))
                .map(value -> mapper.detail(value).withCrossCatalogReferences(
                        references.forOwner("subclass", value.id().value()))).toList(), "subclass", name);
    }

    public List<SubclassSummaryDto> subclassesOf(String classId) {
        required(classes.findById(classId), "class", classId);
        return subclasses.findAll().stream().filter(value -> value.parentClassId().value().equals(classId))
                .map(mapper::summary).toList();
    }

    public List<ClassFeatureSummaryDto> findClassFeatures(String name, String source, String classId, Integer level) {
        return classFeatureCandidates(source).stream()
                .filter(value -> matches(value.name(), value.source().identity().code(), name, source))
                .filter(value -> classId == null || value.parentClassId().value().equals(classId))
                .filter(value -> level == null || value.level() == level)
                .sorted(Comparator.comparingInt(ClassFeatureDefinition::level)
                        .thenComparing(value -> index.order(value.id().value()),
                                Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(value -> value.id().value()))
                .map(value -> mapper.summary(value, index.order(value.id().value()))).toList();
    }

    public ClassFeatureDto classFeatureById(String id) {
        ClassFeatureDefinition value = required(classFeatures.findById(id), "class feature", id);
        return mapper.detail(value, index.order(id)).withCrossCatalogReferences(
                references.forOwner("class-feature", id));
    }

    public List<ClassFeatureDto> classFeaturesByName(String name, String source) {
        return requireMatches(classFeatures.findByName(name).stream().filter(value -> exact(value.name(), name)
                && (source == null || value.source().identity().code().equalsIgnoreCase(source)))
                .map(value -> mapper.detail(value, index.order(value.id().value())).withCrossCatalogReferences(
                        references.forOwner("class-feature", value.id().value()))).toList(), "class feature", name);
    }

    public List<SubclassFeatureSummaryDto> findSubclassFeatures(
            String name, String source, String classId, String subclassId, Integer level) {
        return subclassFeatureCandidates(source).stream()
                .filter(value -> matches(value.name(), value.source().identity().code(), name, source))
                .filter(value -> classId == null || value.parentClassId().value().equals(classId))
                .filter(value -> subclassId == null || value.parentSubclassId().value().equals(subclassId))
                .filter(value -> level == null || value.level() == level)
                .sorted(Comparator.comparingInt(SubclassFeatureDefinition::level)
                        .thenComparing(value -> index.order(value.id().value()),
                                Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(value -> value.id().value()))
                .map(value -> mapper.summary(value, index.order(value.id().value()))).toList();
    }

    public SubclassFeatureDto subclassFeatureById(String id) {
        SubclassFeatureDefinition value = required(subclassFeatures.findById(id), "subclass feature", id);
        return mapper.detail(value, index.order(id)).withCrossCatalogReferences(
                references.forOwner("subclass-feature", id));
    }

    public List<SubclassFeatureDto> subclassFeaturesByName(String name, String source) {
        return requireMatches(subclassFeatures.findByName(name).stream().filter(value -> exact(value.name(), name)
                && (source == null || value.source().identity().code().equalsIgnoreCase(source)))
                .map(value -> mapper.detail(value, index.order(value.id().value())).withCrossCatalogReferences(
                        references.forOwner("subclass-feature", value.id().value())))
                .toList(), "subclass feature", name);
    }

    public List<SubclassFeatureSummaryDto> featuresOfSubclass(String subclassId) {
        required(subclasses.findById(subclassId), "subclass", subclassId);
        return findSubclassFeatures(null, null, null, subclassId, null);
    }

    public List<ClassFeatureSummaryDto> featuresOfClass(String classId) {
        required(classes.findById(classId), "class", classId);
        return findClassFeatures(null, null, classId, null);
    }

    private static boolean matches(String actualName, String actualSource, String name, String source) {
        return (name == null || normalize(actualName).contains(normalize(name)))
                && (source == null || actualSource.equalsIgnoreCase(source));
    }

    private List<ClassDefinition> classCandidates(String source) {
        return source == null ? classes.findAll() : classes.findBySource(source);
    }

    private List<SubclassDefinition> subclassCandidates(String source) {
        return source == null ? subclasses.findAll() : subclasses.findBySource(source);
    }

    private List<ClassFeatureDefinition> classFeatureCandidates(String source) {
        return source == null ? classFeatures.findAll() : classFeatures.findBySource(source);
    }

    private List<SubclassFeatureDefinition> subclassFeatureCandidates(String source) {
        return source == null ? subclassFeatures.findAll() : subclassFeatures.findBySource(source);
    }

    private static boolean exact(String actual, String requested) {
        return normalize(actual).equals(normalize(requested));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static <T> T required(T value, String kind, String id) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No " + kind + " found with ID '" + id + "'");
        }
        return value;
    }

    private static <T> List<T> requireMatches(List<T> matches, String kind, String name) {
        if (matches.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No " + kind + " found with name '" + name + "'");
        }
        return matches;
    }
}
