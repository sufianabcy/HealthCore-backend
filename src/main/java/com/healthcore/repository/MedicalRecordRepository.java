package com.healthcore.repository;

import com.healthcore.entity.MedicalRecord;
import com.healthcore.enums.MedicalRecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    @Query("SELECT r FROM MedicalRecord r WHERE r.patient.id = :patientId " +
           "AND (:type IS NULL OR r.type = :type)")
    Page<MedicalRecord> findByPatientIdAndType(@Param("patientId") Long patientId,
                                               @Param("type") MedicalRecordType type,
                                               Pageable pageable);
}
