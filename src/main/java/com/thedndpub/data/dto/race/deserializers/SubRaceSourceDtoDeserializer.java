package com.thedndpub.data.dto.race.deserializers;

import com.thedndpub.data.dto.race.SourceDto;
import com.thedndpub.data.dto.race.SubraceSourceDto;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;

public class SubRaceSourceDtoDeserializer extends ValueDeserializer<List<SubraceSourceDto>> {

    @Override
    public List<SubraceSourceDto> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

            List<SubraceSourceDto> subraces = new ArrayList<>();
            for(JsonNode subNode : node) {
                try {
                    SubraceSourceDto subraceSourceDto = new SubraceSourceDto(subNode.get("name").asString(), context.readTreeAsValue(subNode, SourceDto.class));

//                    subraces.add(context.readTreeAsValue(subNode, SubraceSourceDto.class));
                    subraces.add(subraceSourceDto);
                }
                catch (Exception e) {
                    throw context.weirdStringException(
                            subNode.toString(),
                            SubraceSourceDto.class,
                            "Failed to parse subraceSource"
                    );
                }
            }
            return subraces;
    }
}