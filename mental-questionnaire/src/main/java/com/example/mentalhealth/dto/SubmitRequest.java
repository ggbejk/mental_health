package com.example.mentalhealth.dto;

import com.example.mentalhealth.entity.UserInfo;
import lombok.Data;

import java.util.List;

@Data
public class SubmitRequest {
    private UserInfo userInfo;
    private List<AnswerDTO> answers;
}