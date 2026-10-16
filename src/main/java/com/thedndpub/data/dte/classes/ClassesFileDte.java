package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ClassesFileDte(
        @JsonProperty("_meta") ClassesMetadataDte metadata,
        @JsonProperty("class") List<ClassDte> classes,
        List<SubclassDte> subclass,
        List<ClassFeatureDte> classFeature,
        List<SubclassFeatureDte> subclassFeature) {
    public ClassesFileDte {
        classes = ClassDteCollections.list(classes);
        subclass = ClassDteCollections.list(subclass);
        classFeature = ClassDteCollections.list(classFeature);
        subclassFeature = ClassDteCollections.list(subclassFeature);
    }
}
