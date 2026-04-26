package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "warning_record")
public class WarningRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "risk_score")
    private BigDecimal riskScore;

    @Column(name = "risk_features")
    private String riskFeatures;

    @Column(name = "status")
    private String status;

    @Column(name = "handler_id")
    private Long handlerId;

    @Column(name = "process_content")
    private String processContent;

    @Column(name = "close_reason")
    private String closeReason;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "source_ref_id")
    private Long sourceRefId;

    @Column(name = "warning_date")
    private LocalDateTime warningDate;

    @Transient
    private String riskTags;
}