package com.thedndpub.data.importer.classes;

public record ClassSourceLocation(String resource, String collection, int index) {
    public String pointer() {
        return resource + "#/" + collection + "/" + index;
    }
}
