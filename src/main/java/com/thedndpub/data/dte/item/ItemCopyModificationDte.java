package com.thedndpub.data.dte.item;

public record ItemCopyModificationDte(
        ItemCopyArrayModificationDte entries,
        ItemCopyArrayModificationDte property) {
}
