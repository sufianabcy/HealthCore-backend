package com.healthcore.service;

import com.healthcore.dto.request.AdminSettingsRequest;
import com.healthcore.dto.response.*;
import com.healthcore.entity.Patient;
import com.healthcore.entity.Doctor;
import com.healthcore.entity.Pharmacy;
import com.healthcore.entity.Appointment;
import com.healthcore.entity.Prescription;
import com.healthcore.entity.SystemSettings;
import com.healthcore.entity.User;
import com.healthcore.enums.AppointmentStatus;
import com.healthcore.enums.DoctorStatus;
import com.healthcore.enums.PatientStatus;
import com.healthcore.enums.PharmacyStatus;
import com.healthcore.enums.PrescriptionStatus;
import com.healthcore.exception.BadRequestException;
import com.healthcore.exception.ResourceNotFoundException;
import com.healthcore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PharmacyRepository pharmacyRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ActivityLogRepository activityLogRepository;
    private final SystemSettingsRepository settingsRepository;
    private final ActivityLogService activityLogService;
    private final AuthService authService;

    // --- Direct Getters for Dashboards / Lists ---

    @Transactional(readOnly = true)
    public Page<AdminPatientDTO> getPatients(Pageable pageable) {
        return patientRepository.findAll(pageable).map(this::mapPatient);
    }

    @Transactional(readOnly = true)
    public Page<AdminDoctorDTO> getDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable).map(this::mapDoctor);
    }

    @Transactional(readOnly = true)
    public Page<AdminPharmacyDTO> getPharmacies(Pageable pageable) {
        return pharmacyRepository.findAll(pageable).map(this::mapPharmacy);
    }

    @Transactional(readOnly = true)
    public Page<AdminAppointmentDTO> getAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(this::mapAppointment);
    }

    @Transactional(readOnly = true)
    public Page<AdminPrescriptionDTO> getPrescriptions(Pageable pageable) {
        return prescriptionRepository.findAllForAdminList(pageable).map(row ->
                AdminPrescriptionDTO.builder()
                        .id(row.getId())
                        .prescriptionCode(row.getPrescriptionCode())
                        .patient(row.getPatient())
                        .doctor(row.getDoctor())
                        .pharmacy(row.getPharmacy())
                        .status(row.getStatus())
                        .flagged(Boolean.TRUE.equals(row.getFlagged()))
                        .build());
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> getLogs(Pageable pageable) {
        return activityLogRepository.findAllByOrderByTimestampDesc(pageable).map(l ->
            new ActivityLogDTO(l.getId(), l.getActor(), l.getAction(), l.getTimestamp()));
    }

    // --- State Changers ---

    @Transactional
    public AdminPatientDTO updatePatientStatus(Long id, String statusStr) {
        User admin = authService.getAuthenticatedUser();
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        try {
            patient.setStatus(PatientStatus.valueOf(statusStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status");
        }
        patient = patientRepository.save(patient);
        activityLogService.log(admin.getName(), "Updated patient " + patient.getUser().getName() + " status to " + statusStr);
        return mapPatient(patient);
    }

    @Transactional
    public AdminDoctorDTO updateDoctorStatus(Long id, String statusStr) {
        User admin = authService.getAuthenticatedUser();
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        try {
            doctor.setStatus(DoctorStatus.valueOf(statusStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status");
        }
        doctor = doctorRepository.save(doctor);
        activityLogService.log(admin.getName(), "Updated doctor " + doctor.getUser().getName() + " status to " + statusStr);
        return mapDoctor(doctor);
    }

    @Transactional
    public AdminPharmacyDTO updatePharmacyStatus(Long id, String statusStr) {
        User admin = authService.getAuthenticatedUser();
        Pharmacy pharmacy = pharmacyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pharmacy", id));
        try {
            pharmacy.setStatus(PharmacyStatus.valueOf(statusStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status");
        }
        pharmacy = pharmacyRepository.save(pharmacy);
        activityLogService.log(admin.getName(), "Updated pharmacy " + pharmacy.getPharmacyName() + " status to " + statusStr);
        return mapPharmacy(pharmacy);
    }

    @Transactional
    public AdminPrescriptionDTO togglePrescriptionFlag(Long id) {
        User admin = authService.getAuthenticatedUser();
        Prescription prescription = prescriptionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Prescription", id));
        prescription.setFlagged(!prescription.getFlagged());
        prescription = prescriptionRepository.save(prescription);
        activityLogService.log(admin.getName(), (prescription.getFlagged() ? "Flagged" : "Unflagged") + " prescription " + prescription.getPrescriptionCode());
        return mapPrescription(prescription);
    }

    // --- Dashboard & Settings ---

    @Transactional(readOnly = true)
    public AdminDashboardDTO getDashboardData() {
        LocalDate today = LocalDate.now();
        return AdminDashboardDTO.builder()
                .totalPatients(patientRepository.count())
                .totalDoctors(doctorRepository.count())
                .totalPharmacies(pharmacyRepository.count())
                .appointmentsToday(appointmentRepository.countByDate(today))
                .activeConsultations(appointmentRepository.countByStatus(AppointmentStatus.ACTIVE))
                .pendingPrescriptions(prescriptionRepository.countByStatus(PrescriptionStatus.PENDING))
                .recentPatients(patientRepository.findTop3ByOrderByRegistrationDateDesc().stream()
                        .map(p -> new AdminDashboardDTO.RecentPatient(p.getId(), p.getUser().getName(), p.getRegistrationDate(), p.getStatus().name()))
                        .collect(Collectors.toList()))
                .recentAppointments(appointmentRepository.findTop3ByOrderByDateDesc().stream()
                        .map(a -> new AdminDashboardDTO.RecentAppointment(a.getId(), a.getPatient().getUser().getName(), a.getDoctor().getUser().getName(), a.getDate(), a.getStatus().name()))
                        .collect(Collectors.toList()))
                .pendingDoctors(doctorRepository.findPendingDoctors().stream()
                        .map(d -> new AdminDashboardDTO.PendingDoctor(d.getId(), d.getUser().getName(), d.getLicense(), d.getStatus().name()))
                        .collect(Collectors.toList()))
                .flaggedPrescriptions(prescriptionRepository.findFlaggedPrescriptions().stream()
                        .map(p -> new AdminDashboardDTO.FlaggedPrescription(
                                p.getId(),
                                p.getPatient() != null && p.getPatient().getUser() != null ? p.getPatient().getUser().getName() : "Unknown",
                                p.getDoctor() != null && p.getDoctor().getUser() != null ? p.getDoctor().getUser().getName() : "Unknown",
                                true))
                        .collect(Collectors.toList()))
                .recentLogs(activityLogRepository.findTop5ByOrderByTimestampDesc().stream()
                        .map(l -> new ActivityLogDTO(l.getId(), l.getActor(), l.getAction(), l.getTimestamp()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public AdminSettingsDTO getSettings() {
        SystemSettings settings = settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> settingsRepository.save(new SystemSettings()));
        return mapSettings(settings);
    }

    @Transactional
    public AdminSettingsDTO updateSettings(AdminSettingsRequest request) {
        User admin = authService.getAuthenticatedUser();
        SystemSettings settings = settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(SystemSettings::new);
                
        if (request.getAllowRegistrations() != null) settings.setAllowRegistrations(request.getAllowRegistrations());
        if (request.getMaintenanceMode() != null) settings.setMaintenanceMode(request.getMaintenanceMode());
        if (request.getPatientPortalActive() != null) settings.setPatientPortalActive(request.getPatientPortalActive());
        if (request.getDoctorPortalActive() != null) settings.setDoctorPortalActive(request.getDoctorPortalActive());
        if (request.getPharmacistPortalActive() != null) settings.setPharmacistPortalActive(request.getPharmacistPortalActive());

        settings = settingsRepository.save(settings);
        activityLogService.log(admin.getName(), "Updated system settings");
        return mapSettings(settings);
    }

    // --- Mappers ---

    private AdminPatientDTO mapPatient(Patient p) {
        return AdminPatientDTO.builder()
                .id(p.getId()).name(p.getUser().getName()).age(p.getAge())
                .contact(p.getContact()).status(p.getStatus().name()).registrationDate(p.getRegistrationDate())
                .build();
    }

    private AdminDoctorDTO mapDoctor(Doctor d) {
        return AdminDoctorDTO.builder()
                .id(d.getId()).name(d.getUser().getName()).license(d.getLicense())
                .specialization(d.getSpecialization()).status(d.getStatus().name())
                .build();
    }

    private AdminPharmacyDTO mapPharmacy(Pharmacy p) {
        return AdminPharmacyDTO.builder()
                .id(p.getId()).name(p.getPharmacyName()).license(p.getLicenseNumber())
                .contact(p.getPhone()).status(p.getStatus().name())
                .build();
    }

    private AdminAppointmentDTO mapAppointment(Appointment a) {
        return AdminAppointmentDTO.builder()
                .id(a.getId()).patient(a.getPatient().getUser().getName())
                .doctor(a.getDoctor().getUser().getName()).date(a.getDate()).status(a.getStatus().name())
                .build();
    }

    private AdminPrescriptionDTO mapPrescription(Prescription p) {
        String patientName = p.getPatient() != null && p.getPatient().getUser() != null
                ? p.getPatient().getUser().getName() : "Unknown patient";
        String doctorName = p.getDoctor() != null && p.getDoctor().getUser() != null
                ? p.getDoctor().getUser().getName() : "Unknown doctor";
        return AdminPrescriptionDTO.builder()
                .id(p.getId()).prescriptionCode(p.getPrescriptionCode())
                .patient(patientName).doctor(doctorName)
                .pharmacy(p.getPharmacy() != null ? p.getPharmacy().getPharmacyName() : "Unassigned")
                .status(p.getStatus().name()).flagged(p.getFlagged())
                .build();
    }

    private AdminSettingsDTO mapSettings(SystemSettings s) {
        return AdminSettingsDTO.builder()
                .allowRegistrations(s.getAllowRegistrations())
                .maintenanceMode(s.getMaintenanceMode())
                .patientPortalActive(s.getPatientPortalActive())
                .doctorPortalActive(s.getDoctorPortalActive())
                .pharmacistPortalActive(s.getPharmacistPortalActive())
                .build();
    }
}
