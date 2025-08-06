package com.thedndpub.data.dte.race.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.ChooseDte;
import com.thedndpub.data.dte.race.LevelSpellKnownDte;
import com.thedndpub.data.dte.race.SpellAbilityDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SpellAbilityDeserializer extends JsonDeserializer<SpellAbilityDte> {
    @Override
    public SpellAbilityDte deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

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
