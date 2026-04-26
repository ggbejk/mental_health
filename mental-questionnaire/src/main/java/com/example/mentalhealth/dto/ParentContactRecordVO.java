package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ParentContactRecordVO {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long counselorId;

    private String counselorName;

    private LocalDateTime contactTime;

    private String contactMethod;

    private String contactPurpose;

    private String contactContent;

    private String contactResult;

    private String followUpPlan;

    private LocalDateTime createTime;
}
