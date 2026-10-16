package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = FeatModificationItemsDeserializer.class)
public sealed interface FeatModificationItemsDte permits FeatModificationTextItemsDte, FeatModificationNamedItemsDte {
}
