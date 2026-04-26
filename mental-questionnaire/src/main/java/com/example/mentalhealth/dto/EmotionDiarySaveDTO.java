package com.example.mentalhealth.dto;

import lombok.Data;

@Data
public class EmotionDiarySaveDTO {

    private Long studentId;

    private Integer moodScore;

    private String moodLabel;

    private String diaryText;

    private String triggerEvent;
}