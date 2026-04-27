package com.healthcore.entity;

import com.healthcore.enums.OrderMethod;
import com.healthcore.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderMethod method = OrderMethod.PICKUP;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PROCESSING;

    @PrePersist
    protected void onCreate() {
        if (this.date == null) this.date = LocalDate.now();
        if (this.status == null) this.status = OrderStatus.PROCESSING;
        if (this.method == null) this.method = OrderMethod.PICKUP;
    }
}
