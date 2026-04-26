package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.EmotionDiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmotionDiaryRepository extends JpaRepository<EmotionDiary, Long> {

    List<EmotionDiary> findByStudentIdOrderByCreateTimeDesc(Long studentId);

    List<EmotionDiary> findByStudentIdAndCreateTimeBetweenOrderByCreateTimeAsc(
            Long studentId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<EmotionDiary> findTop3ByStudentIdOrderByCreateTimeDesc(Long studentId);
}