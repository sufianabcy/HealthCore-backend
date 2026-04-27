package com.healthcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String contact;
    private String allergies;
    private String medicalHistory;
    private String bloodType;
    private String height;
    private String weight;
    private String registrationDate;
    private String status;
    private List<LabReportDTO> labs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LabReportDTO {
        private String name;
        private String date;
        private String result;
    }
}
