package com.healthcore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Boolean allowRegistrations = true;

    @Builder.Default
    private Boolean maintenanceMode = false;

    @Builder.Default
    private Boolean patientPortalActive = true;

    @Builder.Default
    private Boolean doctorPortalActive = true;

    @Builder.Default
    private Boolean pharmacistPortalActive = true;
}
