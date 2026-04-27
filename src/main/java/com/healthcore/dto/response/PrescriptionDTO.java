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
public class PrescriptionDTO {
    private Long id;
    private String prescriptionCode;
    private LocalDate date;
    private String patientName;
    private Long patientId;
    private List<String> medications; // just names for doctor list view
    private String status;
    private Boolean flagged;
    private String additionalInstructions;
    private LocalDate followUpDate;
}
