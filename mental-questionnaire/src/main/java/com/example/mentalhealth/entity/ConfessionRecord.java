package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "confession_record")
@Data
public class ConfessionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    private String content;

    @Column(name = "emotion_tag")
    private String emotionTag;

    @Column(name = "is_anonymous")
    private Integer isAnonymous;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "voice_path")
    private String voicePath;
}