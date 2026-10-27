package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IngestTaintedMaterialExposure.class, name = "INGEST_TAINTED_MATERIAL"),
        @JsonSubTypes.Type(value = IngestContaminatedMaterialExposure.class, name = "INGEST_CONTAMINATED_MATERIAL"),
        @JsonSubTypes.Type(
                value = ContactWithInfectedCreatureExposure.class,
                name = "CONTACT_WITH_INFECTED_CREATURE"),
        @JsonSubTypes.Type(value = InfectedCreatureProximityExposure.class, name = "INFECTED_CREATURE_PROXIMITY"),
        @JsonSubTypes.Type(value = EnvironmentalExposure.class, name = "ENVIRONMENTAL_EXPOSURE"),
        @JsonSubTypes.Type(value = EnvironmentalContactExposure.class, name = "ENVIRONMENTAL_CONTACT"),
        @JsonSubTypes.Type(value = LongRestInQualifyingAreaExposure.class, name = "LONG_REST_IN_QUALIFYING_AREA"),
        @JsonSubTypes.Type(
                value = QualifyingDamageAndEncounterEndExposure.class,
                name = "QUALIFYING_DAMAGE_AND_ENCOUNTER_END"),
        @JsonSubTypes.Type(
                value = WoundByContagionCarrierExposure.class,
                name = "WOUND_BY_CONTAGION_CARRIER"),
        @JsonSubTypes.Type(value = ContactWithContaminatedMaterialExposure.class, name = "CONTACT_WITH_CONTAMINATED_MATERIAL")
})
public sealed interface DiseaseExposure permits
        IngestTaintedMaterialExposure,
        IngestContaminatedMaterialExposure,
        ContactWithInfectedCreatureExposure,
        InfectedCreatureProximityExposure,
        EnvironmentalExposure,
        EnvironmentalContactExposure,
        LongRestInQualifyingAreaExposure,
        QualifyingDamageAndEncounterEndExposure,
        WoundByContagionCarrierExposure,
        ContactWithContaminatedMaterialExposure {
    String kind();
}
