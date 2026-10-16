package com.thedndpub.data.dte.optionalfeature;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record OptionalFeatureSpellSetDte(@JsonProperty("_") List<OptionalFeatureChoiceDte> values) {
}
