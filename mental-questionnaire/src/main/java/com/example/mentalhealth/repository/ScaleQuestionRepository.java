package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.ScaleQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScaleQuestionRepository extends JpaRepository<ScaleQuestion, Long> {

    List<ScaleQuestion> findByScaleIdOrderByQuestionOrderAsc(Long scaleId);
}