package com.thedndpub.data.dto.feat;

import com.thedndpub.data.dto.race.RaceGrantChoiceDto;

import java.util.List;

public record FeatCombinedGrant(List<RaceGrantChoiceDto> choose) {
    public FeatCombinedGrant {
        choose = choose == null ? null : List.copyOf(choose);
    }
}
