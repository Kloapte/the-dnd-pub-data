package com.thedndpub.data.mechanics.review;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/** Review trust is supplied by backend code, never by JSON fields. */
public final class ValidatedHumanMechanicsReviewDeserializer extends StdDeserializer<ValidatedHumanMechanicsReview> {
    public ValidatedHumanMechanicsReviewDeserializer() {
        super(ValidatedHumanMechanicsReview.class);
    }

    @Override
    public ValidatedHumanMechanicsReview deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        if (!(context.getAttribute(MechanicsReviewPolicy.CONTEXT_ATTRIBUTE) instanceof MechanicsReviewPolicy policy)) {
            throw JsonMappingException.from(parser, "Deserializing human review requires a trusted mechanics review policy");
        }
        HumanMechanicsReviewDecision decision = context.readValue(parser, HumanMechanicsReviewDecision.class);
        try {
            return ValidatedHumanMechanicsReview.validate(decision, policy);
        } catch (IllegalArgumentException exception) {
            throw JsonMappingException.from(parser, "Human mechanics review validation failed", exception);
        }
    }
}
