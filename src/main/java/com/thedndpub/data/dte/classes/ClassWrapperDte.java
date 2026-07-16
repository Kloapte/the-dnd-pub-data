package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ClassWrapperDte {
    private Object _meta;
    @JsonProperty("class")
    private List<ClassDte> classes;
    private List<Object> subclass;
    private List<Object> classFeature;
    private List<Object> subclassFeature;
}
