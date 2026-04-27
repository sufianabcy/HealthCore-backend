package com.healthcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private long totalPatients;
    private long totalDoctors;
    private long totalPharmacies;
    private long appointmentsToday;
    private long activeConsultations;
    private long pendingPrescriptions;
    private List<RecentPatient> recentPatients;
    private List<RecentAppointment> recentAppointments;
    private List<PendingDoctor> pendingDoctors;
    private List<FlaggedPrescription> flaggedPrescriptions;
    private List<ActivityLogDTO> recentLogs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentPatient {
        private Long id;
        private String name;
        private LocalDate registrationDate;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentAppointment {
        private Long id;
        private String patient;
        private String doctor;
        private LocalDate date;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingDoctor {
        private Long id;
        private String name;
        private String license;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlaggedPrescription {
        private Long id;
        private String patientName;
        private String doctorName;
        private Boolean flagged;
    }
}
