package com.healthcore.repository;

import com.healthcore.entity.Patient;
import com.healthcore.enums.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserId(Long userId);

    Page<Patient> findAll(Pageable pageable);

    long count();

    List<Patient> findTop3ByOrderByRegistrationDateDesc();

    @Query("SELECT p FROM Patient p WHERE p.user.id IN " +
           "(SELECT a.patient.user.id FROM Appointment a WHERE a.doctor.id = :doctorId)")
    Page<Patient> findDistinctPatientsByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.status = :status")
    long countByStatus(@Param("status") PatientStatus status);
}
