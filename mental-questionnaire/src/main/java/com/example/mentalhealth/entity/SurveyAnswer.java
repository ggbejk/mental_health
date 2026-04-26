package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_answer")
@Data
public class SurveyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "answer_score")
    private Integer answerScore;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}