package com.healthcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String specialization;
    private String department;
    private String license;
    private Boolean online;
    private String status;
}
