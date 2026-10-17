package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dto.monster.syntax.MonsterSyntaxAnnotation;
import com.thedndpub.data.dto.monster.syntax.MonsterSyntaxAnnotationIndex;
import com.thedndpub.data.importer.monster.MonsterSyntaxAnnotationAdapter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/** Startup-loaded internal syntax observations for authored Monster source roots. */
@Component
public final class MonsterSyntaxAnnotationCatalog {
    private final MonsterSyntaxAnnotationIndex index;

    public MonsterSyntaxAnnotationCatalog(ObjectMapper objectMapper, MonsterCatalog monsterCatalog) {
        Objects.requireNonNull(objectMapper, "objectMapper");
        Objects.requireNonNull(monsterCatalog, "monsterCatalog");
        MonsterSyntaxAnnotationAdapter adapter = new MonsterSyntaxAnnotationAdapter(objectMapper);
        index = adapter.adapt(
                List.copyOf(monsterCatalog.monstersById().values()),
                (name, source) -> {
                    var monster = monsterCatalog.findByNameAndSource(name, source);
                    return monster == null ? null : monster.identity().id();
                });
    }

    public List<MonsterSyntaxAnnotation> findByAuthoredMonsterId(MonsterId monsterId) {
        return index.findByAuthoredMonsterId(monsterId);
    }

    public MonsterSyntaxAnnotationIndex index() {
        return index;
    }
}
