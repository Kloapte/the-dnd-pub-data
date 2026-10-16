package com.thedndpub.data.dte.sense;

import java.util.List;

public record SensesFileDte(List<SenseDte> sense) {
    public SensesFileDte {
        sense = sense == null ? null : List.copyOf(sense);
    }
}
