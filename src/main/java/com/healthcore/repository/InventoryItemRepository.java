package com.healthcore.repository;

import com.healthcore.entity.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Page<InventoryItem> findByPharmacyId(Long pharmacyId, Pageable pageable);
    long countByPharmacyId(Long pharmacyId);
}
