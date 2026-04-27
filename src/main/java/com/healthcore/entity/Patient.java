package com.healthcore.entity;

import com.healthcore.enums.Gender;
import com.healthcore.enums.PatientStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String contact;

    @Column(length = 500)
    private String allergies;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    private String bloodType;
    private String height;
    private String weight;

    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PatientStatus status = PatientStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        if (this.registrationDate == null) {
            this.registrationDate = LocalDate.now();
        }
        if (this.status == null) {
            this.status = PatientStatus.ACTIVE;
        }
    }
}
