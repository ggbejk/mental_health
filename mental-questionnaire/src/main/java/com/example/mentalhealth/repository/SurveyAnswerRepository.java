package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

    List<SurveyAnswer> findByTaskIdAndStudentId(Long taskId, Long studentId);
}