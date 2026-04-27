package com.healthcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPrescriptionDTO {
    private Long id;
    private String prescriptionCode;
    private String patient;
    private String doctor;
    private String pharmacy;
    private String status;
    private Boolean flagged;
}
