package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dto.monster.identity.Reference;
import com.thedndpub.data.dto.monster.support.LegendaryGroupAssociation;
import com.thedndpub.data.dto.monster.support.LegendaryGroupAssociationStatus;
import com.thedndpub.data.dto.monster.support.LegendaryGroupDefinition;
import com.thedndpub.data.dto.monster.support.LegendaryGroupId;
import com.thedndpub.data.dto.monster.support.LegendaryGroupInputs;
import com.thedndpub.data.importer.monster.LegendaryGroupResolver;
import com.thedndpub.data.importer.monster.LegendaryGroupSourceLoader;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/** Internal support registry. It never modifies, enriches or serializes a Monster definition. */
@Component
public final class LegendaryGroupSupportCatalog {
    private final LegendaryGroupInputs authoredInputs;
    private final Map<LegendaryGroupId, LegendaryGroupDefinition> definitions;
    private final Map<String, LegendaryGroupAssociation> associations;

    public LegendaryGroupSupportCatalog(
            ObjectMapper mapper,
            SourceRegistry sourceRegistry,
            MonsterCatalog monsterCatalog) {
        authoredInputs = new LegendaryGroupSourceLoader(mapper, sourceRegistry).loadResources();
        definitions = new LegendaryGroupResolver().resolve(authoredInputs);
        if (definitions.isEmpty()) {
            throw new IllegalStateException("Legendary-group support catalog is empty");
        }
        Map<String, LegendaryGroupAssociation> matches = new TreeMap<>();
        for (var monster : monsterCatalog.monstersById().values()) {
            Reference reference = monster.identity().legendaryGroup();
            if (reference != null) {
                matches.put(monster.identity().id().value(), associate(monster.identity().id(), reference));
            }
        }
        associations = Collections.unmodifiableMap(matches);
    }

    public LegendaryGroupAssociation associate(MonsterId monsterId, Reference reference) {
        Objects.requireNonNull(reference, "reference");
        var requested = LegendaryGroupId.from(reference.name(), reference.source());
        var target = definitions.get(requested);
        return new LegendaryGroupAssociation(
                monsterId, requested,
                target == null ? LegendaryGroupAssociationStatus.UNRESOLVED : LegendaryGroupAssociationStatus.RESOLVED,
                target == null ? null : target.id());
    }

    public LegendaryGroupInputs authoredInputs() {
        return authoredInputs;
    }

    public Map<LegendaryGroupId, LegendaryGroupDefinition> definitionsById() {
        return definitions;
    }

    public List<LegendaryGroupDefinition> definitions() {
        return List.copyOf(definitions.values());
    }

    public LegendaryGroupDefinition findByNameAndSource(String name, String source) {
        return definitions.get(LegendaryGroupId.from(name, source));
    }

    public Map<String, LegendaryGroupAssociation> associations() {
        return associations;
    }
}
