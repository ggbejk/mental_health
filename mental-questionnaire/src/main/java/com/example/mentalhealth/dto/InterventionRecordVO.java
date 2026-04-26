package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InterventionRecordVO {

    private Long id;

    private Long warningId;

    private Long studentId;

    private String studentName;

    private String college;

    private String grade;

    private String className;

    private Long counselorId;

    private String content;

    private String actionType;

    private LocalDateTime followUpTime;

    private LocalDateTime createTime;
}