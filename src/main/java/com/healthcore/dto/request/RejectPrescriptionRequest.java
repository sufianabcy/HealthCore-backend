package com.healthcore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectPrescriptionRequest {
    @NotBlank
    private String reason;
}
