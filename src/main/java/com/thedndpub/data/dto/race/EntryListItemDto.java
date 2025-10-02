package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntryListItemDto {
    private String title;
    private String text;
}
