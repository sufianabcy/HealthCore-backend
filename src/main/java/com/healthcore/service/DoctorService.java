package com.healthcore.service;

import com.healthcore.dto.request.AddPatientRequest;
import com.healthcore.dto.response.DoctorProfileDTO;
import com.healthcore.dto.response.DoctorSummaryDTO;
import com.healthcore.dto.response.PatientDTO;
import com.healthcore.entity.Doctor;
import com.healthcore.entity.Patient;
import com.healthcore.entity.User;
import com.healthcore.enums.Gender;
import com.healthcore.enums.PatientStatus;
import com.healthcore.enums.UserRole;
import com.healthcore.exception.BadRequestException;
import com.healthcore.exception.ResourceNotFoundException;
import com.healthcore.repository.DoctorRepository;
import com.healthcore.repository.PatientRepository;
import com.healthcore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    @Transactional(readOnly = true)
    public List<String> getDepartments() {
        return doctorRepository.findDistinctDepartments();
    }

    @Transactional(readOnly = true)
    public Page<DoctorSummaryDTO> getDoctors(String department, String search, Pageable pageable) {
        Page<Doctor> doctors = doctorRepository.findByDepartmentAndSearch(department, search, pageable);
        return doctors.map(this::mapToSummary);
    }

    @Transactional(readOnly = true)
    public DoctorProfileDTO getMyProfile() {
        User user = authService.getAuthenticatedUser();
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        return mapToProfile(doctor);
    }

    @Transactional
    public DoctorProfileDTO updateStatus(boolean online) {
        User user = authService.getAuthenticatedUser();
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

        doctor.setOnline(online);
        doctor = doctorRepository.save(doctor);
        activityLogService.log("Dr. " + user.getName(), "Status changed to " + (online ? "Online" : "Offline"));
        return mapToProfile(doctor);
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> getMyPatients(Pageable pageable) {
        User user = authService.getAuthenticatedUser();
        return patientRepository.findDistinctPatientsByDoctorId(user.getId(), pageable)
                .map(this::mapToPatientDTO);
    }

    @Transactional
    public PatientDTO addPatient(AddPatientRequest request) {
        User doctorUser = authService.getAuthenticatedUser();
        
        // Generate a pseudo-email for patient dashboard accounts.
        String baseEmail = request.getName().toLowerCase().replace(" ", ".") + "@patient.com";
        String email = baseEmail;
        int counter = 1;
        while (userRepository.existsByEmail(email)) {
            email = baseEmail.replace("@", counter + "@");
            counter++;
        }

        User newUser = User.builder()
                .name(request.getName())
                .email(email)
                .password(passwordEncoder.encode("TempPass123!")) // secure temp password
                .role(UserRole.ROLE_PATIENT)
                .build();
                
        newUser = userRepository.save(newUser);

        Gender gender;
        try {
            gender = Gender.valueOf(request.getGender().toUpperCase());
        } catch (Exception e) {
            gender = Gender.OTHER;
        }

        String history = request.getHistory() != null ? request.getHistory() : request.getMedicalHistory();

        Patient patient = Patient.builder()
                .user(newUser)
                .age(request.getAge())
                .gender(gender)
                .contact(request.getContact())
                .allergies(request.getAllergies())
                .medicalHistory(history)
                .registrationDate(LocalDate.now())
                .status(PatientStatus.ACTIVE)
                .build();

        patient = patientRepository.save(patient);
        activityLogService.log("Dr. " + doctorUser.getName(), "Registered new patient: " + patient.getUser().getName());

        return mapToPatientDTO(patient);
    }

    private DoctorSummaryDTO mapToSummary(Doctor doctor) {
        return DoctorSummaryDTO.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .specialization(doctor.getSpecialization())
                .department(doctor.getDepartment())
                .online(doctor.getOnline())
                .build();
    }

    private DoctorProfileDTO mapToProfile(Doctor doctor) {
        return DoctorProfileDTO.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .email(doctor.getUser().getEmail())
                .specialization(doctor.getSpecialization())
                .department(doctor.getDepartment())
                .license(doctor.getLicense())
                .online(doctor.getOnline())
                .status(doctor.getStatus().name())
                .build();
    }

    private PatientDTO mapToPatientDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .name(patient.getUser().getName())
                .age(patient.getAge())
                .gender(patient.getGender() != null ? patient.getGender().name() : null)
                .contact(patient.getContact())
                .allergies(patient.getAllergies())
                .medicalHistory(patient.getMedicalHistory())
                .bloodType(patient.getBloodType())
                .height(patient.getHeight())
                .weight(patient.getWeight())
                .registrationDate(patient.getRegistrationDate().toString())
                .status(patient.getStatus().name())
                .labs(new ArrayList<>()) // Return empty lists since LabReport is a separate entity that doctors view
                .build();
    }
}
