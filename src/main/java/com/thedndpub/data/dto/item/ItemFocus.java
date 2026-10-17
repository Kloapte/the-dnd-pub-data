package com.thedndpub.data.dto.item;

/** Authored focus information, without interpreting class eligibility. */
public sealed interface ItemFocus permits ItemBooleanFocus, ItemClassFocus {
}
