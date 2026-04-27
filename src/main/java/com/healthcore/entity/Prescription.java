package com.healthcore.entity;

import com.healthcore.enums.PrescriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String prescriptionCode;

    // Legacy DB schema requires this non-null unique column.
    @Column(name = "prescription_id", nullable = false, unique = true)
    private String prescriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PrescriptionStatus status = PrescriptionStatus.DRAFT;

    @Builder.Default
    private Boolean flagged = false;

    @Column(columnDefinition = "TEXT")
    private String additionalInstructions;

    private LocalDate followUpDate;

    @Column(length = 1000)
    private String rejectionReason;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Medication> medications = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.date == null) this.date = LocalDate.now();
        if (this.status == null) this.status = PrescriptionStatus.DRAFT;
        if (this.flagged == null) this.flagged = false;
        if (this.prescriptionId == null || this.prescriptionId.isBlank()) {
            this.prescriptionId = "PR-" + UUID.randomUUID();
        }
    }

    @PostPersist
    protected void afterPersist() {
        if (this.prescriptionCode == null) {
            this.prescriptionCode = "RX-" + String.format("%05d", this.id);
        }
    }
}
