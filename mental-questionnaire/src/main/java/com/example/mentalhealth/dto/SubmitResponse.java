package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmitResponse {
    private Integer userId;
    private Integer totalScore;
    private String level;
    private String message;
}