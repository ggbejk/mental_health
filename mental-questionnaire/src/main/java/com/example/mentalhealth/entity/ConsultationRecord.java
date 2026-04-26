package com.example.mentalhealth.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "consultation_record")
public class ConsultationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    private Long counselorId;

    private String studentName;

    private String counselorName;

    private String consultationType;

    private LocalDateTime appointmentTime;

    private String status; // PENDING, SCHEDULED, COMPLETED, CANCELLED

    private String notes;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
