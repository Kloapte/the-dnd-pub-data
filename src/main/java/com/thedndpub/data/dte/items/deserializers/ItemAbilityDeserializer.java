package com.thedndpub.data.dte.items.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.generic.AbilityDte;
import com.thedndpub.data.dte.generic.ChooseDte;

public class ItemAbilityDeserializer extends ValueDeserializer<AbilityDte> {
    @Override
    public AbilityDte deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if (node.get("from") != null) {
            //This is needed because 2 items have choose as main object wrapper for some reason...
            AbilityDte dte = new AbilityDte();
            dte.setChoose(context.readTreeAsValue(node, ChooseDte.class));
            return dte;
        }
        else {
            return context.readTreeAsValue(node, AbilityDte.class);
        }
    }
}