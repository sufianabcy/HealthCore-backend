package com.healthcore.repository;

import com.healthcore.entity.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    Optional<Pharmacy> findByUserId(Long userId);
    Page<Pharmacy> findAll(Pageable pageable);
    long count();
}
