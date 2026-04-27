package com.healthcore.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String pharmacyName;
    private String licenseNumber;
    private String phone;
    private String email;
    private String address;
    private String operatingHours;
}
