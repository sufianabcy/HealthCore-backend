package com.healthcore.repository;

import com.healthcore.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByPharmacyId(Long pharmacyId, Pageable pageable);

    Optional<Order> findByPrescriptionId(Long prescriptionId);
}
