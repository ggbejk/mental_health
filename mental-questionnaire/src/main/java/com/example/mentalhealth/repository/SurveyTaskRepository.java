package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.SurveyTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyTaskRepository extends JpaRepository<SurveyTask, Long> {
    List<SurveyTask> findAllByOrderByCreateTimeDesc();

    List<SurveyTask> findByStatus(Integer status);
}
