package com.thedndpub.data.dte.charoption;

import java.util.List;

public record CharacterCreationOptionsFileDte(List<CharacterCreationOptionDte> charoption) {
    public CharacterCreationOptionsFileDte {
        charoption = charoption == null ? null : List.copyOf(charoption);
    }
}
