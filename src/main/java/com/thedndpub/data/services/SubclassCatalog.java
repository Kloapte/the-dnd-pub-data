package com.thedndpub.data.services;

import com.thedndpub.data.dto.classes.SubclassDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class SubclassCatalog {
    private final ClassDomainIndex index;
    public SubclassCatalog(ClassDomainIndex index) {
        this.index = index;
    }

    public List<SubclassDefinition> findAll() {
        return index.subclasses();
    }

    public SubclassDefinition findById(String id) {
        return index.subclassById(id);
    }

    public List<SubclassDefinition> findByName(String name) {
        return index.subclassesNamed(name);
    }

    public List<SubclassDefinition> findBySource(String source) {
        return index.subclassesFromSource(source);
    }

    public int size() {
        return index.subclasses().size();
    }
}
