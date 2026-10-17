package com.thedndpub.data.dte.object;

import java.util.List;

public record ObjectFileDte(List<ObjectSourceDte> object) {
    public ObjectFileDte {
        object = object == null ? null : List.copyOf(object);
    }
}
