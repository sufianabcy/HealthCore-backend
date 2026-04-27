package com.healthcore.service;

import com.healthcore.dto.request.CreatePrescriptionRequest;
import com.healthcore.dto.request.MedicationRequest;
import com.healthcore.dto.request.RejectPrescriptionRequest;
import com.healthcore.dto.response.PharmacistPrescriptionDTO;
import com.healthcore.dto.response.PrescriptionDTO;
import com.healthcore.entity.Doctor;
import com.healthcore.entity.Medication;
import com.healthcore.entity.Patient;
import com.healthcore.entity.Pharmacy;
import com.healthcore.entity.Prescription;
import com.healthcore.entity.User;
import com.healthcore.entity.Order;
import com.healthcore.enums.OrderStatus;
import com.healthcore.enums.PrescriptionStatus;
import com.healthcore.exception.BadRequestException;
import com.healthcore.exception.ResourceNotFoundException;
import com.healthcore.repository.DoctorRepository;
import com.healthcore.repository.OrderRepository;
import com.healthcore.repository.PatientRepository;
import com.healthcore.repository.PharmacyRepository;
import com.healthcore.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PharmacyRepository pharmacyRepository;
    private final OrderRepository orderRepository;
    private final ActivityLogService activityLogService;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<PrescriptionDTO> getDoctorPrescriptions(Pageable pageable) {
        User user = authService.getAuthenticatedUser();
        return prescriptionRepository.findValidByDoctorId(user.getId(), pageable)
                .map(this::mapToDoctorDTO);
    }

    @Transactional
    public PrescriptionDTO createPrescription(CreatePrescriptionRequest request) {
        User user = authService.getAuthenticatedUser();
        Doctor doctor = doctorRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", user.getId()));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId()));

        PrescriptionStatus status;
        try {
            status = request.getStatus() != null ? PrescriptionStatus.valueOf(request.getStatus().toUpperCase()) : PrescriptionStatus.DRAFT;
        } catch (IllegalArgumentException e) {
            status = PrescriptionStatus.DRAFT;
        }

        Prescription prescription = Prescription.builder()
                .patient(patient)
                .doctor(doctor)
                .date(request.getDate() != null ? LocalDate.parse(request.getDate()) : LocalDate.now())
                .status(status)
                .additionalInstructions(request.getAdditionalInstructions())
                .followUpDate(request.getFollowUpDate() != null ? LocalDate.parse(request.getFollowUpDate()) : null)
                .build();

        if (request.getMedications() != null) {
            for (MedicationRequest mReq : request.getMedications()) {
                Medication medication = Medication.builder()
                        .prescription(prescription)
                        .name(mReq.getName())
                        .dosage(mReq.getDosage())
                        .frequency(mReq.getFrequency())
                        .duration(mReq.getDuration())
                        .notes(mReq.getNotes())
                        .build();
                prescription.getMedications().add(medication);
            }
        }

        prescription = prescriptionRepository.save(prescription);
        
        if (status == PrescriptionStatus.SENT) {
            createOrderForPrescription(prescription);
            activityLogService.log("Dr. " + user.getName(), "Sent prescription " + prescription.getPrescriptionCode() + " to pharmacy network");
        } else {
            activityLogService.log("Dr. " + user.getName(), "Saved draft prescription for " + patient.getUser().getName());
        }

        return mapToDoctorDTO(prescription);
    }

    @Transactional
    public PrescriptionDTO sendDraft(Long id) {
        User user = authService.getAuthenticatedUser();
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", id));

        if (prescription.getStatus() != PrescriptionStatus.DRAFT) {
            throw new BadRequestException("Can only send Draft prescriptions");
        }

        prescription.setStatus(PrescriptionStatus.SENT);
        prescription = prescriptionRepository.save(prescription);
        createOrderForPrescription(prescription);

        activityLogService.log("Dr. " + user.getName(), "Sent draft prescription " + prescription.getPrescriptionCode());
        return mapToDoctorDTO(prescription);
    }

    private void createOrderForPrescription(Prescription prescription) {
        Order order = Order.builder()
                .prescription(prescription)
                .date(LocalDate.now())
                .status(OrderStatus.PROCESSING)
                .build();
        orderRepository.save(order);
    }

    // --- Pharmacy Endpoints ---

    @Transactional(readOnly = true)
    public Page<PharmacistPrescriptionDTO> getPharmacyPrescriptions(Pageable pageable) {
        User user = authService.getAuthenticatedUser();
        return prescriptionRepository.findValidByPharmacyIdOrUnassignedSent(user.getId(), pageable)
                .map(this::mapToPharmacistDTO);
    }

    @Transactional
    public PharmacistPrescriptionDTO verifyForPharmacy(Long id) {
        User user = authService.getAuthenticatedUser();
        Pharmacy pharmacy = pharmacyRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", user.getId()));

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", id));

        prescription.setStatus(PrescriptionStatus.VERIFIED);
        prescription.setPharmacy(pharmacy);
        prescription = prescriptionRepository.save(prescription);

        // Also assign order to this pharmacy
        orderRepository.findByPrescriptionId(id).ifPresent(order -> {
            order.setPharmacy(pharmacy);
            orderRepository.save(order);
        });

        activityLogService.log("Pharmacy: " + pharmacy.getPharmacyName(), "Verified prescription " + prescription.getPrescriptionCode());
        return mapToPharmacistDTO(prescription);
    }

    @Transactional
    public PharmacistPrescriptionDTO dispenseForPharmacy(Long id) {
        User user = authService.getAuthenticatedUser();
        Pharmacy pharmacy = pharmacyRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", user.getId()));

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", id));

        if (prescription.getStatus() != PrescriptionStatus.VERIFIED) {
            throw new BadRequestException("Only verified prescriptions can be dispensed");
        }

        prescription.setStatus(PrescriptionStatus.DISPENSED);
        prescription = prescriptionRepository.save(prescription);

        orderRepository.findByPrescriptionId(id).ifPresent(order -> {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
        });

        activityLogService.log("Pharmacy: " + pharmacy.getPharmacyName(), "Dispensed prescription " + prescription.getPrescriptionCode());
        return mapToPharmacistDTO(prescription);
    }

    @Transactional
    public PharmacistPrescriptionDTO rejectForPharmacy(Long id, RejectPrescriptionRequest request) {
        User user = authService.getAuthenticatedUser();
        Pharmacy pharmacy = pharmacyRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", user.getId()));

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", id));

        prescription.setStatus(PrescriptionStatus.REJECTED);
        prescription.setRejectionReason(request.getReason());
        prescription.setPharmacy(pharmacy);
        prescription = prescriptionRepository.save(prescription);

        activityLogService.log("Pharmacy: " + pharmacy.getPharmacyName(), "Rejected prescription " + prescription.getPrescriptionCode());
        return mapToPharmacistDTO(prescription);
    }

    // --- Mappers ---

    private PrescriptionDTO mapToDoctorDTO(Prescription p) {
        Long patientId = p.getPatient() != null ? p.getPatient().getId() : null;
        String patientName = p.getPatient() != null && p.getPatient().getUser() != null
                ? p.getPatient().getUser().getName() : "Unknown patient";
        return PrescriptionDTO.builder()
                .id(p.getId())
                .prescriptionCode(p.getPrescriptionCode())
                .date(p.getDate())
                .patientName(patientName)
                .patientId(patientId)
                .medications(p.getMedications().stream().map(Medication::getName).collect(Collectors.toList()))
                .status(p.getStatus().name())
                .flagged(p.getFlagged())
                .additionalInstructions(p.getAdditionalInstructions())
                .followUpDate(p.getFollowUpDate())
                .build();
    }

    private PharmacistPrescriptionDTO mapToPharmacistDTO(Prescription p) {
        Patient patient = p.getPatient();
        Doctor doctor = p.getDoctor();

        PharmacistPrescriptionDTO.PatientInfo pInfo = PharmacistPrescriptionDTO.PatientInfo.builder()
                .name(patient != null && patient.getUser() != null ? patient.getUser().getName() : "Unknown")
                .dateOfBirth(patient != null && patient.getAge() != null ? "Age " + patient.getAge() : "Unknown")
                .allergies(patient != null ? patient.getAllergies() : null)
                .contact(patient != null ? patient.getContact() : null)
                .build();

        String specialization = "—";
        if (doctor != null) {
            specialization = doctor.getSpecialization() != null ? doctor.getSpecialization() : doctor.getDepartment();
            if (specialization == null) {
                specialization = "—";
            }
        }
        PharmacistPrescriptionDTO.DoctorInfo dInfo = PharmacistPrescriptionDTO.DoctorInfo.builder()
                .name(doctor != null && doctor.getUser() != null ? doctor.getUser().getName() : "Unknown")
                .specialization(specialization)
                .build();

        var mInfos = p.getMedications().stream().map(m ->
                PharmacistPrescriptionDTO.MedicationInfo.builder()
                        .name(m.getName())
                        .dosage(m.getDosage())
                        .frequency(m.getFrequency())
                        .duration(m.getDuration())
                        .notes(m.getNotes())
                        .build()
        ).collect(Collectors.toList());

        String listMedicationName = p.getMedications().isEmpty() ? "None" : p.getMedications().get(0).getName();
        if (p.getMedications().size() > 1) {
            listMedicationName += " + " + (p.getMedications().size() - 1) + " more";
        }

        return PharmacistPrescriptionDTO.builder()
                .id(p.getId())
                .prescriptionCode(p.getPrescriptionCode())
                .date(p.getDate())
                .patient(pInfo)
                .doctor(dInfo)
                .medications(mInfos)
                .instructions(p.getAdditionalInstructions())
                .status(p.getStatus().name())
                .flagged(p.getFlagged())
                .rejectionReason(p.getRejectionReason())
                .medication(listMedicationName)
                .build();
    }
}
