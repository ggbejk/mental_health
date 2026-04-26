package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DashboardOverviewVO {

    private Long totalStudents;

    private Long totalSurveyStudents;

    private Long totalWarnings;

    private Long totalInterventions;

    private Double surveyCoverageRate;

    private Double interventionCoverageRate;

    private List<NameValueVO> warningLevelStats;

    private List<NameValueVO> collegeWarningStats;

    private List<NameValueVO> warningStatusStats;

    private List<NameValueVO> warningTrendStats;

    private List<NameValueVO> riskTagStats;

    private List<NameValueVO> genderWarningStats;

    private List<NameValueVO> gradeWarningStats;
}