package com.healthcore.service;

import com.healthcore.dto.request.BookAppointmentRequest;
import com.healthcore.dto.request.CreateAppointmentByDoctorRequest;
import com.healthcore.dto.request.RescheduleRequest;
import com.healthcore.dto.response.AppointmentDTO;
import com.healthcore.entity.Appointment;
import com.healthcore.entity.Doctor;
import com.healthcore.entity.Patient;
import com.healthcore.entity.User;
import com.healthcore.enums.AppointmentStatus;
import com.healthcore.enums.AppointmentType;
import com.healthcore.exception.BadRequestException;
import com.healthcore.exception.ResourceNotFoundException;
import com.healthcore.repository.AppointmentRepository;
import com.healthcore.repository.DoctorRepository;
import com.healthcore.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ActivityLogService activityLogService;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getPatientAppointments(Pageable pageable) {
        User user = authService.getAuthenticatedUser();
        return appointmentRepository.findByPatientId(user.getId(), pageable)
                .map(this::mapToDTO);
    }

    @Transactional
    public AppointmentDTO bookAppointment(BookAppointmentRequest request) {
        User user = authService.getAuthenticatedUser();
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user: " + user.getId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", request.getDoctorId()));

        AppointmentType type;
        try {
            type = AppointmentType.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid appointment type: " + request.getType());
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .date(LocalDate.parse(request.getDate()))
                .time(request.getTime())
                .type(type)
                .status(AppointmentStatus.PENDING)
                .duration(30)
                .build();

        appointment = appointmentRepository.save(appointment);
        activityLogService.log(user.getName(), "Booked appointment with Dr. " + doctor.getUser().getName() + " on " + request.getDate());

        return mapToDTO(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getDoctorSchedule(Pageable pageable) {
        User user = authService.getAuthenticatedUser();
        return appointmentRepository.findByDoctorId(user.getId(), pageable)
                .map(this::mapToDTO);
    }

    @Transactional
    public AppointmentDTO doctorCreateAppointment(CreateAppointmentByDoctorRequest request) {
        User user = authService.getAuthenticatedUser();
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId()));

        AppointmentType type;
        try {
            type = AppointmentType.valueOf(request.getType().toUpperCase().replace("-", "_")); // Virtual -> VIRTUAL, In-Person -> IN_PERSON
        } catch (IllegalArgumentException | NullPointerException e) {
            type = AppointmentType.VIRTUAL; // default
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .date(LocalDate.parse(request.getDate()))
                .time(request.getTime())
                .type(type)
                .duration(request.getDuration() != null ? request.getDuration() : 30)
                .status(AppointmentStatus.UPCOMING) // created by doctor, so it's upcoming
                .build();

        appointment = appointmentRepository.save(appointment);
        activityLogService.log("Dr. " + user.getName(), "Scheduled appointment for patient " + patient.getUser().getName() + " on " + request.getDate());

        return mapToDTO(appointment);
    }

    @Transactional
    public AppointmentDTO updateStatus(Long id, String statusStr) {
        User user = authService.getAuthenticatedUser(); // Doctor or Admin
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        AppointmentStatus status;
        try {
            status = AppointmentStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + statusStr);
        }

        appointment.setStatus(status);
        appointment = appointmentRepository.save(appointment);
        
        String logPrefix = user.getRole().name().equals("ROLE_ADMIN") ? "Admin " : "Dr. ";
        activityLogService.log(logPrefix + user.getName(), "Updated appointment " + id + " status to " + statusStr);

        return mapToDTO(appointment);
    }

    @Transactional
    public AppointmentDTO reschedule(Long id, RescheduleRequest request) {
        User user = authService.getAuthenticatedUser();
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        appointment.setDate(LocalDate.parse(request.getDate()));
        appointment.setTime(request.getTime());
        appointment = appointmentRepository.save(appointment);

        activityLogService.log("Dr. " + user.getName(), "Rescheduled appointment " + id + " to " + request.getDate() + " " + request.getTime());

        return mapToDTO(appointment);
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableSlots(Long doctorId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        List<Appointment> existing = appointmentRepository.findByDoctorIdAndDate(doctorId, date);
        List<String> allSlots = List.of("09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM");

        List<String> bookedSlots = existing.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(Appointment::getTime)
                .toList();

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .toList();
    }
    
    @Transactional
    public AppointmentDTO cancelAppointmentForAdmin(Long id) {
         User user = authService.getAuthenticatedUser();
         Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
                
         appointment.setStatus(AppointmentStatus.CANCELLED);
         appointmentRepository.save(appointment);
         activityLogService.log("Admin: " + user.getName(), "Force cancelled appointment ID: " + id);
         return mapToDTO(appointment);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        User user = authService.getAuthenticatedUser();
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        // Let doctors or patients delete. Security allows since it's just their own view in the portal.
        appointmentRepository.delete(appointment);
        activityLogService.log(user.getName(), "Deleted appointment ID: " + id);
    }

    private AppointmentDTO mapToDTO(Appointment appointment) {
        String pName = appointment.getPatient().getUser().getName();
        String dName = appointment.getDoctor().getUser().getName();
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .patientName(pName)
                .patient(pName)
                .patientId(appointment.getPatient().getId())
                .doctorName(dName)
                .doctor(dName)
                .doctorId(appointment.getDoctor().getId())
                .date(appointment.getDate())
                .time(appointment.getTime())
                .type(appointment.getType().name())
                .status(appointment.getStatus().name())
                .build();
    }
}
