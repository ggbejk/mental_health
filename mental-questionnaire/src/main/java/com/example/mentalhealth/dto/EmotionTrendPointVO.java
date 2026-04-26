package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmotionTrendPointVO {

    private String date;

    private Integer moodScore;
}