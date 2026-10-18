package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/** Retains whether the source Item supplied a recharge field, including explicit null. */
@JsonDeserialize(using = ItemRechargeDeserializer.class)
public record ItemRechargeDte(String value, boolean present) {
}
