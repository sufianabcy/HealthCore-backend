package com.healthcore.controller;

import com.healthcore.dto.request.AdminSettingsRequest;
import com.healthcore.dto.request.UpdateStatusRequest;
import com.healthcore.dto.response.*;
import com.healthcore.service.AdminService;
import com.healthcore.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AppointmentService appointmentService;

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardDTO> getDashboard() {
        return ApiResponse.of(adminService.getDashboardData());
    }

    // --- Directories ---

    @GetMapping("/patients")
    public ApiResponse<PagedResponse<AdminPatientDTO>> getPatients(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(adminService.getPatients(pageable)));
    }

    @PatchMapping("/patients/{id}/status")
    public ApiResponse<AdminPatientDTO> updatePatientStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ApiResponse.of(adminService.updatePatientStatus(id, request.getStatus()));
    }

    @GetMapping("/doctors")
    public ApiResponse<PagedResponse<AdminDoctorDTO>> getDoctors(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(adminService.getDoctors(pageable)));
    }

    @PatchMapping("/doctors/{id}/status")
    public ApiResponse<AdminDoctorDTO> updateDoctorStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ApiResponse.of(adminService.updateDoctorStatus(id, request.getStatus()));
    }

    @GetMapping("/pharmacies")
    public ApiResponse<PagedResponse<AdminPharmacyDTO>> getPharmacies(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(adminService.getPharmacies(pageable)));
    }

    @PatchMapping("/pharmacies/{id}/status")
    public ApiResponse<AdminPharmacyDTO> updatePharmacyStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ApiResponse.of(adminService.updatePharmacyStatus(id, request.getStatus()));
    }

    // --- Monitored Entities ---

    @GetMapping("/appointments")
    public ApiResponse<PagedResponse<AdminAppointmentDTO>> getAppointments(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(adminService.getAppointments(pageable)));
    }

    @PatchMapping("/appointments/{id}/cancel")
    public ApiResponse<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        return ApiResponse.of(appointmentService.cancelAppointmentForAdmin(id));
    }

    @GetMapping("/prescriptions")
    public ApiResponse<PagedResponse<AdminPrescriptionDTO>> getPrescriptions(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(adminService.getPrescriptions(pageable)));
    }

    @PatchMapping("/prescriptions/{id}/flag")
    public ApiResponse<AdminPrescriptionDTO> flagPrescription(@PathVariable Long id) {
        return ApiResponse.of(adminService.togglePrescriptionFlag(id));
    }

    @GetMapping("/logs")
    public ApiResponse<PagedResponse<ActivityLogDTO>> getLogs(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(adminService.getLogs(pageable)));
    }

    // --- Settings ---

    @GetMapping("/settings")
    public ApiResponse<AdminSettingsDTO> getSettings() {
        return ApiResponse.of(adminService.getSettings());
    }

    @PutMapping("/settings") // Using @PutMapping for explicit update matching frontend specs
    public ApiResponse<AdminSettingsDTO> updateSettings(@RequestBody AdminSettingsRequest request) {
        return ApiResponse.of(adminService.updateSettings(request));
    }
}
