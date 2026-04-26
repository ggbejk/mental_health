package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "intervention_record")
@Data
public class InterventionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warning_id")
    private Long warningId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "counselor_id")
    private Long counselorId;

    private String content;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "follow_up_time")
    private LocalDateTime followUpTime;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}