package com.example.mentalhealth.controller;

import com.example.mentalhealth.dto.RadarIndicatorVO;
import com.example.mentalhealth.dto.SurveyReportVO;
import com.example.mentalhealth.entity.SurveyResult;
import com.example.mentalhealth.entity.SysUser;
import com.example.mentalhealth.repository.SurveyResultRepository;
import com.example.mentalhealth.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/student/report")
public class SurveyReportController {

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @GetMapping("/detail")
    public SurveyReportVO getReport(@RequestParam Long resultId) {
        SurveyResult result = surveyResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("测评结果不存在"));

        SysUser student = sysUserRepository.findById(result.getStudentId())
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        int totalScore = result.getTotalScore() == null
                ? 0
                : result.getTotalScore().intValue();
        String riskLevel = getRiskLevel(totalScore);
        String summaryText = getSummaryText(totalScore);
        String suggestionText = getSuggestionText(totalScore);

        List<RadarIndicatorVO> indicators = Arrays.asList(
                new RadarIndicatorVO("情绪状态", 100),
                new RadarIndicatorVO("压力水平", 100),
                new RadarIndicatorVO("睡眠状态", 100),
                new RadarIndicatorVO("人际适应", 100),
                new RadarIndicatorVO("学习动力", 100)
        );

        List<Integer> radarValues = buildRadarValues(totalScore);

        return new SurveyReportVO(
                result.getId(),
                result.getStudentId(),
                student.getRealName(),
                "学生心理健康测评",
                totalScore,
                riskLevel,
                summaryText,
                suggestionText,
                result.getCreateTime(),
                indicators,
                radarValues
        );
    }

    private String getRiskLevel(Integer totalScore) {
        if (totalScore == null) return "正常";
        if (totalScore >= 85) return "高风险";
        if (totalScore >= 65) return "中风险";
        if (totalScore >= 45) return "轻度预警";
        return "正常";
    }

    private String getSummaryText(Integer totalScore) {
        if (totalScore == null) {
            return "当前暂无有效测评结果。";
        }

        if (totalScore >= 85) {
            return "本次测评显示你当前可能存在较明显的心理困扰与风险信号，建议尽快与辅导员或专业心理老师沟通。";
        } else if (totalScore >= 65) {
            return "本次测评显示你近期存在一定程度的情绪压力和适应困扰，建议及时关注作息、压力来源与情绪变化。";
        } else if (totalScore >= 45) {
            return "本次测评显示你近期存在轻度波动，整体可控，但仍建议保持规律作息并适当释放压力。";
        } else {
            return "本次测评结果整体较平稳，你当前心理状态总体良好，请继续保持规律生活与积极支持系统。";
        }
    }

    private String getSuggestionText(Integer totalScore) {
        if (totalScore == null) {
            return "建议完成完整量表后再次查看报告。";
        }

        if (totalScore >= 85) {
            return "建议你尽快主动寻求帮助，包括联系辅导员、预约学校心理咨询，减少独自承受压力的时间；同时尽量保持规律饮食与睡眠，避免长期封闭自己。";
        } else if (totalScore >= 65) {
            return "建议你近期降低过高的学习或生活负荷，给自己安排固定休息时间；可尝试与信任的朋友、老师沟通，必要时预约心理咨询。";
        } else if (totalScore >= 45) {
            return "建议你继续保持日常观察，可通过运动、散步、写情绪日记、和朋友交流等方式缓解压力，并关注睡眠与情绪波动。";
        } else {
            return "建议继续保持当前节奏，规律作息、适量运动、维持稳定社交连接，并持续关注自己的情绪变化。";
        }
    }

    private List<Integer> buildRadarValues(Integer totalScore) {
        int score = totalScore == null ? 0 : totalScore;

        int emotion = Math.min(100, score + 10);
        int pressure = Math.min(100, score + 5);
        int sleep = Math.min(100, Math.max(20, score - 5));
        int relation = Math.min(100, Math.max(20, score - 10));
        int motivation = Math.min(100, Math.max(20, 100 - score / 2));

        return Arrays.asList(
                emotion,
                pressure,
                sleep,
                relation,
                motivation
        );
    }
}