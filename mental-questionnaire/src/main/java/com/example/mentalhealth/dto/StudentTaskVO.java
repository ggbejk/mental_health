package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StudentTaskVO {

    private Long id;
    private String title;
    private String targetRange;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;

    private Boolean hasSubmitted;
    private Long resultId;
    private LocalDateTime submitTime;
}