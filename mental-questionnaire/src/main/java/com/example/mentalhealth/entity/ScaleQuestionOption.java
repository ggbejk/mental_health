package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "scale_question_option")
@Data
public class ScaleQuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id")
    private Long questionId;

    private String optionText;

    private Integer score;
}
