package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.MechanicPayload;

import java.util.List;

public record StrategyExtraction(List<ExtractedMechanicPayload> payloads, String diagnosticCode, String explanation) {
    public StrategyExtraction {
        payloads = List.copyOf(payloads);
    }

    public StrategyExtraction(MechanicPayload payload, String diagnosticCode, String explanation) {
        this(payload == null ? List.of() : List.of(new ExtractedMechanicPayload(payload, null)),
                diagnosticCode, explanation);
    }

    public static StrategyExtraction unsupported(String code, String explanation) {
        return new StrategyExtraction(List.of(), code, explanation);
    }

    public boolean supported() { return !payloads.isEmpty(); }

    public MechanicPayload payload() {
        return payloads.isEmpty() ? null : payloads.getFirst().payload();
    }
}
