package com.healthcore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAppointmentByDoctorRequest {
    @NotNull
    private Long patientId;

    private String date;
    private String time;
    private String type; // "Virtual" or "In-Person"
    private Integer duration;
}
