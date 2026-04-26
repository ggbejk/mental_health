package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "result")
@Data
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "total_score")
    private Integer totalScore;

    private String level;
}