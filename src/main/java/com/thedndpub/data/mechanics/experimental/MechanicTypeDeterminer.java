package com.thedndpub.data.mechanics.experimental;

import java.util.ArrayList;
import java.util.List;

public final class MechanicTypeDeterminer {
    public List<MechanicCandidate> determine(List<SourceFragment> fragments) {
        List<MechanicCandidate> result = new ArrayList<>();
        for (SourceFragment fragment : fragments) {
            for (MechanicType type : fragment.signals()) {
                result.add(new MechanicCandidate(type, fragment.id(), "generic-source-signals", "1",
                        List.of(fragment.kind().name(), fragment.pointer()), String.join(" > ", fragment.hierarchy()),
                        fragment.kind() == SourceFragment.Kind.STRUCTURED_FIELD));
            }
        }
        return result.stream().sorted(java.util.Comparator.comparing(MechanicCandidate::fragmentId)
                .thenComparing(candidate -> candidate.type().name())).toList();
    }
}
