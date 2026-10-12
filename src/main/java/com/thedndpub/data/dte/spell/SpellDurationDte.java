package com.thedndpub.data.dte.spell;

import java.util.List;

public record SpellDurationDte(String type, Boolean concentration, DurationAmountDte duration, List<String> ends) {
}
