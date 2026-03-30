package com.thedndpub.data.dte.items;

import lombok.Data;

@Data
public class ItemAttachedSpellDte {
    private String name;
    private int charges;
    private int limitedUses;
    private Boolean daily; //Long rest
    private Boolean rest; //Short or long rest
    private Boolean atWill;
    private Boolean ritual;
    private Boolean other;
    private String ability;
}
