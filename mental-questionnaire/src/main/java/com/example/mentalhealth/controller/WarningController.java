package com.example.mentalhealth.controller;

import com.example.mentalhealth.entity.WarningRecord;
import com.example.mentalhealth.repository.WarningRecordRepository;
import com.example.mentalhealth.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/warning")
public class WarningController {

    @Autowired
    private WarningRecordRepository warningRecordRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    /**
     * 1. 获取预警列表
     */
    @GetMapping("/list")
    public List<WarningRecord> getWarningList() {
        return warningRecordRepository.findAllByOrderByCreateTimeDesc();
    }

    /**
     * 2. 获取辅导员预警列表
     */
    @GetMapping("/counselor/list")
    public List<WarningRecord> getCounselorWarningList(@RequestParam Long counselorId) {
        // 这里需要根据辅导员负责的班级或学生来筛选预警记录
        // 暂时返回所有预警记录
        return warningRecordRepository.findAllByOrderByCreateTimeDesc();
    }

    /**
     * 3. 认领预警
     */
    @PostMapping("/claim")
    public String claimWarning(@RequestBody Map<String, Object> params) {
        Long warningId = Long.parseLong(params.get("warningId").toString());
        Long counselorId = Long.parseLong(params.get("counselorId").toString());

        WarningRecord warning = warningRecordRepository.findById(warningId).orElse(null);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在");
        }

        warning.setStatus("CLAIMED");
        warning.setHandlerId(counselorId);
        warning.setUpdateTime(LocalDateTime.now());
        warningRecordRepository.save(warning);

        return "认领成功";
    }

    /**
     * 4. 处理预警
     */
    @PostMapping("/process")
    public String processWarning(@RequestBody Map<String, Object> params) {
        Long warningId = Long.parseLong(params.get("warningId").toString());
        String processContent = params.get("processContent").toString();

        WarningRecord warning = warningRecordRepository.findById(warningId).orElse(null);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在");
        }

        warning.setStatus("PROCESSING");
        warning.setProcessContent(processContent);
        warning.setUpdateTime(LocalDateTime.now());
        warningRecordRepository.save(warning);

        return "处理成功";
    }

    /**
     * 5. 关闭预警
     */
    @PostMapping("/close")
    public String closeWarning(@RequestBody Map<String, Object> params) {
        Long warningId = Long.parseLong(params.get("warningId").toString());
        String closeReason = params.get("closeReason").toString();

        WarningRecord warning = warningRecordRepository.findById(warningId).orElse(null);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在");
        }

        warning.setStatus("CLOSED");
        warning.setCloseReason(closeReason);
        warning.setUpdateTime(LocalDateTime.now());
        warningRecordRepository.save(warning);

        return "关闭成功";
    }

    /**
     * 6. 获取预警详情
     */
    @GetMapping("/detail")
    public WarningRecord getWarningDetail(@RequestParam Long id) {
        return warningRecordRepository.findById(id).orElse(null);
    }

    /**
     * 7. 生成预警
     */
    @PostMapping("/generate")
    public String generateWarning(@RequestBody Map<String, Object> params) {
        Long studentId = Long.parseLong(params.get("studentId").toString());
        String riskLevel = params.get("riskLevel").toString();
        String riskFeatures = params.get("riskFeatures").toString();

        WarningRecord warning = new WarningRecord();
        warning.setStudentId(studentId);
        warning.setRiskLevel(riskLevel);
        warning.setRiskFeatures(riskFeatures);
        warning.setStatus("UNCLAIMED");
        warning.setCreateTime(LocalDateTime.now());
        warning.setUpdateTime(LocalDateTime.now());

        warningRecordRepository.save(warning);

        return "预警生成成功";
    }
}
