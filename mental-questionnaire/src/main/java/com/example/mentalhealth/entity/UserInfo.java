package com.example.mentalhealth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_info")
@Data
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String gender;
    private String grade;
    private String college;

    @Column(name = "major_type")
    private String majorType;

    @Column(name = "only_child")
    private String onlyChild;
}