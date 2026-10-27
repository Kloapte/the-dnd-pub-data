package com.thedndpub.data.dto.disease.recovery;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HerbalismKitOintmentTreatment.class, name = "HERBALISM_KIT_OINTMENT"),
        @JsonSubTypes.Type(value = ManufacturedAntidoteTreatment.class, name = "MANUFACTURED_ANTIDOTE")
})
public sealed interface DiseaseTreatment permits HerbalismKitOintmentTreatment, ManufacturedAntidoteTreatment {
    String kind();
}
