package com.thedndpub.data.dto.monster.content;

public sealed interface ContentValue permits ContentText, ContentNumber, ContentBoolean,
        ContentNull, ContentArray, ContentObject {
}
