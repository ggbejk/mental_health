package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "emotion_diary")
@Data
public class EmotionDiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "mood_score")
    private Integer moodScore;

    @Column(name = "mood_label")
    private String moodLabel;

    @Column(name = "diary_text")
    private String diaryText;

    @Column(name = "trigger_event")
    private String triggerEvent;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}