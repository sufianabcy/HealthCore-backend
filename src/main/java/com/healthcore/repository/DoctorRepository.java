package com.healthcore.repository;

import com.healthcore.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);

    Page<Doctor> findAll(Pageable pageable);

    @Query("SELECT DISTINCT d.department FROM Doctor d WHERE d.department IS NOT NULL")
    List<String> findDistinctDepartments();

    @Query("SELECT d FROM Doctor d WHERE (:department IS NULL OR d.department = :department) " +
           "AND (:search IS NULL OR LOWER(d.user.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Doctor> findByDepartmentAndSearch(@Param("department") String department,
                                           @Param("search") String search,
                                           Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.status = 'PENDING'")
    List<Doctor> findPendingDoctors();

    long count();
}
