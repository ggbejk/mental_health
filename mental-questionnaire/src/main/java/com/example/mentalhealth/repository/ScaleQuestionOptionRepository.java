package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.ScaleQuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScaleQuestionOptionRepository extends JpaRepository<ScaleQuestionOption, Long> {
    List<ScaleQuestionOption> findByQuestionIdOrderByIdAsc(Long questionId);
}
