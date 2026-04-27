package com.healthcore.controller;

import com.healthcore.dto.response.ApiResponse;
import com.healthcore.dto.response.DoctorSummaryDTO;
import com.healthcore.dto.response.PagedResponse;
import com.healthcore.service.AppointmentService;
import com.healthcore.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SharedController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    @GetMapping("/departments")
    public ApiResponse<List<String>> getDepartments() {
        return ApiResponse.of(doctorService.getDepartments());
    }

    @GetMapping("/doctors")
    public ApiResponse<PagedResponse<DoctorSummaryDTO>> getDoctors(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(doctorService.getDoctors(department, search, pageable)));
    }

    @GetMapping(value = {"/doctors/by-department", "/doctors/list"})
    public ApiResponse<PagedResponse<DoctorSummaryDTO>> getDoctorsByDepartment(
            @RequestParam(required = false) String department,
            Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(doctorService.getDoctors(department, null, pageable)));
    }

    @GetMapping("/appointments/available-slots")
    public ApiResponse<List<String>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date) {
        return ApiResponse.of(appointmentService.getAvailableSlots(doctorId, date));
    }
}
