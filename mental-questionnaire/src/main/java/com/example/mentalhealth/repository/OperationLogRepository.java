package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    Page<OperationLog> findByCreateTimeBetweenOrderByCreateTimeDesc(
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    Page<OperationLog> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    Page<OperationLog> findByOperationModuleOrderByCreateTimeDesc(String module, Pageable pageable);

    @Query("SELECT o FROM OperationLog o WHERE " +
           "(:userName IS NULL OR o.userName LIKE %:userName%) AND " +
           "(:operationModule IS NULL OR o.operationModule = :operationModule) AND " +
           "(:startTime IS NULL OR o.createTime >= :startTime) AND " +
           "(:endTime IS NULL OR o.createTime <= :endTime)")
    Page<OperationLog> searchLogs(
            @Param("userName") String userName,
            @Param("operationModule") String operationModule,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
}
