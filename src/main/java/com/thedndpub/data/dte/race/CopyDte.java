package com.thedndpub.data.dte.race;

import lombok.Data;

@Data
public class CopyDte {
    private String name;
    private String source;
    private CopyModDte _mod;
    private PreserveDte _preserve;
}
