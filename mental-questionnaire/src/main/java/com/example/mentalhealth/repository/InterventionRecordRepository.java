package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.InterventionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterventionRecordRepository extends JpaRepository<InterventionRecord, Long> {

    List<InterventionRecord> findByWarningIdOrderByCreateTimeDesc(Long warningId);

    List<InterventionRecord> findByCounselorIdOrderByCreateTimeDesc(Long counselorId);

    List<InterventionRecord> findByStudentIdOrderByCreateTimeDesc(Long studentId);

    List<InterventionRecord> findByStudentIdIn(List<Long> studentIds);
}