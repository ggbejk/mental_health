package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SurveyReportVO {

    private Long resultId;

    private Long studentId;

    private String studentName;

    private String scaleName;

    private Integer totalScore;

    private String riskLevel;

    private String summaryText;

    private String suggestionText;

    private LocalDateTime submitTime;

    private List<RadarIndicatorVO> radarIndicators;

    private List<Integer> radarValues;
}