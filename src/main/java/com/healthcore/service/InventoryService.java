package com.healthcore.service;

import com.healthcore.dto.request.RestockInventoryRequest;
import com.healthcore.dto.response.InventoryItemDTO;
import com.healthcore.entity.InventoryItem;
import com.healthcore.entity.Pharmacy;
import com.healthcore.entity.User;
import com.healthcore.enums.InventoryCategory;
import com.healthcore.exception.BadRequestException;
import com.healthcore.exception.ResourceNotFoundException;
import com.healthcore.repository.InventoryItemRepository;
import com.healthcore.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final PharmacyRepository pharmacyRepository;
    private final AuthService authService;
    private final ActivityLogService activityLogService;

    @Transactional(readOnly = true)
    public Page<InventoryItemDTO> getPharmacyInventory(Pageable pageable) {
        User user = authService.getAuthenticatedUser();
        return inventoryItemRepository.findByPharmacyId(user.getId(), pageable).map(this::mapToDTO);
    }

    @Transactional
    public InventoryItemDTO addInventory(RestockInventoryRequest request) {
        User user = authService.getAuthenticatedUser();
        Pharmacy pharmacy = pharmacyRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", user.getId()));

        InventoryCategory category;
        try {
            category = InventoryCategory.valueOf(request.getCategory().replace(" ", "_").toUpperCase());
        } catch (Exception e) {
            category = InventoryCategory.OTHER;
        }

        InventoryItem item = InventoryItem.builder()
                .pharmacy(pharmacy)
                .name(request.getName())
                .category(category)
                .stock(request.getStock())
                .build();

        item = inventoryItemRepository.save(item);
        activityLogService.log("Pharmacy: " + pharmacy.getPharmacyName(), "Added new inventory item: " + item.getName());
        return mapToDTO(item);
    }

    @Transactional
    public InventoryItemDTO updateStock(Long id, Integer quantity) {
        User user = authService.getAuthenticatedUser();
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", id));

        if (quantity == null || quantity < 0) {
            throw new BadRequestException("Quantity must be a positive integer");
        }

        item.setStock(quantity);
        item = inventoryItemRepository.save(item);
        activityLogService.log("Pharmacy: " + user.getName(), "Updated stock for " + item.getName() + " to " + quantity);
        return mapToDTO(item);
    }

    private InventoryItemDTO mapToDTO(InventoryItem item) {
        return InventoryItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .category(item.getCategory().name().replace("_", " "))
                .stock(item.getStock())
                .status(item.getStatus()) // computed status field
                .build();
    }
}
