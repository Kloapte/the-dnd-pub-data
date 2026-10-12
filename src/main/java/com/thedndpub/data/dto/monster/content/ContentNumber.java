package com.thedndpub.data.dto.monster.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ContentNumber(BigDecimal value) implements ContentValue {
}
