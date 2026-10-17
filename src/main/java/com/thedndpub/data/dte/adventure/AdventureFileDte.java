package com.thedndpub.data.dte.adventure;

import java.util.List;

public record AdventureFileDte(List<AdventureDte> adventure) {
    public AdventureFileDte {
        adventure = adventure == null ? null : List.copyOf(adventure);
    }
}
