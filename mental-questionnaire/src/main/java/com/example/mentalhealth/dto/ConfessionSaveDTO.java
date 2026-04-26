package com.example.mentalhealth.dto;

import lombok.Data;

@Data
public class ConfessionSaveDTO {

    private Long studentId;

    private String content;

    private String emotionTag;

    private Integer isAnonymous;
}