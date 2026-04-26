package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "parent_contact_record")
@Data
public class ParentContactRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "counselor_id")
    private Long counselorId;

    @Column(name = "contact_time")
    private LocalDateTime contactTime;

    @Column(name = "contact_method")
    private String contactMethod;

    @Column(name = "contact_purpose")
    private String contactPurpose;

    @Column(name = "contact_content")
    private String contactContent;

    @Column(name = "contact_result")
    private String contactResult;

    @Column(name = "follow_up_plan")
    private String followUpPlan;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
