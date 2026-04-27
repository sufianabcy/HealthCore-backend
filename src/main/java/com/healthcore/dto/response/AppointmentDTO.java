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
public class AppointmentDTO {
    private Long id;
    // Both field names as required by frontend
    private String patientName;
    private String patient;   // same as patientName
    private Long patientId;
    private String doctorName;
    private String doctor;    // same as doctorName
    private Long doctorId;
    private LocalDate date;
    private String time;
    private String type;
    private String status;
}
