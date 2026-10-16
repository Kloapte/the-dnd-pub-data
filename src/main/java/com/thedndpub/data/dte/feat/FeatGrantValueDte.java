package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FeatGrantValueDeserializer.class)
public sealed interface FeatGrantValueDte
        permits FeatBooleanGrantDte, FeatCountGrantDte, FeatChoiceGrantDte {
}
