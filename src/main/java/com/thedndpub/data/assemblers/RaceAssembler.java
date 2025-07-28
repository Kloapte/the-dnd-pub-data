package com.thedndpub.data.assemblers;

import com.thedndpub.data.dte.race.RaceDte;
import com.thedndpub.data.dto.RaceDto;
import com.thedndpub.data.dto.SourceDto;
import org.springframework.stereotype.Component;

@Component
public class RaceAssembler {
    public RaceDto mapRaceDteToDto(RaceDte dte) {
        RaceDto dto = new RaceDto();
        dto.setName(dte.getName());
        dto.setSource(this.mapSource(dte.getSource()));

        return dto;
    }

    private SourceDto mapSource(String dte) {
        return SourceDto.fromString(dte);
    }
}
