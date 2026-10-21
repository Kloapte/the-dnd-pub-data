package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Source-named delivery alternatives; a null range means the source does not give one here. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EnchantedObjectDelivery(List<EnchantedObjectDeliveryMode> modes, Integer thrownRangeFeet) {
    public EnchantedObjectDelivery {
        modes = ModelChecks.list(modes);
        ModelChecks.require(modes.equals(List.of(EnchantedObjectDeliveryMode.THROW,
                        EnchantedObjectDeliveryMode.SLING)),
                "Magic Stone supports throwing or hurling a stone with a sling");
        ModelChecks.require(thrownRangeFeet != null && thrownRangeFeet == 60,
                "The source specifies a 60-foot range for a thrown pebble");
    }
}
