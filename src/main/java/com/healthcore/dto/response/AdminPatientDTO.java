package com.healthcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPatientDTO {
    private Long id;
    private String name;
    private Integer age;
    private String contact;
    private String status;
    private LocalDate registrationDate;
}
