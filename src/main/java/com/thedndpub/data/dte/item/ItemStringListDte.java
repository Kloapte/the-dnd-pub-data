package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = ItemStringListDeserializer.class)
public record ItemStringListDte(List<String> values, Boolean enabled) {
}
