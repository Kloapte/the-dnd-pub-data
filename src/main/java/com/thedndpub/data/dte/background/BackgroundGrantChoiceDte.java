package com.thedndpub.data.dte.background;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundGrantChoiceDte(List<String> from, Integer count, List<String> categories, String fromFilter) {
    public BackgroundGrantChoiceDte {
        from = from == null ? List.of() : List.copyOf(from);
        categories = categories == null ? List.of() : List.copyOf(categories);
    }

    public BackgroundGrantChoiceDte(List<String> from, Integer count, List<String> categories) {
        this(from, count, categories, null);
    }
}
