package com.thedndpub.data.services;

import com.thedndpub.data.dto.classes.ClassFeatureDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class ClassFeatureCatalog {
    private final ClassDomainIndex index;
    public ClassFeatureCatalog(ClassDomainIndex index) {
        this.index = index;
    }

    public List<ClassFeatureDefinition> findAll() {
        return index.classFeatures();
    }

    public ClassFeatureDefinition findById(String id) {
        return index.classFeatureById(id);
    }

    public List<ClassFeatureDefinition> findByName(String name) {
        return index.classFeaturesNamed(name);
    }

    public List<ClassFeatureDefinition> findBySource(String source) {
        return index.classFeaturesFromSource(source);
    }

    public int size() {
        return index.classFeatures().size();
    }
}
