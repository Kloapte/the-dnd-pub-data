package com.thedndpub.data.dte.item;

import java.util.List;
import java.util.Map;

public record ItemAttachedSpellsDte(
        List<String> direct,
        String ability,
        ItemSpellGrantDte will,
        ItemSpellGrantDte daily,
        ItemSpellGrantDte rest,
        ItemSpellGrantDte charges,
        ItemSpellGrantDte limited,
        List<String> other,
        List<String> ritual) {
}
