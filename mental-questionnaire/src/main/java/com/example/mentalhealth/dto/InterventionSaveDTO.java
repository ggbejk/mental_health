package com.example.mentalhealth.dto;

import lombok.Data;

@Data
public class InterventionSaveDTO {

    private Long warningId;

    private Long studentId;

    private Long counselorId;

    private String content;

    private String actionType;

    private String followUpTime;
}