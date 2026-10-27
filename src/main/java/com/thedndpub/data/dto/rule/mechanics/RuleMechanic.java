package com.thedndpub.data.dto.rule.mechanics;

import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

public interface RuleMechanic {
    RuleMechanicKind kind();

    String subtype();
}
