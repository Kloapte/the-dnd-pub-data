package com.thedndpub.data.dte.race.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.generic.ChooseDte;
import com.thedndpub.data.dte.race.SpellAbilityDte;

import java.util.ArrayList;

public class SpellAbilityDeserializer extends ValueDeserializer<SpellAbilityDte> {
    @Override
    public SpellAbilityDte deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        SpellAbilityDte dte = new SpellAbilityDte();

        if(node != null && node.isTextual()) {
            dte.setAbility(node.asText());
        }
        else if(node != null && node.isObject()) {
            ChooseDte chooseDte = new ChooseDte();
            chooseDte.setFrom(new ArrayList<>());
            for(JsonNode item : node.get("choose")) {
                chooseDte.getFrom().add(item.asText());
            }
            dte.setChoose(chooseDte);
        }

        return dte;
    }
}
