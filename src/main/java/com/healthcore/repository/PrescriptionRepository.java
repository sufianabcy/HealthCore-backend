package com.healthcore.repository;

import com.healthcore.entity.Prescription;
import com.healthcore.enums.PrescriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

                @Query("""
                                                SELECT p
                                                FROM Prescription p
                                                JOIN p.patient pat
                                                JOIN pat.user pu
                                                JOIN p.doctor doc
                                                JOIN doc.user du
                                                WHERE doc.id = :doctorId
                                                        AND p.prescriptionCode IS NOT NULL
                                                """)
                Page<Prescription> findValidByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);

    Page<Prescription> findAll(Pageable pageable);

                @Query("""
                                                SELECT p
                                                FROM Prescription p
                                                JOIN p.patient pat
                                                JOIN pat.user pu
                                                JOIN p.doctor doc
                                                JOIN doc.user du
                                                WHERE (p.pharmacy.id = :pharmacyId OR (p.status = 'SENT' AND p.pharmacy IS NULL))
                                                        AND p.prescriptionCode IS NOT NULL
                                                """)
                Page<Prescription> findValidByPharmacyIdOrUnassignedSent(@Param("pharmacyId") Long pharmacyId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.status = :status")
    long countByStatus(@Param("status") PrescriptionStatus status);

    @Query("SELECT p FROM Prescription p WHERE p.flagged = true")
    List<Prescription> findFlaggedPrescriptions();

    /**
     * Admin list: SQL LEFT JOINs so missing patient/doctor/pharmacy rows never cause 500s.
     */
    @Query(
            value = """
                    SELECT p.id AS id,
                           p.prescription_code AS prescriptionCode,
                           COALESCE(pu.name, 'Unknown patient') AS patient,
                           COALESCE(du.name, 'Unknown doctor') AS doctor,
                           COALESCE(ph.pharmacy_name, 'Unassigned') AS pharmacy,
                           p.status AS status,
                           p.flagged AS flagged
                    FROM prescriptions p
                    LEFT JOIN patients pat ON pat.id = p.patient_id
                    LEFT JOIN users pu ON pu.id = pat.id
                    LEFT JOIN doctors doc ON doc.id = p.doctor_id
                    LEFT JOIN users du ON du.id = doc.id
                    LEFT JOIN pharmacies ph ON ph.id = p.pharmacy_id
                    """,
            countQuery = "SELECT COUNT(*) FROM prescriptions",
            nativeQuery = true)
    Page<AdminPrescriptionListProjection> findAllForAdminList(Pageable pageable);
}
