package com.thedndpub.data.dte.treasure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = MagicItemChooseDeserializer.class)
@JsonSerialize(using = MagicItemChooseSerializer.class)
public sealed interface MagicItemChooseDte permits FromItemsChoiceDte, FromGenericChoiceDte,
        FromGroupChoiceDte, FromMatchingChoiceDte {
}
