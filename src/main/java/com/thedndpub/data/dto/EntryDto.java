package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, // use a field that already exists
        property = "type", // use the existing 'type' property in your JSON
        visible = true
)
@JsonSubTypes({
//        @JsonSubTypes.Type(value = Entr.class, name = "item"),
        @JsonSubTypes.Type(value = EntryTextDto.class, name = "text"),
        @JsonSubTypes.Type(value = EntryWithTableDto.class, name = "table"),
        @JsonSubTypes.Type(value = EntryWithSubDto.class, name = "entries")
})
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class EntryDto {
    private String name;
    private String type;
}
