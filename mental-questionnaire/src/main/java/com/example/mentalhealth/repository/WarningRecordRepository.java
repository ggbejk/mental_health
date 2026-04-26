package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.WarningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarningRecordRepository extends JpaRepository<WarningRecord, Long> {
    List<WarningRecord> findByStudentId(Long studentId);
    List<WarningRecord> findByHandlerId(Long handlerId);
    List<WarningRecord> findByStatus(String status);
    Optional<WarningRecord> findByStudentIdAndSourceTypeAndSourceRefId(Long studentId, String sourceType, Long sourceRefId);
    List<WarningRecord> findByStudentIdInOrderByCreateTimeDesc(List<Long> studentIds);
    List<WarningRecord> findAllByOrderByCreateTimeDesc();
}