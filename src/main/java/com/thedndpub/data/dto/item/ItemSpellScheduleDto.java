package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemSpellScheduleDto(
        List<String> direct,
        String ability,
        ItemSpellGrantDto will,
        ItemSpellGrantDto daily,
        ItemSpellGrantDto rest,
        ItemSpellGrantDto charges,
        ItemSpellGrantDto limited,
        List<String> other,
        List<String> ritual) {
    public ItemSpellScheduleDto {
        direct = List.copyOf(direct);
        other = List.copyOf(other);
        ritual = List.copyOf(ritual);
    }

}
