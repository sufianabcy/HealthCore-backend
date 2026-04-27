package com.healthcore.entity;

import com.healthcore.enums.InventoryCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryCategory category;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    private LocalDateTime updatedAt;

    /**
     * Computed status based on stock level.
     * stock >= 50 -> "In Stock"
     * stock 1-49 -> "Low Stock"
     * stock == 0 -> "Out of Stock"
     */
    @Transient
    public String getStatus() {
        if (stock == null || stock == 0) return "Out of Stock";
        if (stock < 50) return "Low Stock";
        return "In Stock";
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.updatedAt = LocalDateTime.now();
        if (this.stock == null) this.stock = 0;
    }
}
