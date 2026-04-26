package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "scale_question")
@Data
public class ScaleQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scale_id")
    private Long scaleId;

    private String title;

    private String dimension;

    @Column(name = "question_order")
    private Integer questionOrder;

    @Column(name = "is_reverse")
    private Integer isReverse;

    @Column(name = "question_type")
    private String questionType;
}