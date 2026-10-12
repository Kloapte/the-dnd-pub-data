package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterFeatureItemsDeserializer.class)
public sealed interface MonsterFeatureItemsDte permits MonsterFeatureItemDte, MonsterFeatureItemListDte {
}
