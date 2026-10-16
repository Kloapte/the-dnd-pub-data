package com.thedndpub.data.dte.item;

import java.util.List;
import java.util.Map;

public record ItemSpellGrantDte(List<String> direct, Map<String, List<String>> byUses) {
}
