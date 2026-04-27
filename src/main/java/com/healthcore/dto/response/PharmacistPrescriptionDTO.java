package com.healthcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacistPrescriptionDTO {
    private Long id;
    private String prescriptionCode;
    private LocalDate date;
    private PatientInfo patient;
    private DoctorInfo doctor;
    private List<MedicationInfo> medications;
    private String instructions; // additionalInstructions
    private String status;
    private Boolean flagged;
    private String rejectionReason;

    // Also expose flat fields for simple list display
    private String medication; // first medication name for list view

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {
        private String name;
        private String dateOfBirth;
        private String allergies;
        private String contact;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorInfo {
        private String name;
        private String specialization;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicationInfo {
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
        private String notes;
    }
}
