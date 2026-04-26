package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class SurveyResultDetailVO {

    private Long taskId;

    private Long studentId;

    private BigDecimal totalScore;

    private String riskLevel;

    private String reportText;

    private List<DimensionScoreVO> dimensionScores;
}