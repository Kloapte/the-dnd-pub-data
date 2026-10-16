package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ClassSpellSelectionDeserializer.class)
public record ClassSpellSelectionDte(
        String spell,
        ClassSpellChoiceDte choose,
        String all,
        Integer count) {
}
