package com.example.mentalhealth.controller;

import com.example.mentalhealth.dto.OperationLogVO;
import com.example.mentalhealth.entity.OperationLog;
import com.example.mentalhealth.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/operation-log")
public class OperationLogController {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @GetMapping("/list")
    public Map<String, Object> getOperationLogs(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String operationModule,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OperationLog> logPage = operationLogRepository.searchLogs(
                userName, operationModule, startTime, endTime, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("total", logPage.getTotalElements());
        result.put("totalPages", logPage.getTotalPages());
        result.put("current", page + 1);
        result.put("records", logPage.getContent().stream().map(log -> new OperationLogVO(
                log.getId(),
                log.getUserId(),
                log.getUserName(),
                log.getUserRole(),
                log.getOperationType(),
                log.getOperationModule(),
                log.getOperationDesc(),
                log.getRequestMethod(),
                log.getRequestUrl(),
                log.getOperationStatus(),
                log.getCreateTime()
        )).toList());

        return result;
    }

    @GetMapping("/modules")
    public Map<String, String> getOperationModules() {
        Map<String, String> modules = new HashMap<>();
        modules.put("学生管理", "学生管理");
        modules.put("预警管理", "预警管理");
        modules.put("干预管理", "干预管理");
        modules.put("咨询预约", "咨询预约");
        modules.put("心理普查", "心理普查");
        modules.put("量表管理", "量表管理");
        modules.put("家长联系", "家长联系");
        modules.put("倾诉记录", "倾诉记录");
        modules.put("情绪日记", "情绪日记");
        modules.put("AI聊天", "AI聊天");
        modules.put("系统设置", "系统设置");
        return modules;
    }

    @GetMapping("/types")
    public Map<String, String> getOperationTypes() {
        Map<String, String> types = new HashMap<>();
        types.put("新增", "新增");
        types.put("修改", "修改");
        types.put("删除", "删除");
        types.put("查询", "查询");
        types.put("认领", "认领");
        types.put("干预", "干预");
        types.put("结案", "结案");
        types.put("预约", "预约");
        types.put("取消", "取消");
        types.put("提交", "提交");
        types.put("登录", "登录");
        types.put("登出", "登出");
        return types;
    }
}
