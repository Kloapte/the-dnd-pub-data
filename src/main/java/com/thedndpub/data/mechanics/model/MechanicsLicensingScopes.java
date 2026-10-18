package com.thedndpub.data.mechanics.model;

final class MechanicsLicensingScopes {
    private MechanicsLicensingScopes() {
    }

    static boolean matches(MechanicsLicensing licensing, MechanicRevisionReference claim) {
        return switch (licensing) {
            case UnassessedMechanicsLicensing ignored -> true;
            case SrdMechanicsLicensing srd -> srd.coverage().stream()
                    .allMatch(coverage -> coverage.scope().claim().equals(claim));
            case OtherMechanicsLicensing other -> other.coverage().stream()
                    .allMatch(coverage -> coverage.scope().claim().equals(claim));
            case OpenMechanicsLicensing open -> open.basis().scope().claim().equals(claim);
        };
    }
}
