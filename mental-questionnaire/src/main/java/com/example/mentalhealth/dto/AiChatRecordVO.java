package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AiChatRecordVO {

    private Long id;

    private Long studentId;

    private String content;

    private String emotionTag;

    private String sourceType;

    private LocalDateTime createTime;
}
