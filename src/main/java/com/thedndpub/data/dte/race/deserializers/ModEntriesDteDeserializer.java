package com.thedndpub.data.dte.race.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.CopyModEntryDte;
import com.thedndpub.data.dte.race.LevelSpellKnownDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModEntriesDteDeserializer extends JsonDeserializer<List<CopyModEntryDte>> {
    @Override
    public List<CopyModEntryDte> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        List<CopyModEntryDte> dteList = new ArrayList<>();

        if (node.isArray()) {
            dteList = mapper.convertValue(node, new TypeReference<ArrayList<CopyModEntryDte>>() {
            });
        } else {
            CopyModEntryDte dte = mapper.convertValue(node, CopyModEntryDte.class);
            dteList.add(dte);
        }

        return dteList;
    }
}
