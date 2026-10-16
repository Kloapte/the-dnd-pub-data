package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FeatVersionModDte(@JsonProperty("entries") List<FeatEntryModificationDte> entries) {
}
