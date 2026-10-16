package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(using = ClassSpellChoiceDeserializer.class)
public record ClassSpellChoiceDte(
        String filter,
        List<String> from,
        Integer count) {
    public ClassSpellChoiceDte {
        from = ClassDteCollections.list(from);
    }
}
