package com.thedndpub.data.dto.search;

public record SearchRequest(boolean races, boolean spells, boolean classes, boolean items, boolean backgrounds, String query, SearchSourceType source, boolean exact) {

}
