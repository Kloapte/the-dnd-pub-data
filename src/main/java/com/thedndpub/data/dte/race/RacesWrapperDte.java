package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RacesWrapperDte {
    private List<RaceDte> race;
    private List<SubraceDte> subrace;
    //TODO subraces
}
