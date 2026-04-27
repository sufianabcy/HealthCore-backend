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
public class AdminAppointmentDTO {
    private Long id;
    private String patient;
    private String doctor;
    private LocalDate date;
    private String status;
}
