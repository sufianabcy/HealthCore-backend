package com.healthcore.dto.request;

import lombok.Data;

@Data
public class AdminSettingsRequest {
    private Boolean allowRegistrations;
    private Boolean maintenanceMode;
    private Boolean patientPortalActive;
    private Boolean doctorPortalActive;
    private Boolean pharmacistPortalActive;
}
