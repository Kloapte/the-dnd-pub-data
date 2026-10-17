package com.thedndpub.data.dte.common.entry;

/** Typed source link target used by link and image entry nodes. */
public record EntryHrefDte(String type, String path, String hash) {
}
