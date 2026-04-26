package com.example.mentalhealth.controller;

import com.example.mentalhealth.dto.ConfessionSaveDTO;
import com.example.mentalhealth.dto.ConfessionVO;
import com.example.mentalhealth.entity.ConfessionRecord;
import com.example.mentalhealth.entity.WarningRecord;
import com.example.mentalhealth.repository.ConfessionRecordRepository;
import com.example.mentalhealth.repository.WarningRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/student/confession")
public class ConfessionController {

    @Autowired
    private ConfessionRecordRepository confessionRecordRepository;

    @Autowired
    private WarningRecordRepository warningRecordRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/voice/";

    /**
     * 1. 提交匿名倾诉（文字）
     */
    @PostMapping("/save")
    public String saveConfession(@RequestBody ConfessionSaveDTO dto) {
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new RuntimeException("倾诉内容不能为空");
        }

        ConfessionRecord record = new ConfessionRecord();
        record.setStudentId(dto.getStudentId());
        record.setContent(dto.getContent());
        record.setEmotionTag(dto.getEmotionTag());
        record.setIsAnonymous(dto.getIsAnonymous() == null ? 1 : dto.getIsAnonymous());
        record.setCreateTime(LocalDateTime.now());

        ConfessionRecord saved = confessionRecordRepository.save(record);

        checkAndCreateWarning(saved);

