package com.healthcore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestockInventoryRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotNull
    private Integer stock;
}
