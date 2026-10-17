package com.thedndpub.data.dto.item;

import java.util.List;

public record ItemClassFocus(List<String> classLabels) implements ItemFocus {
    public ItemClassFocus {
        classLabels = List.copyOf(classLabels);
    }
}
