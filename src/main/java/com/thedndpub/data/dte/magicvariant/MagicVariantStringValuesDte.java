package com.thedndpub.data.dte.magicvariant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = MagicVariantStringValuesDeserializer.class)
public record MagicVariantStringValuesDte(List<String> values) {
    public MagicVariantStringValuesDte {
        values = List.copyOf(values);
    }
}
