package com.healthcore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddPatientRequest {
    @NotBlank
    private String name;

    @NotNull
    private Integer age;

    private String gender;
    private String contact;
    private String allergies;
    private String medicalHistory;
    private String history; // alias, frontend uses both
}
