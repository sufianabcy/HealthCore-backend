package com.healthcore.controller;

import com.healthcore.dto.request.RejectPrescriptionRequest;
import com.healthcore.dto.request.RestockInventoryRequest;
import com.healthcore.dto.request.UpdateOrderStatusRequest;
import com.healthcore.dto.request.UpdateProfileRequest;
import com.healthcore.dto.response.ApiResponse;
import com.healthcore.dto.response.InventoryItemDTO;
import com.healthcore.dto.response.OrderDTO;
import com.healthcore.dto.response.PharmacistPrescriptionDTO;
import com.healthcore.dto.response.PharmacyProfileDTO;
import com.healthcore.dto.response.PagedResponse;
import com.healthcore.service.InventoryService;
import com.healthcore.service.OrderService;
import com.healthcore.service.PharmacyService;
import com.healthcore.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pharmacy/me")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyService pharmacyService;
    private final PrescriptionService prescriptionService;
    private final OrderService orderService;
    private final InventoryService inventoryService;

    // --- Profile & Status ---

    @GetMapping("/profile")
    public ApiResponse<PharmacyProfileDTO> getProfile() {
        return ApiResponse.of(pharmacyService.getMyProfile());
    }

    @PutMapping("/profile")
    public ApiResponse<PharmacyProfileDTO> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ApiResponse.of(pharmacyService.updateProfile(request));
    }

    @PatchMapping("/status")
    public ApiResponse<PharmacyProfileDTO> updateStatus(@RequestBody Map<String, Boolean> payload) {
        Boolean online = payload.getOrDefault("online", false);
        return ApiResponse.of(pharmacyService.updateStatus(online));
    }

    // --- Prescriptions ---

    @GetMapping("/prescriptions")
    public ApiResponse<PagedResponse<PharmacistPrescriptionDTO>> getPrescriptions(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(prescriptionService.getPharmacyPrescriptions(pageable)));
    }

    @PatchMapping("/prescriptions/{id}/verify")
    public ApiResponse<PharmacistPrescriptionDTO> verifyPrescription(@PathVariable Long id) {
        return ApiResponse.of(prescriptionService.verifyForPharmacy(id));
    }

    @PatchMapping("/prescriptions/{id}/dispense")
    public ApiResponse<PharmacistPrescriptionDTO> dispensePrescription(@PathVariable Long id) {
        return ApiResponse.of(prescriptionService.dispenseForPharmacy(id));
    }

    @PatchMapping("/prescriptions/{id}/reject")
    public ApiResponse<PharmacistPrescriptionDTO> rejectPrescription(@PathVariable Long id, @Valid @RequestBody RejectPrescriptionRequest request) {
        return ApiResponse.of(prescriptionService.rejectForPharmacy(id, request));
    }

    // --- Orders ---

    @GetMapping("/orders")
    public ApiResponse<PagedResponse<OrderDTO>> getOrders(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(orderService.getPharmacyOrders(pageable)));
    }

    @PatchMapping("/orders/{id}/status")
    public ApiResponse<OrderDTO> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ApiResponse.of(orderService.updateStatus(id, request.getStatus()));
    }

    // --- Inventory ---

    @GetMapping("/inventory")
    public ApiResponse<PagedResponse<InventoryItemDTO>> getInventory(Pageable pageable) {
        return ApiResponse.of(PagedResponse.from(inventoryService.getPharmacyInventory(pageable)));
    }

    @PostMapping("/inventory")
    public ApiResponse<InventoryItemDTO> addInventory(@Valid @RequestBody RestockInventoryRequest request) {
        return ApiResponse.of(inventoryService.addInventory(request));
    }

    @PatchMapping("/inventory/{id}")
    public ApiResponse<InventoryItemDTO> updateInventoryStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return ApiResponse.of(inventoryService.updateStock(id, quantity));
    }
}
