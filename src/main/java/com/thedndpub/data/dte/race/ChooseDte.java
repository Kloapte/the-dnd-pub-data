package com.thedndpub.data.dte.race;

import lombok.Data;

import java.util.List;

@Data
public class ChooseDte {
    private List<String> from;
    private String fromFilter;
    private List<String> category;
    private int count;
    private int amount;
}
