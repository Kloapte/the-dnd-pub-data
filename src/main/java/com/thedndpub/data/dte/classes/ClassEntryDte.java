package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/** Class-specific rich content; never a public API representation. */
@JsonDeserialize(using = ClassEntryDeserializer.class)
public sealed interface ClassEntryDte permits ClassTextEntryDte, ClassBlockEntryDte,
        ClassReferenceEntryDte, ClassOptionsEntryDte, ClassAbilityEntryDte,
        ClassStatblockEntryDte, ClassTableEntryDte {
}
