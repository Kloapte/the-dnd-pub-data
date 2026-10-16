package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.classes.ClassNormalizationResult;
import com.thedndpub.data.importer.classes.ClassDefinitionNormalizer;
import com.thedndpub.data.importer.classes.ClassExternalReferenceResolver;
import com.thedndpub.data.importer.classes.ClassesSourceLoader;
import org.springframework.stereotype.Component;

/** Loads and normalizes the complete class corpus exactly once for all four catalogs. */
@Component
public final class ClassDomainSnapshot {
    private final ClassNormalizationResult result;

    public ClassDomainSnapshot(ObjectMapper mapper, SourceRegistry registry, ItemCatalog items,
            OptionalFeatureCatalog optionalFeatures, FeatCatalog feats, MonsterCatalog monsters) {
        try {
            var sources = new ClassesSourceLoader(mapper).load();
            var external = ClassExternalReferenceResolver.fromExistingCatalogs(
                    mapper, registry, items, optionalFeatures, feats, monsters);
            result = new ClassDefinitionNormalizer(registry, external).normalize(sources);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not initialize class domain snapshot", exception);
        }
    }

    public ClassNormalizationResult result() {
        return result;
    }
}
