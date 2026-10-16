package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemChargeDto(Integer count, String countText, String recharge, Integer rechargeAmount,
                           String rechargeAmountText) {
}
