package com.thedndpub.data.dte.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ItemsWrapperDte(
    List<ItemDte> item,
    List<ItemDte> itemGroup
){}
