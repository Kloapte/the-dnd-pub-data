package com.thedndpub.data.dte.race;

import com.thedndpub.data.dte.generic.ChooseDte;
import lombok.Data;

import java.util.List;

@Data
public class ResistDte {
    private List<String> resist;
    private ChooseDte choose;
}
