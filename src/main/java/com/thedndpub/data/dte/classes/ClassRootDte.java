package com.thedndpub.data.dte.classes;

/** Source-authored roots; the collection, not a rendering type, determines their kind. */
public sealed interface ClassRootDte permits ClassDte, SubclassDte, ClassFeatureDte, SubclassFeatureDte {
    String name();

    String source();

    Integer page();
}
