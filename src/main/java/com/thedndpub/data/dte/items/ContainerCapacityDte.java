package com.thedndpub.data.dte.items;

import lombok.Data;

import java.util.List;

@Data
public class ContainerCapacityDte {
    private List<Integer> weight;
    private List<Integer> volume;
    private Boolean weightless;
    private List<ContainerCapacityItemDte> item;
}
