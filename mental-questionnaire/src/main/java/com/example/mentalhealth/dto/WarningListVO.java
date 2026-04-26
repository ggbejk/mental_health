package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WarningListVO {

    private Long warningId;

    private Long studentId;

    private String studentName;

    private String college;

    private String grade;

    private String className;

    private String gender;

    private String sourceType;

    private String riskLevel;

    private BigDecimal riskScore;

    private String riskFeatures;

    private String status;

    private Long counselorId;

    private LocalDateTime createTime;
}