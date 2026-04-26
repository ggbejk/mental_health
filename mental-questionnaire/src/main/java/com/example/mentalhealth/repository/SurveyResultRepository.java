package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    long countByTaskId(Long taskId);
    
    Optional<SurveyResult> findByTaskIdAndStudentId(Long taskId, Long studentId);
}