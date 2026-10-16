package com.thedndpub.data.dte.classes;

public record ClassCopyPreserveDte(
        Boolean page,
        Boolean otherSources,
        Boolean referenceSources,
        Boolean srd,
        Boolean basicRules,
        Boolean reprintedAs) {
}
