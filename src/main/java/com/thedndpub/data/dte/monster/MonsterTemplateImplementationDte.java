package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record MonsterTemplateImplementationDte(@JsonProperty("_variables") Map<String, String> variables) {
}
