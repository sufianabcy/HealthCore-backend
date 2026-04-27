package com.healthcore.service;

import com.healthcore.dto.request.UpdateProfileRequest;
import com.healthcore.dto.response.PharmacyProfileDTO;
import com.healthcore.entity.Pharmacy;
import com.healthcore.entity.User;
import com.healthcore.exception.ResourceNotFoundException;
import com.healthcore.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final AuthService authService;
    private final ActivityLogService activityLogService;

    @Transactional(readOnly = true)
    public PharmacyProfileDTO getMyProfile() {
        User user = authService.getAuthenticatedUser();
        Pharmacy pharmacy = pharmacyRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", user.getId()));
        return mapToDTO(pharmacy);
    }

    @Transactional
    public PharmacyProfileDTO updateProfile(UpdateProfileRequest request) {
        User user = authService.getAuthenticatedUser();
        Pharmacy pharmacy = pharmacyRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", user.getId()));

        if (request.getPharmacyName() != null) pharmacy.setPharmacyName(request.getPharmacyName());
        if (request.getLicenseNumber() != null) pharmacy.setLicenseNumber(request.getLicenseNumber());
        if (request.getPhone() != null) pharmacy.setPhone(request.getPhone());
        if (request.getAddress() != null) pharmacy.setAddress(request.getAddress());
        if (request.getOperatingHours() != null) pharmacy.setOperatingHours(request.getOperatingHours());

        // Assuming email updates user email too, though simplifying here for profile fields
        
        pharmacy = pharmacyRepository.save(pharmacy);
        activityLogService.log("Pharmacy: " + pharmacy.getPharmacyName(), "Updated profile information");
        return mapToDTO(pharmacy);
    }

    @Transactional
    public PharmacyProfileDTO updateStatus(boolean online) {
        User user = authService.getAuthenticatedUser();
        Pharmacy pharmacy = pharmacyRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", user.getId()));

        pharmacy.setOnline(online);
        pharmacy = pharmacyRepository.save(pharmacy);
        activityLogService.log("Pharmacy: " + pharmacy.getPharmacyName(), "Status changed to " + (online ? "Online" : "Offline"));
        return mapToDTO(pharmacy);
    }

    private PharmacyProfileDTO mapToDTO(Pharmacy pharmacy) {
        return PharmacyProfileDTO.builder()
                .id(pharmacy.getId())
                .pharmacyName(pharmacy.getPharmacyName())
                .licenseNumber(pharmacy.getLicenseNumber())
                .phone(pharmacy.getPhone())
                .email(pharmacy.getUser().getEmail())
                .address(pharmacy.getAddress())
                .operatingHours(pharmacy.getOperatingHours())
                .online(pharmacy.getOnline())
                .status(pharmacy.getStatus().name())
                .build();
    }
}
