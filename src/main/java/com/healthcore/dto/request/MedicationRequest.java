package com.healthcore.dto.request;

import lombok.Data;

@Data
public class MedicationRequest {
    private String name;
    private String dosage;
    private String frequency;
    private String duration;
    private String notes;
}
