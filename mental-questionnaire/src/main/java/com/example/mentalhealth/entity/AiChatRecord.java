package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_chat_record")
@Data
public class AiChatRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    private String content;

    @Column(name = "emotion_tag")
    private String emotionTag;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
