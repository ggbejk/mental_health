package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EmotionDiaryVO {

    private Long id;

    private Long studentId;

    private Integer moodScore;

    private String moodLabel;

    private String diaryText;

    private String triggerEvent;

    private LocalDateTime createTime;
}