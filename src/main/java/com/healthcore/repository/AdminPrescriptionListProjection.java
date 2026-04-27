package com.healthcore.repository;

/**
 * Native-query projection for admin prescription list (LEFT JOINs avoid orphan FK failures).
 */
public interface AdminPrescriptionListProjection {
    Long getId();

    String getPrescriptionCode();

    String getPatient();

    String getDoctor();

    String getPharmacy();

    String getStatus();

    Boolean getFlagged();
}
