package com.example.mentalhealth.controller;

import com.example.mentalhealth.dto.EmotionDiarySaveDTO;
import com.example.mentalhealth.dto.EmotionDiaryVO;
import com.example.mentalhealth.dto.EmotionTrendPointVO;
import com.example.mentalhealth.entity.EmotionDiary;
import com.example.mentalhealth.entity.WarningRecord;
import com.example.mentalhealth.repository.EmotionDiaryRepository;
import com.example.mentalhealth.repository.WarningRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/student/emotion")
public class EmotionDiaryController {

    @Autowired
    private EmotionDiaryRepository emotionDiaryRepository;

    @Autowired
    private WarningRecordRepository warningRecordRepository;

    /**
     * 1. 保存情绪日记
     */
    @PostMapping("/save")
    public String saveDiary(@RequestBody EmotionDiarySaveDTO dto) {
        if (dto.getStudentId() == null || dto.getMoodScore() == null) {
            throw new RuntimeException("参数不完整");
        }

        EmotionDiary diary = new EmotionDiary();
        diary.setStudentId(dto.getStudentId());
        diary.setMoodScore(dto.getMoodScore());
        diary.setMoodLabel(dto.getMoodLabel());
        diary.setDiaryText(dto.getDiaryText());
        diary.setTriggerEvent(dto.getTriggerEvent());
        diary.setCreateTime(LocalDateTime.now());

        EmotionDiary savedDiary = emotionDiaryRepository.save(diary);

        checkAndCreateWarning(savedDiary);

        return "保存成功";
    }

    /**
     * 2. 查询学生日记列表
     */
    @GetMapping("/list")
    public List<EmotionDiaryVO> getDiaryList(@RequestParam Long studentId) {
        List<EmotionDiary> list = emotionDiaryRepository.findByStudentIdOrderByCreateTimeDesc(studentId);
        List<EmotionDiaryVO> result = new ArrayList<>();

        for (EmotionDiary item : list) {
            result.add(new EmotionDiaryVO(
                    item.getId(),
                    item.getStudentId(),
                    item.getMoodScore(),
                    item.getMoodLabel(),
                    item.getDiaryText(),
                    item.getTriggerEvent(),
                    item.getCreateTime()
            ));
        }

        return result;
    }

    /**
     * 3. 查询近7天情绪趋势
     */
    @GetMapping("/trend")
    public List<EmotionTrendPointVO> getTrend(@RequestParam Long studentId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusDays(6).atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        List<EmotionDiary> list = emotionDiaryRepository
                .findByStudentIdAndCreateTimeBetweenOrderByCreateTimeAsc(studentId, start, end);

        List<EmotionTrendPointVO> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (EmotionDiary item : list) {
            result.add(new EmotionTrendPointVO(
                    item.getCreateTime().format(formatter),
                    item.getMoodScore()
            ));
        }

        return result;
    }

    /**
     * 自动预警识别
     */
    private void checkAndCreateWarning(EmotionDiary diary) {
        String combinedText = (diary.getDiaryText() == null ? "" : diary.getDiaryText())
                + " "
                + (diary.getTriggerEvent() == null ? "" : diary.getTriggerEvent());

        // 规则1：高风险词直接红色预警
        if (containsHighRiskKeyword(combinedText)) {
            Optional<WarningRecord> existing = warningRecordRepository
                    .findByStudentIdAndSourceTypeAndSourceRefId(
                            diary.getStudentId(),
                            "情绪日记",
                            diary.getId()
                    );

            if (existing.isEmpty()) {
                WarningRecord warning = new WarningRecord();
                warning.setStudentId(diary.getStudentId());
                warning.setSourceType("情绪日记");
                warning.setSourceRefId(diary.getId());
                warning.setRiskLevel("RED");
                warning.setRiskScore(BigDecimal.valueOf(90));
                warning.setRiskFeatures("情绪日记中出现高风险关键词");
                warning.setStatus("UNCLAIMED");
                warning.setHandlerId(null);
                warning.setCreateTime(LocalDateTime.now());
                warningRecordRepository.save(warning);
            }
            return;
        }

        // 规则2：最近3条低情绪趋势预警
        List<EmotionDiary> recentList = emotionDiaryRepository.findTop3ByStudentIdOrderByCreateTimeDesc(diary.getStudentId());
        long lowCount = recentList.stream()
                .filter(item -> item.getMoodScore() != null && item.getMoodScore() <= 2)
                .count();

        if (recentList.size() >= 3 && lowCount >= 2) {
            Optional<WarningRecord> existing = warningRecordRepository
                    .findByStudentIdAndSourceTypeAndSourceRefId(
                            diary.getStudentId(),
                            "情绪趋势",
                            diary.getId()
                    );

            if (existing.isEmpty()) {
                WarningRecord warning = new WarningRecord();
                warning.setStudentId(diary.getStudentId());
                warning.setSourceType("情绪趋势");
                warning.setSourceRefId(diary.getId());

                if (lowCount == 3) {
                    warning.setRiskLevel("ORANGE");
                    warning.setRiskScore(BigDecimal.valueOf(75));
                    warning.setRiskFeatures("最近3次情绪记录均偏低，存在持续性情绪低落风险");
                } else {
                    warning.setRiskLevel("YELLOW");
                    warning.setRiskScore(BigDecimal.valueOf(60));
                    warning.setRiskFeatures("最近3次情绪记录中有2次偏低，建议关注近期情绪波动");
                }

                warning.setStatus("UNCLAIMED");
                warning.setHandlerId(null);
                warning.setCreateTime(LocalDateTime.now());
                warningRecordRepository.save(warning);
            }
        }
    }

    /**
     * 高风险关键词识别
     */
    private boolean containsHighRiskKeyword(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        String[] keywords = {
                "不想活",
                "活着没意义",
                "想死",
                "轻生",
                "自残",
                "崩溃",
                "绝望",
                "结束自己"
        };

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}