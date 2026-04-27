package com.healthcore.controller;

import com.healthcore.dto.request.BookAppointmentRequest;
import com.healthcore.dto.response.ApiResponse;
import com.healthcore.dto.response.AppointmentDTO;
import com.healthcore.dto.response.MedicalRecordDTO;
import com.healthcore.dto.response.PagedResponse;
import com.healthcore.service.AppointmentService;
import com.healthcore.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients/me")
@RequiredArgsConstructor
public class PatientController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;

    @GetMapping("/appointments")
    public ApiResponse<PagedResponse<AppointmentDTO>> getAppointments(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(appointmentService.getPatientAppointments(pageable)));
    }

    @PostMapping("/appointments")
    public ApiResponse<AppointmentDTO> bookAppointment(@Valid @RequestBody BookAppointmentRequest request) {
        return ApiResponse.of(appointmentService.bookAppointment(request));
    }

    @GetMapping("/records")
    public ApiResponse<PagedResponse<MedicalRecordDTO>> getRecords(
            @RequestParam(required = false) String type,
            Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(patientService.getMyRecords(type, pageable)));
    }
}
