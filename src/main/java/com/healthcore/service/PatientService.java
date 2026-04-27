package com.healthcore.service;

import com.healthcore.dto.response.MedicalRecordDTO;
import com.healthcore.entity.MedicalRecord;
import com.healthcore.entity.User;
import com.healthcore.enums.MedicalRecordType;
import com.healthcore.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<MedicalRecordDTO> getMyRecords(String typeStr, Pageable pageable) {
        User user = authService.getAuthenticatedUser();
        MedicalRecordType type = null;

        if (typeStr != null && !typeStr.isEmpty()) {
            try {
                // map frontend types like "Lab Result" to enum LAB_RESULT
                type = MedicalRecordType.valueOf(typeStr.replace(" ", "_").toUpperCase());
            } catch (IllegalArgumentException e) {
                // ignore invalid type filter
            }
        }

        Page<MedicalRecord> records = medicalRecordRepository.findByPatientIdAndType(user.getId(), type, pageable);
        return records.map(this::mapToDTO);
    }

    private MedicalRecordDTO mapToDTO(MedicalRecord record) {
        return MedicalRecordDTO.builder()
                .id(record.getId())
                .type(record.getType().name().replace("_", " ")) // Make it look nice for frontend
                .title(record.getTitle())
                .date(record.getDate())
                .doctor(record.getDoctor() != null ? record.getDoctor().getUser().getName() : "System")
                .status(record.getStatus())
                .build();
    }
}
