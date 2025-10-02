package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.race.LanguageProficiencyDto;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntryMapped {
    private boolean decoded;
    private String name;
    private boolean powerfulBuild;
    private List<Placeholder> placeholders;
    private boolean proficiency;
    private String text;
    private Integer darkvision;
    private List<String> allignments;
    private LanguageProficiencyDto languages;
}
