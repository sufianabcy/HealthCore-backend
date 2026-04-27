package com.healthcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyProfileDTO {
    private Long id;
    private String pharmacyName;
    private String licenseNumber;
    private String phone;
    private String email;
    private String address;
    private String operatingHours;
    private Boolean online;
    private String status;
}
