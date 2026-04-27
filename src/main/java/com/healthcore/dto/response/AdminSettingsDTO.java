package com.healthcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSettingsDTO {
    private Boolean allowRegistrations;
    private Boolean maintenanceMode;
    private Boolean patientPortalActive;
    private Boolean doctorPortalActive;
    private Boolean pharmacistPortalActive;
}
