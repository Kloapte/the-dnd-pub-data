package com.thedndpub.data.dto.treasure;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.treasure.ArtObjectTableDte;
import com.thedndpub.data.dte.treasure.DragonTreasureDte;
import com.thedndpub.data.dte.treasure.GemTableDte;
import com.thedndpub.data.dte.treasure.IndividualTreasureDte;
import com.thedndpub.data.dte.treasure.MagicItemTableDte;
import com.thedndpub.data.dte.treasure.TreasureHoardDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public final class TreasureTableMapper {
    public IndividualTreasureDefinition map(IndividualTreasureDte source) {
        requireDefinition(source.name(), source.source(), source.table(), "individual");
        return new IndividualTreasureDefinition(id(source.source(), TreasureTableKind.INDIVIDUAL, source.name()),
                source.name(), new SourceReference(source.source(), source.page()), source);
    }

    public TreasureHoardDefinition map(TreasureHoardDte source) {
        requireDefinition(source.name(), source.source(), source.table(), "hoard");
        return new TreasureHoardDefinition(id(source.source(), TreasureTableKind.HOARD, source.name()),
                source.name(), new SourceReference(source.source(), source.page()), source);
    }

    public DragonTreasureDefinition map(DragonTreasureDte source, int sharedMundaneItemRowCount) {
        requireText(source.name(), "dragon.name");
        requireText(source.source(), "dragon.source");
        if (source.coins() == null || source.dragonMundaneItems() == null || source.gems() == null
                || source.artObjects() == null || source.magicItems() == null || source.magicItems().isEmpty()) {
            throw new IllegalArgumentException("Dragon treasure definition is missing an authored component");
        }
        if (source.gems().typeTable() == null || source.gems().typeTable().isEmpty()
                || source.artObjects().typeTable() == null || source.artObjects().typeTable().isEmpty()) {
            throw new IllegalArgumentException("Dragon treasure type tables must contain rows");
        }
        for (var group : source.magicItems()) {
            if (group.typeTable() == null || group.typeTable().isEmpty()) {
                throw new IllegalArgumentException("Dragon magic item type table must contain rows");
            }
        }
        return new DragonTreasureDefinition(id(source.source(), TreasureTableKind.DRAGON, source.name()),
                source.name(), new SourceReference(source.source(), source.page()), source,
                sharedMundaneItemRowCount);
    }

    public GemTableDefinition map(GemTableDte source) {
        requireDefinition(source.name(), source.source(), source.table(), "gems");
        return new GemTableDefinition(id(source.source(), TreasureTableKind.GEMS, source.name()),
                source.name(), new SourceReference(source.source(), source.page()), source);
    }

    public ArtObjectTableDefinition map(ArtObjectTableDte source) {
        requireDefinition(source.name(), source.source(), source.table(), "artObjects");
        return new ArtObjectTableDefinition(id(source.source(), TreasureTableKind.ART_OBJECTS, source.name()),
                source.name(), new SourceReference(source.source(), source.page()), source);
    }

    public MagicItemTableDefinition map(MagicItemTableDte source) {
        requireDefinition(source.name(), source.source(), source.table(), "magicItems");
        return new MagicItemTableDefinition(id(source.source(), TreasureTableKind.MAGIC_ITEMS, source.name()),
                source.name(), new SourceReference(source.source(), source.page()), source);
    }

    private static void requireDefinition(String name, String source, List<?> rows, String collection) {
        requireText(name, collection + ".name");
        requireText(source, collection + ".source");
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException(collection + " definition must contain authored rows");
        }
    }

    private static TreasureTableId id(String source, TreasureTableKind kind, String name) {
        return new TreasureTableId("treasure-table:" + source + ":" + kind.sourceCollection() + ":" + slug(name));
    }

    private static String slug(String value) {
        String slug = Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Treasure table name cannot produce a stable ID: " + value);
        }
        return slug;
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
