package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.ParentContactRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentContactRecordRepository extends JpaRepository<ParentContactRecord, Long> {

    List<ParentContactRecord> findByStudentIdOrderByCreateTimeDesc(Long studentId);

    List<ParentContactRecord> findByCounselorIdOrderByCreateTimeDesc(Long counselorId);
}
