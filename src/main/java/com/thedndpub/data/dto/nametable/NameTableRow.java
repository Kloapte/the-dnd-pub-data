package com.thedndpub.data.dto.nametable;

import java.util.Objects;

/** Source-authored roll interval and result, retained internally and never exposed through the public API. */
public record NameTableRow(int min, int max, String result) {
    public NameTableRow {
        Objects.requireNonNull(result, "result");
    }
}
