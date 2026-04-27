package com.healthcore.service;

import com.healthcore.dto.response.OrderDTO;
import com.healthcore.entity.Order;
import com.healthcore.entity.User;
import com.healthcore.enums.OrderStatus;
import com.healthcore.exception.BadRequestException;
import com.healthcore.exception.ResourceNotFoundException;
import com.healthcore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final ActivityLogService activityLogService;

    @Transactional(readOnly = true)
    public Page<OrderDTO> getPharmacyOrders(Pageable pageable) {
        User user = authService.getAuthenticatedUser();
        return orderRepository.findByPharmacyId(user.getId(), pageable).map(this::mapToDTO);
    }

    @Transactional
    public OrderDTO updateStatus(Long id, String statusStr) {
        User user = authService.getAuthenticatedUser(); // Pharmacist
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        // Note: Production should verify order belongs to this pharmacy
        
        OrderStatus status;
        try {
            statusStr = statusStr.replace(" ", "_").toUpperCase(); // "Ready to Ship" -> "READY_TO_SHIP"
            status = OrderStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + statusStr);
        }

        order.setStatus(status);
        order = orderRepository.save(order);
        activityLogService.log("Pharmacy: " + user.getName(), "Updated order " + id + " status to " + status.name());
        return mapToDTO(order);
    }

    private OrderDTO mapToDTO(Order order) {
        String patientName = "Unknown";
        if (order.getPrescription() != null && order.getPrescription().getPatient() != null) {
            patientName = order.getPrescription().getPatient().getUser().getName();
        }

        return OrderDTO.builder()
                .id(order.getId())
                .date(order.getDate())
                .patient(patientName)
                .method(order.getMethod().name())
                .status(order.getStatus().name().replace("_", " "))
                .build();
    }
}
