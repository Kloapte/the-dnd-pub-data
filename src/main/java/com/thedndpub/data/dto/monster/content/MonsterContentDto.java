package com.thedndpub.data.dto.monster.content;

public sealed interface MonsterContentDto permits PlainText, Section, ListContent, ListItem,
        TableContent, QuoteContent, InsetContent, SpellcastingContent {
}
