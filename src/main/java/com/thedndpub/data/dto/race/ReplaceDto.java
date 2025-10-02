package com.thedndpub.data.dto.race;

import lombok.Data;

import java.util.List;

@Data
public class ReplaceDto {
    private String replaceEntry;
    //Only ever seems to one sub entry too
    private String replaceSubEntry;
    private List<EntryDto> replaceWith;
}
