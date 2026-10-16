package com.thedndpub.data.services;

import com.thedndpub.data.dto.classes.SubclassFeatureDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class SubclassFeatureCatalog {
    private final ClassDomainIndex index;
    public SubclassFeatureCatalog(ClassDomainIndex index) {
        this.index = index;
    }

    public List<SubclassFeatureDefinition> findAll() {
        return index.subclassFeatures();
    }

    public SubclassFeatureDefinition findById(String id) {
        return index.subclassFeatureById(id);
    }

    public List<SubclassFeatureDefinition> findByName(String name) {
        return index.subclassFeaturesNamed(name);
    }

    public List<SubclassFeatureDefinition> findBySource(String source) {
        return index.subclassFeaturesFromSource(source);
    }

    public int size() {
        return index.subclassFeatures().size();
    }
}
