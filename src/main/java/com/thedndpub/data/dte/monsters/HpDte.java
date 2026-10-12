package com.thedndpub.data.dte.monsters;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record HpDte(String average, String formula, String special) {
}
