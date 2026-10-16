package com.thedndpub.data.services;

import com.thedndpub.data.dto.classes.ClassDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class ClassCatalog {
    private final ClassDomainIndex index;
    public ClassCatalog(ClassDomainIndex index) {
        this.index = index;
    }

    public List<ClassDefinition> findAll() {
        return index.classes();
    }

    public ClassDefinition findById(String id) {
        return index.classById(id);
    }

    public List<ClassDefinition> findByName(String name) {
        return index.classesNamed(name);
    }

    public List<ClassDefinition> findBySource(String source) {
        return index.classesFromSource(source);
    }

    public int size() {
        return index.classes().size();
    }
}
