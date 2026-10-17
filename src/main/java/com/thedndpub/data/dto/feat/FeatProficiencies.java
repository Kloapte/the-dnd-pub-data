package com.thedndpub.data.dto.feat;

import com.thedndpub.data.dto.race.RaceGrantGroupDto;

import java.util.List;

/** Ordered authored grant groups; no choices have been selected or awarded. */
public record FeatProficiencies(
        List<RaceGrantGroupDto> skills,
        List<RaceGrantGroupDto> expertise,
        List<RaceGrantGroupDto> languages,
        List<RaceGrantGroupDto> tools,
        List<RaceGrantGroupDto> weapons,
        List<RaceGrantGroupDto> armor,
        List<RaceGrantGroupDto> savingThrows,
        List<FeatCombinedGrant> combined) {
    public FeatProficiencies {
        skills = copy(skills);
        expertise = copy(expertise);
        languages = copy(languages);
        tools = copy(tools);
        weapons = copy(weapons);
        armor = copy(armor);
        savingThrows = copy(savingThrows);
        combined = copy(combined);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
