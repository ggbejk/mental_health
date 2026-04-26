package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.ConsultationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRecordRepository extends JpaRepository<ConsultationRecord, Long> {
}
