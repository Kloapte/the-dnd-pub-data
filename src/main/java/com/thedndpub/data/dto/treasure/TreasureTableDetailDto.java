package com.thedndpub.data.dto.treasure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dte.treasure.DragonMundaneItemRowDte;
import com.thedndpub.data.dte.treasure.IndividualTreasureRowDte;
import com.thedndpub.data.dte.treasure.HoardTreasureRowDte;
import com.thedndpub.data.dte.treasure.DragonTreasureTypeRowDte;
import com.thedndpub.data.dte.treasure.DragonMagicItemTypeRowDte;
import com.thedndpub.data.dte.treasure.MagicItemTableRowDte;
import com.thedndpub.data.dte.treasure.NestedMagicItemRowDte;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TreasureTableDetailDto(
        TreasureTableId id,
        String name,
        SourceIdentity source,
        Integer page,
        String collectionType,
        String category,
        Integer challengeMin,
        Integer challengeMax,
        Integer denominationGp,
        String magicItemTableCode,
        int tableCount,
        int rowCount,
        List<TreasureTableStructureDto> tables,
        List<TreasureTableStructureDto> sharedTables,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public TreasureTableDetailDto {
        tables = List.copyOf(tables);
        sharedTables = List.copyOf(sharedTables);
        crossCatalogReferences = List.copyOf(crossCatalogReferences);
    }

    public static TreasureTableDetailDto from(
            TreasureTableDefinition definition,
            List<DragonMundaneItemRowDte> sharedMundaneItems,
            List<PublicCrossCatalogReferenceDto> references) {
        Integer challengeMin = null;
        Integer challengeMax = null;
        Integer denominationGp = null;
        String magicItemTableCode = null;
        List<TreasureTableStructureDto> tables = new ArrayList<>();
        List<TreasureTableStructureDto> sharedTables = List.of();

        if (definition instanceof IndividualTreasureDefinition individual) {
            challengeMin = individual.authoredData().crMin();
            challengeMax = individual.authoredData().crMax();
            tables.add(structure("coins", individual.authoredData().table(), IndividualTreasureRowDte::min,
                    IndividualTreasureRowDte::max));
        } else if (definition instanceof TreasureHoardDefinition hoard) {
            challengeMin = hoard.authoredData().crMin();
            challengeMax = hoard.authoredData().crMax();
            tables.add(structure("hoard", hoard.authoredData().table(), HoardTreasureRowDte::min,
                    HoardTreasureRowDte::max));
        } else if (definition instanceof DragonTreasureDefinition dragon) {
            tables.add(structure("gems.typeTable", dragon.authoredData().gems().typeTable(),
                    DragonTreasureTypeRowDte::min, DragonTreasureTypeRowDte::max));
            tables.add(structure("artObjects.typeTable", dragon.authoredData().artObjects().typeTable(),
                    DragonTreasureTypeRowDte::min, DragonTreasureTypeRowDte::max));
            for (int index = 0; index < dragon.authoredData().magicItems().size(); index++) {
                var group = dragon.authoredData().magicItems().get(index);
                tables.add(structure("magicItems[" + index + "].typeTable", group.typeTable(),
                        DragonMagicItemTypeRowDte::min, DragonMagicItemTypeRowDte::max));
            }
            sharedTables = List.of(structure("dragonMundaneItems", sharedMundaneItems,
                    DragonMundaneItemRowDte::min, DragonMundaneItemRowDte::max));
        } else if (definition instanceof GemTableDefinition gems) {
            denominationGp = gems.authoredData().type();
            tables.add(new TreasureTableStructureDto("gems", gems.authoredData().table().size(), List.of(), List.of()));
        } else if (definition instanceof ArtObjectTableDefinition artObjects) {
            denominationGp = artObjects.authoredData().type();
            tables.add(new TreasureTableStructureDto(
                    "artObjects", artObjects.authoredData().table().size(), List.of(), List.of()));
        } else if (definition instanceof MagicItemTableDefinition magicItems) {
            magicItemTableCode = magicItems.authoredData().type();
            List<TreasureTableStructureDto> nested = new ArrayList<>();
            for (int index = 0; index < magicItems.authoredData().table().size(); index++) {
                MagicItemTableRowDte row = magicItems.authoredData().table().get(index);
                if (row.table() != null) {
                    nested.add(structure("nestedResults[" + index + "]", row.table(),
                            NestedMagicItemRowDte::min, NestedMagicItemRowDte::max));
                }
            }
            tables.add(new TreasureTableStructureDto("magicItems", magicItems.authoredData().table().size(),
                    magicItems.authoredData().table().stream()
                            .map(row -> new TreasureRollRangeDto(row.min(), row.max())).toList(), nested));
        } else {
            throw new IllegalArgumentException("Unsupported treasure table definition: " + definition.getClass());
        }

        return new TreasureTableDetailDto(definition.id(), definition.name(), definition.source().identity(),
                definition.source().page(), definition.kind().sourceCollection(), definition.category(), challengeMin,
                challengeMax, denominationGp, magicItemTableCode, definition.tableCount(), definition.rowCount(),
                tables, sharedTables, references);
    }

    private static <T> TreasureTableStructureDto structure(
            String role,
            List<T> rows,
            java.util.function.Function<T, Integer> minimum,
            java.util.function.Function<T, Integer> maximum) {
        List<TreasureRollRangeDto> ranges = rows.stream()
                .map(row -> new TreasureRollRangeDto(minimum.apply(row), maximum.apply(row)))
                .toList();
        return new TreasureTableStructureDto(role, rows.size(), ranges, List.of());
    }
}
