package com.healthcore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreatePrescriptionRequest {
    @NotNull
    private Long patientId;

    private String date; // YYYY-MM-DD, defaults to today if null

    private String status; // "Draft" or "Sent"

    private List<MedicationRequest> medications;

    private String additionalInstructions;

    private String followUpDate; // YYYY-MM-DD
}
