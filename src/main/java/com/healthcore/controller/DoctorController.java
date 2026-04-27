package com.healthcore.controller;

import com.healthcore.dto.request.AddPatientRequest;
import com.healthcore.dto.request.CreateAppointmentByDoctorRequest;
import com.healthcore.dto.request.CreatePrescriptionRequest;
import com.healthcore.dto.request.RescheduleRequest;
import com.healthcore.dto.request.UpdateStatusRequest;
import com.healthcore.dto.response.*;
import com.healthcore.service.AppointmentService;
import com.healthcore.service.DoctorService;
import com.healthcore.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;

    // --- Profile & Status ---

    @GetMapping("/doctors/me")
    public ApiResponse<DoctorProfileDTO> getMyProfile() {
        return ApiResponse.of(doctorService.getMyProfile());
    }

    @PatchMapping("/doctors/me/status")
    public ApiResponse<DoctorProfileDTO> updateStatus(@RequestBody Map<String, Boolean> payload) {
        Boolean online = payload.getOrDefault("online", false);
        return ApiResponse.of(doctorService.updateStatus(online));
    }

    // --- Patients ---

    @GetMapping("/doctors/me/patients")
    public ApiResponse<PagedResponse<PatientDTO>> getMyPatients(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(doctorService.getMyPatients(pageable)));
    }

    @PostMapping("/doctors/me/patients")
    public ApiResponse<PatientDTO> addPatient(@Valid @RequestBody AddPatientRequest request) {
        return ApiResponse.of(doctorService.addPatient(request));
    }

    // --- Schedule & Appointments ---

    @GetMapping("/doctors/me/schedule")
    public ApiResponse<PagedResponse<AppointmentDTO>> getSchedule(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(appointmentService.getDoctorSchedule(pageable)));
    }

    @PostMapping("/doctors/me/appointments")
    public ApiResponse<AppointmentDTO> createAppointment(@Valid @RequestBody CreateAppointmentByDoctorRequest request) {
        return ApiResponse.of(appointmentService.doctorCreateAppointment(request));
    }

    @PatchMapping("/appointments/{id}/status")
    public ApiResponse<AppointmentDTO> updateAppointmentStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ApiResponse.of(appointmentService.updateStatus(id, request.getStatus()));
    }

    @PatchMapping("/appointments/{id}/reschedule")
    public ApiResponse<AppointmentDTO> rescheduleAppointment(@PathVariable Long id, @Valid @RequestBody RescheduleRequest request) {
        return ApiResponse.of(appointmentService.reschedule(id, request));
    }

    // --- Prescriptions ---

    @GetMapping("/doctors/me/prescriptions")
    public ApiResponse<PagedResponse<PrescriptionDTO>> getPrescriptions(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(prescriptionService.getDoctorPrescriptions(pageable)));
    }

    @PostMapping("/doctors/me/prescriptions")
    public ApiResponse<PrescriptionDTO> createPrescription(@Valid @RequestBody CreatePrescriptionRequest request) {
        return ApiResponse.of(prescriptionService.createPrescription(request));
    }

    @PatchMapping("/doctors/me/prescriptions/{id}/send")
    public ApiResponse<PrescriptionDTO> sendDraftPrescription(@PathVariable Long id) {
        return ApiResponse.of(prescriptionService.sendDraft(id));
    }
}
