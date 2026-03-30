package com.thedndpub.data.dte.items.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.generic.AbilityDte;
import com.thedndpub.data.dte.generic.ChooseDte;

import java.io.IOException;

public class ItemAbilityDeserializer extends JsonDeserializer<AbilityDte> {
    @Override
    public AbilityDte deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if (node.get("from") != null) {
            //This is needed because 2 items have choose as main object wrapper for some reason...
            AbilityDte dte = new AbilityDte();
            dte.setChoose(mapper.convertValue(node, ChooseDte.class));
            return dte;
        }
        else {
            return mapper.convertValue(node, AbilityDte.class);
        }
    }
}