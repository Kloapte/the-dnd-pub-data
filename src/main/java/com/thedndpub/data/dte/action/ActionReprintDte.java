package com.thedndpub.data.dte.action;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ActionReprintDeserializer.class)
public record ActionReprintDte(String uid, String tag) {
}