        return "提交成功";
    }

    /**
     * 2. 提交语音倾诉
     */
    @PostMapping("/voice/save")
    public String saveVoiceConfession(
            @RequestParam Long studentId,
            @RequestParam String emotionTag,
            @RequestParam(required = false, defaultValue = "1") Integer isAnonymous,
            @RequestParam("audioFile") MultipartFile audioFile) {

        if (audioFile == null || audioFile.isEmpty()) {
            throw new RuntimeException("语音文件不能为空");
        }

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalFilename = audioFile.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".webm";
            String fileName = UUID.randomUUID().toString() + extension;
            String filePath = UPLOAD_DIR + fileName;

            audioFile.transferTo(new File(filePath));

            ConfessionRecord record = new ConfessionRecord();
            record.setStudentId(studentId);
            record.setContent("[语音倾诉]" + fileName);
            record.setEmotionTag(emotionTag);
            record.setIsAnonymous(isAnonymous);
            record.setVoicePath(fileName);
            record.setCreateTime(LocalDateTime.now());

            ConfessionRecord saved = confessionRecordRepository.save(record);

            checkAndCreateWarningForVoice(saved, audioFile);

            return "提交成功";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("语音倾诉保存失败: " + e.getMessage());
        }
    }

    /**
     * 3. 查询我的倾诉记录
     */
    @GetMapping("/my")
    public List<ConfessionVO> myConfessions(@RequestParam Long studentId) {
        List<ConfessionRecord> list = confessionRecordRepository.findByStudentIdOrderByCreateTimeDesc(studentId);
        List<ConfessionVO> result = new ArrayList<>();

        for (ConfessionRecord item : list) {
            result.add(new ConfessionVO(
                    item.getId(),
                    item.getStudentId(),
                    item.getContent(),
                    item.getEmotionTag(),
                    item.getIsAnonymous(),
                    item.getCreateTime(),
                    item.getVoicePath()
            ));
        }

        return result;
    }

    /**
     * 自动预警
     */
    private void checkAndCreateWarning(ConfessionRecord record) {
        String text = record.getContent() == null ? "" : record.getContent();

        String riskLevel = null;
        BigDecimal riskScore = null;
        String riskFeatures = null;

        if (containsHighRiskKeyword(text)) {
            riskLevel = "RED";
            riskScore = BigDecimal.valueOf(95);
            riskFeatures = "匿名倾诉内容中出现高风险关键词";
        } else if (containsMediumRiskKeyword(text)) {
            riskLevel = "ORANGE";
            riskScore = BigDecimal.valueOf(78);
            riskFeatures = "匿名倾诉内容中出现明显负面风险表达";
        } else if (containsLowRiskKeyword(text)) {
            riskLevel = "YELLOW";
            riskScore = BigDecimal.valueOf(62);
            riskFeatures = "匿名倾诉内容中出现持续压力或情绪困扰表达";
        }

        if (riskLevel == null) {
            return;
        }

        Optional<WarningRecord> existing = warningRecordRepository
                .findByStudentIdAndSourceTypeAndSourceRefId(
                        record.getStudentId(),
                        "匿名倾诉",
                        record.getId()
                );

        if (existing.isPresent()) {
            return;
        }

        WarningRecord warning = new WarningRecord();
        warning.setStudentId(record.getStudentId());
        warning.setSourceType("匿名倾诉");
        warning.setSourceRefId(record.getId());
        warning.setRiskLevel(riskLevel);
        warning.setRiskScore(riskScore);
        warning.setRiskFeatures(riskFeatures);
        warning.setStatus("UNCLAIMED");
        warning.setHandlerId(null);
        warning.setCreateTime(LocalDateTime.now());

        warningRecordRepository.save(warning);
    }

    private boolean containsHighRiskKeyword(String text) {
        String[] keywords = {
                "不想活",
                "活着没意义",
                "想死",
                "轻生",
                "自杀",
                "结束自己",
                "自残",
                "跳楼",
                "割腕",
                "不如死了"
        };

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsMediumRiskKeyword(String text) {
        String[] keywords = {
                "崩溃",
                "绝望",
                "撑不下去",
                "没有希望",
                "活得好累",
                "很痛苦",
                "想消失",
                "每天都睡不着"
        };

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLowRiskKeyword(String text) {
        String[] keywords = {
                "压力很大",
                "焦虑",
                "烦",
                "失眠",
                "难过",
                "压抑",
                "不开心",
                "没人理解我",
                "很累"
        };

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private void checkAndCreateWarningForVoice(ConfessionRecord record, MultipartFile audioFile) {
        String emotionTag = record.getEmotionTag();

        String riskLevel = null;
        BigDecimal riskScore = null;
        String riskFeatures = null;

        if ("情绪低落".equals(emotionTag) || "焦虑紧张".equals(emotionTag)) {
            riskLevel = "YELLOW";
            riskScore = BigDecimal.valueOf(65);
            riskFeatures = "语音倾诉情绪标签为" + emotionTag + "，建议关注";
        } else if ("学习压力".equals(emotionTag) || "睡眠问题".equals(emotionTag)) {
            riskLevel = "YELLOW";
            riskScore = BigDecimal.valueOf(55);
            riskFeatures = "语音倾诉涉及" + emotionTag + "问题";
        } else if ("家庭困扰".equals(emotionTag) || "人际关系".equals(emotionTag)) {
            riskLevel = "ORANGE";
            riskScore = BigDecimal.valueOf(75);
            riskFeatures = "语音倾诉涉及" + emotionTag + "问题";
        }

        if (riskLevel == null) {
            return;
        }

        Optional<WarningRecord> existing = warningRecordRepository
                .findByStudentIdAndSourceTypeAndSourceRefId(
                        record.getStudentId(),
                        "语音倾诉",
                        record.getId()
                );

        if (existing.isPresent()) {
            return;
        }

        WarningRecord warning = new WarningRecord();
        warning.setStudentId(record.getStudentId());
        warning.setSourceType("语音倾诉");
        warning.setSourceRefId(record.getId());
        warning.setRiskLevel(riskLevel);
        warning.setRiskScore(riskScore);
        warning.setRiskFeatures(riskFeatures);
        warning.setStatus("UNCLAIMED");
        warning.setHandlerId(null);
        warning.setCreateTime(LocalDateTime.now());

        warningRecordRepository.save(warning);
    }
}