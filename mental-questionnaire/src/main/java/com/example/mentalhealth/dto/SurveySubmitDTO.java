package com.example.mentalhealth.dto;

import lombok.Data;

import java.util.List;

@Data
public class SurveySubmitDTO {

    private Long taskId;

    private Long studentId;

    private List<SurveyAnswerItemDTO> answers;
}