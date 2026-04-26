package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.ConfessionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfessionRecordRepository extends JpaRepository<ConfessionRecord, Long> {

    List<ConfessionRecord> findByStudentIdOrderByCreateTimeDesc(Long studentId);

    List<ConfessionRecord> findAllByOrderByCreateTimeDesc();
}