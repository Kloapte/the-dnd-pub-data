package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(using = ClassTableCellDeserializer.class)
public record ClassTableCellDte(
        String text,
        Integer number,
        String type,
        Integer value,
        List<ClassHitDiceDte> toRoll,
        Boolean rollable) {
    public ClassTableCellDte {
        toRoll = ClassDteCollections.list(toRoll);
    }
}
