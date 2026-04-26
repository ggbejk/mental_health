package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SurveyResultVO {

    private Long resultId;

    private Long taskId;

    private Long studentId;

    private BigDecimal totalScore;

    private String riskLevel;

    private String reportText;
}