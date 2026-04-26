package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "operation_log")
@Data
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_role")
    private String userRole;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "operation_module")
    private String operationModule;

    @Column(name = "operation_desc")
    private String operationDesc;

    @Column(name = "request_method")
    private String requestMethod;

    @Column(name = "request_url")
    private String requestUrl;

    @Column(name = "request_params")
    private String requestParams;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "operation_status")
    private String operationStatus;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
