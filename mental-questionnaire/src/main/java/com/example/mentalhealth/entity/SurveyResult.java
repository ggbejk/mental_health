package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "survey_result")
public class SurveyResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "total_score")
    private BigDecimal totalScore;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "report_text")
    private String reportText;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}