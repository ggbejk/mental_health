package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimensionScoreVO {

    private String dimension;

    private Integer score;
}