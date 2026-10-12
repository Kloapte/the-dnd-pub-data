package com.thedndpub.data.dte.monsters.deserializers;

import com.thedndpub.data.dte.monsters.DamageTraitDte;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;

public abstract class DamageTraitDeserializer extends ValueDeserializer<List<DamageTraitDte>>{
    private final String nestedField;

    protected DamageTraitDeserializer(String nestedField) {
        this.nestedField = nestedField;
    }

    @Override
    public List<DamageTraitDte> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = parser.readValueAsTree();

        List<DamageTraitDte> result = new ArrayList<>();

        for (JsonNode traitNode : node) {

            if (traitNode.isString()) {
                result.add(new DamageTraitDte(
                        traitNode.asString(),
                        null
                ));
                continue;
            }

            if (traitNode.isObject()) {
                String note = traitNode.has("note")
                        ? traitNode.get("note").asString()
                        : null;

                if(note == null) {
                    note = traitNode.has("preNote")
                            ? traitNode.get("preNote").asString()
                            : null;
                }

                JsonNode subTraits = traitNode.get(this.nestedField);

                if (subTraits != null && subTraits.isArray()) {
                    for (JsonNode subTrait : subTraits) {
                        if(subTrait.isString()) {
                            result.add(new DamageTraitDte(
                                    subTrait.asString(),
                                    note
                            ));
                        }
                        else if(subTrait.isObject()) {
                            note += " - " + subTrait.get("note").asString();
                            for(JsonNode subResistance : subTrait.get(this.nestedField)) {
                                result.add(new DamageTraitDte(subResistance.asString(), note));
                            }
                        }
                        else {
                            throw context.weirdStringException(
                                    node.toString(),
                                    DamageTraitDte.class,
                                    "Unexpected nested " + this.nestedField + " object"
                            );
                        }
                    }
                }
            }
        }

        return result;
    }
}