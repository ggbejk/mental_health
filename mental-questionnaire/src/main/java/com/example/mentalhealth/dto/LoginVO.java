package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {

    private Long userId;

    private String username;

    private String realName;

    private String role;

    private String college;

    private String className;

    private String token;
}