package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OperationLogVO {

    private Long id;

    private Long userId;

    private String userName;

    private String userRole;

    private String operationType;

    private String operationModule;

    private String operationDesc;

    private String requestMethod;

    private String requestUrl;

    private String operationStatus;

    private LocalDateTime createTime;
}
