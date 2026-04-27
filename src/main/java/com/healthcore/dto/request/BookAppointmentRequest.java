package com.healthcore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookAppointmentRequest {
    @NotNull
    private Long doctorId;

    @NotBlank
    private String date; // YYYY-MM-DD

    @NotBlank
    private String time; // e.g. "09:00 AM"

    @NotBlank
    private String type; // "VIRTUAL" or "IN_PERSON"
}
