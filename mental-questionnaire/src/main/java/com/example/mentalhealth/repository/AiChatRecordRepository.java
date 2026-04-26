package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.AiChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiChatRecordRepository extends JpaRepository<AiChatRecord, Long> {

    List<AiChatRecord> findByStudentIdOrderByCreateTimeAsc(Long studentId);

    List<AiChatRecord> findTop50ByStudentIdOrderByCreateTimeDesc(Long studentId);
}
