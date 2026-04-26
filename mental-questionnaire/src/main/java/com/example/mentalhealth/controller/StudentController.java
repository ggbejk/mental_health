package com.example.mentalhealth.controller;

import com.example.mentalhealth.dto.DimensionScoreVO;
import com.example.mentalhealth.dto.SurveyAnswerItemDTO;
import com.example.mentalhealth.dto.SurveyResultDetailVO;
import com.example.mentalhealth.dto.SurveyResultVO;
import com.example.mentalhealth.dto.SurveySubmitDTO;
import com.example.mentalhealth.entity.ScaleQuestion;
import com.example.mentalhealth.entity.SurveyAnswer;
import com.example.mentalhealth.entity.SurveyResult;
import com.example.mentalhealth.entity.SurveyTask;
import com.example.mentalhealth.dto.StudentTaskVO;
import com.example.mentalhealth.repository.ScaleQuestionRepository;
import com.example.mentalhealth.repository.SurveyAnswerRepository;
import com.example.mentalhealth.repository.SurveyResultRepository;
import com.example.mentalhealth.repository.SurveyTaskRepository;
import com.example.mentalhealth.repository.SysUserRepository;
import com.example.mentalhealth.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private SurveyTaskRepository surveyTaskRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private ScaleQuestionRepository scaleQuestionRepository;

    @Autowired
    private SurveyAnswerRepository surveyAnswerRepository;

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    /**
     * 1. 获取测评任务列表（学生视角）
     */
    @GetMapping("/tasks")
    public List<StudentTaskVO> getTasks(@RequestParam Long studentId) {
        List<SurveyTask> taskList = surveyTaskRepository.findByStatus(1);
        List<StudentTaskVO> result = new ArrayList<>();

        for (SurveyTask task : taskList) {
            Optional<SurveyResult> surveyResultOpt =
                    surveyResultRepository.findByTaskIdAndStudentId(task.getId(), studentId);

            boolean hasSubmitted = surveyResultOpt.isPresent();
            Long resultId = hasSubmitted ? surveyResultOpt.get().getId() : null;
            LocalDateTime submitTime = hasSubmitted ? surveyResultOpt.get().getCreateTime() : null;

            result.add(new StudentTaskVO(
                    task.getId(),
                    task.getTitle(),
                    task.getTargetRange(),
                    task.getStartTime(),
                    task.getEndTime(),
                    task.getStatus(),
                    hasSubmitted,
                    resultId,
                    submitTime
            ));
        }

        return result;
    }
    /**
     * 2. 获取某个任务对应的题目
     */
    @GetMapping("/task/{taskId}/questions")
    public List<ScaleQuestion> getQuestions(@PathVariable Long taskId) {
        SurveyTask task = surveyTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("测评任务不存在"));

        return scaleQuestionRepository.findByScaleIdOrderByQuestionOrderAsc(task.getScaleId());
    }

    /**
     * 3. 提交测评答案并生成结果
     */
    @PostMapping("/task/submit")
    public SurveyResultVO submitSurvey(@RequestBody SurveySubmitDTO dto) {

        if (dto.getTaskId() == null || dto.getStudentId() == null || dto.getAnswers() == null || dto.getAnswers().isEmpty()) {
            throw new RuntimeException("提交参数不完整");
        }

      /*  Optional<SurveyResult> existingResult = surveyResultRepository.findByTaskIdAndStudentId(dto.getTaskId(), dto.getStudentId());
        if (existingResult.isPresent()) {
            throw new RuntimeException("该学生已提交过该测评任务");
        }
*/
        int total = 0;

        for (SurveyAnswerItemDTO item : dto.getAnswers()) {
            if (item.getAnswerScore() == null) {
                throw new RuntimeException("答案分数不能为空，questionId=" + item.getQuestionId());
            }
            SurveyAnswer answer = new SurveyAnswer();
            answer.setTaskId(dto.getTaskId());
            answer.setStudentId(dto.getStudentId());
            answer.setQuestionId(item.getQuestionId());
            answer.setAnswerScore(item.getAnswerScore());
            answer.setCreateTime(LocalDateTime.now());
            surveyAnswerRepository.save(answer);

            ScaleQuestion question = scaleQuestionRepository.findById(item.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("题目不存在，questionId=" + item.getQuestionId()));

            int finalScore;
            if (question.getIsReverse() != null && question.getIsReverse() == 1) {
                finalScore = 6 - item.getAnswerScore();
            } else {
                finalScore = item.getAnswerScore();
            }

            total += finalScore;
        }

        String riskLevel;
        String reportText;

        if (total <= 24) {
            riskLevel = "NORMAL";
            reportText = "当前整体心理状态较稳定，请继续保持良好的作息和学习节奏。";
        } else if (total <= 36) {
            riskLevel = "YELLOW";
            reportText = "你近期存在一定心理压力，建议通过运动、倾诉、规律作息等方式进行自我调节。";
        } else if (total <= 48) {
            riskLevel = "ORANGE";
            reportText = "你近期心理压力较明显，建议尽快与辅导员、朋友或心理老师沟通。";
        } else {
            riskLevel = "RED";
            reportText = "系统识别到你当前可能存在较高心理风险，建议尽快联系学校心理中心寻求专业帮助。";
        }

        SurveyResult result = new SurveyResult();
        result.setTaskId(dto.getTaskId());
        result.setStudentId(dto.getStudentId());
        result.setTotalScore(BigDecimal.valueOf(total));
        result.setRiskLevel(riskLevel);
        result.setReportText(reportText);
        result.setCreateTime(LocalDateTime.now());

        SurveyResult savedResult = surveyResultRepository.save(result);

        return new SurveyResultVO(
                savedResult.getId(),
                savedResult.getTaskId(),
                savedResult.getStudentId(),
                savedResult.getTotalScore(),
                savedResult.getRiskLevel(),
                savedResult.getReportText()
        );
    }

    /**
     * 4. 查询某学生某任务的测评结果
     */
    @GetMapping("/result")
    public SurveyResultVO getResult(@RequestParam Long taskId, @RequestParam Long studentId) {
        SurveyResult result = surveyResultRepository.findByTaskIdAndStudentId(taskId, studentId)
                .orElseThrow(() -> new RuntimeException("未找到测评结果"));

        return new SurveyResultVO(
                result.getId(),
                result.getTaskId(),
                result.getStudentId(),
                result.getTotalScore(),
                result.getRiskLevel(),
                result.getReportText()
        );
    }

    /**
     * 5. 查询某学生某任务的结果详情（含维度统计）
     */
    @GetMapping("/result/detail")
    public SurveyResultDetailVO getResultDetail(@RequestParam Long taskId, @RequestParam Long studentId) {
        SurveyResult result = surveyResultRepository.findByTaskIdAndStudentId(taskId, studentId)
                .orElseThrow(() -> new RuntimeException("未找到测评结果"));

        SurveyTask task = surveyTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("测评任务不存在"));

        List<ScaleQuestion> questions = scaleQuestionRepository.findByScaleIdOrderByQuestionOrderAsc(task.getScaleId());
        List<SurveyAnswer> answers = surveyAnswerRepository.findByTaskIdAndStudentId(taskId, studentId);

        Map<Long, Integer> answerMap = new HashMap<>();
        for (SurveyAnswer answer : answers) {
            answerMap.put(answer.getQuestionId(), answer.getAnswerScore());
        }

        Map<String, Integer> dimensionMap = new LinkedHashMap<>();

        for (ScaleQuestion question : questions) {
            Integer rawScore = answerMap.get(question.getId());
            if (rawScore == null) {
                continue;
            }

            int finalScore;
            if (question.getIsReverse() != null && question.getIsReverse() == 1) {
                finalScore = 6 - rawScore;
            } else {
                finalScore = rawScore;
            }

            String dimension = question.getDimension() == null ? "未分类" : question.getDimension();
            dimensionMap.put(dimension, dimensionMap.getOrDefault(dimension, 0) + finalScore);
        }

        List<DimensionScoreVO> dimensionScores = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dimensionMap.entrySet()) {
            dimensionScores.add(new DimensionScoreVO(entry.getKey(), entry.getValue()));
        }

        return new SurveyResultDetailVO(
                result.getTaskId(),
                result.getStudentId(),
                result.getTotalScore(),
                result.getRiskLevel(),
                result.getReportText(),
                dimensionScores
        );
    }
    /**
     * 6. 根据 resultId 查询测评报告基础信息
     */
    @GetMapping("/report")
    public SurveyResultVO getReportByResultId(@RequestParam Long resultId) {
        SurveyResult result = surveyResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("未找到测评结果"));

        return new SurveyResultVO(
                result.getId(),
                result.getTaskId(),
                result.getStudentId(),
                result.getTotalScore(),
                result.getRiskLevel(),
                result.getReportText()
        );
    }
    /**
     * 7. 根据 resultId 查询结果详情（含维度统计）
     */
    @GetMapping("/report/detail/by-result-id")
    public SurveyResultDetailVO getReportDetailByResultId(@RequestParam Long resultId) {
        SurveyResult result = surveyResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("未找到测评结果"));

        Long taskId = result.getTaskId();
        Long studentId = result.getStudentId();

        SurveyTask task = surveyTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("测评任务不存在"));

        List<ScaleQuestion> questions = scaleQuestionRepository.findByScaleIdOrderByQuestionOrderAsc(task.getScaleId());
        List<SurveyAnswer> answers = surveyAnswerRepository.findByTaskIdAndStudentId(taskId, studentId);

        Map<Long, Integer> answerMap = new HashMap<>();
        for (SurveyAnswer answer : answers) {
            answerMap.put(answer.getQuestionId(), answer.getAnswerScore());
        }

        Map<String, Integer> dimensionMap = new LinkedHashMap<>();

        for (ScaleQuestion question : questions) {
            Integer rawScore = answerMap.get(question.getId());
            if (rawScore == null) {
                continue;
            }

            int finalScore;
            if (question.getIsReverse() != null && question.getIsReverse() == 1) {
                finalScore = 6 - rawScore;
            } else {
                finalScore = rawScore;
            }

            String dimension = question.getDimension() == null ? "未分类" : question.getDimension();
            dimensionMap.put(dimension, dimensionMap.getOrDefault(dimension, 0) + finalScore);
        }

        List<DimensionScoreVO> dimensionScores = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dimensionMap.entrySet()) {
            dimensionScores.add(new DimensionScoreVO(entry.getKey(), entry.getValue()));
        }

        return new SurveyResultDetailVO(
                result.getTaskId(),
                result.getStudentId(),
                result.getTotalScore(),
                result.getRiskLevel(),
                result.getReportText(),
                dimensionScores
        );
    }

    @GetMapping("/{studentId}")
    public SysUser getStudentDetail(@PathVariable Long studentId) {
        return sysUserRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }
}