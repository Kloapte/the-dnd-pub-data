package com.thedndpub.data.dte.skill;

import java.util.List;

public record SkillsFileDte(List<SkillDte> skill) {
    public SkillsFileDte {
        skill = skill == null ? null : List.copyOf(skill);
    }
}
