package com.healthcore.entity;

import com.healthcore.enums.DoctorStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private String specialization;
    private String department;
    private String license;

    @Builder.Default
    private Boolean online = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DoctorStatus status = DoctorStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        if (this.online == null) this.online = false;
        if (this.status == null) this.status = DoctorStatus.PENDING;
    }
}
