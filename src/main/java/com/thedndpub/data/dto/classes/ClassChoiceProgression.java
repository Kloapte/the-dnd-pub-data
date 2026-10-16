package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassChoiceProgression(
        ClassExternalKind targetKind,
        String name,
        List<String> categories,
        List<ClassLevelCount> milestones,
        List<ClassRequiredOption> required) {
    public ClassChoiceProgression {
        categories = categories == null ? List.of() : List.copyOf(categories);
        milestones = milestones == null ? List.of() : List.copyOf(milestones);
        required = required == null ? List.of() : List.copyOf(required);
    }
}
