package com.thedndpub.data.dte.classes;

public record ClassFeatureConsumptionDte(
        String name,
        Integer amount,
        Integer amountMin,
        Integer amountMax) {
}
