package com.healthcore.entity;

import com.healthcore.enums.PharmacyStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pharmacies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pharmacy {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private String pharmacyName;
    private String licenseNumber;
    private String phone;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String operatingHours;

    @Builder.Default
    private Boolean online = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PharmacyStatus status = PharmacyStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        if (this.online == null) this.online = false;
        if (this.status == null) this.status = PharmacyStatus.PENDING;
    }
}
