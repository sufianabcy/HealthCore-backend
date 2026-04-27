package com.healthcore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RescheduleRequest {
    @NotBlank
    private String date; // YYYY-MM-DD

    @NotBlank
    private String time;
}
