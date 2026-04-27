package com.healthcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSummaryDTO {
    private Long id;
    private String name;
    private String specialization;
    private String department;
    private Boolean online;
}
