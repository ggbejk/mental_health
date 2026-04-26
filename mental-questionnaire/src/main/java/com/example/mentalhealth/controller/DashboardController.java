package com.example.mentalhealth.controller;

import com.example.mentalhealth.entity.*;
import com.example.mentalhealth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private SurveyTaskRepository surveyTaskRepository;

    @Autowired
    private SurveyAnswerRepository surveyAnswerRepository;

    @Autowired
    private InterventionRecordRepository interventionRecordRepository;

    @Autowired
    private WarningRecordRepository warningRecordRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @GetMapping(value = "/overview", produces = "application/json;charset=UTF-8")
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        long totalStudents = sysUserRepository.countByRole("STUDENT");
        result.put("totalStudents", totalStudents);

        long totalSurveyStudents = surveyResultRepository.findAll().stream()
                .map(r -> r.getStudentId())
                .distinct()
                .count();
        result.put("totalSurveyStudents", totalSurveyStudents);

        double coverageRate = totalStudents > 0 ?
                Math.round((double) totalSurveyStudents / totalStudents * 10000.0) / 100.0 : 0;
        result.put("surveyCoverageRate", coverageRate);

        List<SurveyResult> allResults = surveyResultRepository.findAll();
        long totalWarnings = allResults.stream()
                .filter(r -> r.getRiskLevel() != null &&
                        (r.getRiskLevel().equals("YELLOW") ||
                                r.getRiskLevel().equals("ORANGE") ||
                                r.getRiskLevel().equals("RED")))
                .count();
        result.put("totalWarnings", totalWarnings);

        long totalInterventions = interventionRecordRepository.count();
        result.put("totalInterventions", totalInterventions);

        double interventionCoverage = totalWarnings > 0 ?
                Math.round((double) totalInterventions / totalWarnings * 10000.0) / 100.0 : 0;
        result.put("interventionCoverageRate", interventionCoverage);

        result.put("warningLevelStats", getWarningLevelStats(allResults));
        result.put("warningStatusStats", getWarningStatusStats());
        result.put("warningTrendStats", getWarningTrendStats());
        result.put("riskTagStats", getRiskTagStats());
        result.put("genderWarningStats", getGenderWarningStats());
        result.put("gradeWarningStats", getGradeWarningStats());
        result.put("collegeWarningStats", getCollegeWarningStats());

        return result;
    }

    private List<Map<String, Object>> getWarningLevelStats(List<SurveyResult> results) {
        // 按学生ID去重，只取每个学生的最新测评结果
        Map<Long, SurveyResult> latestResults = new HashMap<>();
        for (SurveyResult r : results) {
            Long studentId = r.getStudentId();
            if (!latestResults.containsKey(studentId)) {
                latestResults.put(studentId, r);
            }
        }

        Map<String, Long> levelCount = latestResults.values().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRiskLevel() == null ? "NORMAL" : r.getRiskLevel(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        String[] levels = {"GREEN", "YELLOW", "ORANGE", "RED"};
        String[] names = {"绿色正常", "黄色预警", "橙色预警", "红色预警"};

        for (int i = 0; i < levels.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", names[i]);
            item.put("value", levelCount.getOrDefault(levels[i], 0L));
            stats.add(item);
        }
        return stats;
    }

    private List<Map<String, Object>> getWarningStatusStats() {
        List<WarningRecord> warnings = warningRecordRepository.findAll();
        Map<String, Long> statusCount = warnings.stream()
                .collect(Collectors.groupingBy(
                        w -> w.getStatus() == null ? "未处理" : w.getStatus(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        statusCount.forEach((status, count) -> {
            Map<String, Object> item = new HashMap<>();
            // 将英文状态码转换为中文
            String statusName = switch (status) {
                case "UNCLAIMED" -> "未认领";
                case "CLAIMED" -> "已认领";
                case "PROCESSING" -> "处理中";
                case "CLOSED" -> "已结案";
                default -> status;
            };
            item.put("name", statusName);
            item.put("value", count);
            stats.add(item);
        });
        return stats;
    }

    private List<Map<String, Object>> getWarningTrendStats() {
        List<SurveyResult> results = surveyResultRepository.findAll();
        Map<String, Long> monthCount = results.stream()
                .filter(r -> r.getCreateTime() != null)
                .map(SurveyResult::getStudentId)
                .distinct()
                .collect(Collectors.groupingBy(
                        studentId -> {
                            SurveyResult result = results.stream()
                                    .filter(r -> r.getStudentId().equals(studentId))
                                    .findFirst()
                                    .orElse(null);
                            return result != null && result.getCreateTime() != null 
                                    ? result.getCreateTime().getYear() + "-" + String.format("%02d", result.getCreateTime().getMonthValue())
                                    : "未知";
                        },
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        monthCount.forEach((month, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", month);
            item.put("value", count);
            stats.add(item);
        });

        stats.sort(Comparator.comparing(m -> (String) m.get("name")));
        return stats;
    }

    private List<Map<String, Object>> getWarningTrendStats(List<Long> studentIds) {
        List<SurveyResult> results = surveyResultRepository.findAll();
        Map<String, Long> monthCount = results.stream()
                .filter(r -> studentIds.contains(r.getStudentId()))
                .filter(r -> r.getCreateTime() != null)
                .map(SurveyResult::getStudentId)
                .distinct()
                .collect(Collectors.groupingBy(
                        studentId -> {
                            SurveyResult result = results.stream()
                                    .filter(r -> r.getStudentId().equals(studentId))
                                    .findFirst()
                                    .orElse(null);
                            return result != null && result.getCreateTime() != null 
                                    ? result.getCreateTime().getYear() + "-" + String.format("%02d", result.getCreateTime().getMonthValue())
                                    : "未知";
                        },
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        monthCount.forEach((month, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", month);
            item.put("value", count);
            stats.add(item);
        });

        stats.sort(Comparator.comparing(m -> (String) m.get("name")));
        return stats;
    }

    private List<Map<String, Object>> getRiskTagStats() {
        List<WarningRecord> warnings = warningRecordRepository.findAll();
        System.out.println("Found " + warnings.size() + " warning records");
        Map<String, Long> tagCount = new HashMap<>();

        for (WarningRecord w : warnings) {
            if (w.getRiskFeatures() != null && !w.getRiskFeatures().isEmpty()) {
                System.out.println("Risk features: " + w.getRiskFeatures());
                String[] tags = w.getRiskFeatures().split("、");
                System.out.println("Split into " + tags.length + " tags");
                for (String tag : tags) {
                    tag = tag.trim();
                    System.out.println("Processing tag: '" + tag + "'");
                    if (!tag.isEmpty()) {
                        tagCount.put(tag, tagCount.getOrDefault(tag, 0L) + 1);
                    }
                }
            } else {
                System.out.println("Warning record " + w.getId() + " has no risk features");
            }
        }

        System.out.println("Tag count map: " + tagCount);
        List<Map<String, Object>> stats = new ArrayList<>();
        tagCount.forEach((tag, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", tag);
            item.put("value", count);
            stats.add(item);
        });

        System.out.println("Generated stats: " + stats);
        stats.sort((a, b) -> Long.compare((Long) b.get("value"), (Long) a.get("value")));
        List<Map<String, Object>> result = stats.stream().limit(10).collect(Collectors.toList());
        System.out.println("Final result: " + result);
        return result;
    }

    private List<Map<String, Object>> getGenderWarningStats() {
        List<SysUser> students = sysUserRepository.findByRole("STUDENT");
        Map<String, Long> genderCount = students.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getGender() == null ? "未知" : s.getGender(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        genderCount.forEach((gender, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", gender);
            item.put("value", count);
            stats.add(item);
        });
        return stats;
    }

    private List<Map<String, Object>> getGradeWarningStats() {
        List<SysUser> students = sysUserRepository.findByRole("STUDENT");
        Map<String, Long> gradeCount = students.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getGrade() == null ? "未知" : s.getGrade(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        gradeCount.forEach((grade, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", grade);
            item.put("value", count);
            stats.add(item);
        });
        return stats;
    }

    private List<Map<String, Object>> getCollegeWarningStats() {
        List<SysUser> students = sysUserRepository.findByRole("STUDENT");
        Map<String, Long> collegeCount = students.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCollege() == null ? "未知学院" : s.getCollege(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        collegeCount.forEach((college, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", college);
            item.put("value", count);
            stats.add(item);
        });
        return stats;
    }

    /**
     * 教师端数据大屏 - 只显示教师负责的班级/学生数据
     */
    @GetMapping(value = "/teacher/overview", produces = "application/json;charset=UTF-8")
    public Map<String, Object> getTeacherOverview(@RequestParam(required = false) Long teacherId, @RequestParam(required = false) String className) {
        System.out.println("Teacher overview called with teacherId: " + teacherId + ", className: " + className);
        Map<String, Object> result = new HashMap<>();

        // 获取学生列表
        List<SysUser> students;
        final List<Long> studentIds;
        
        if (className != null) {
            // 如果提供了className，获取该班级的学生
            students = sysUserRepository.findByClassNameAndRole(className, "STUDENT");
            System.out.println("Found " + students.size() + " students for class: " + className);
            studentIds = students.stream().map(SysUser::getId).collect(Collectors.toList());
        } else {
            // 如果没有提供className，返回所有学生
            students = sysUserRepository.findByRole("STUDENT");
            studentIds = students.stream().map(SysUser::getId).collect(Collectors.toList());
        }
        System.out.println("Student IDs: " + studentIds);

        // 统计学生总数
        long totalStudents = students.size();
        result.put("totalStudents", totalStudents);

        // 统计完成测评的学生数
        List<SurveyResult> surveyResults = surveyResultRepository.findAll();
        long totalSurveyStudents = surveyResults.stream()
                .filter(r -> studentIds.contains(r.getStudentId()))
                .map(SurveyResult::getStudentId)
                .distinct()
                .count();
        result.put("totalSurveyStudents", totalSurveyStudents);

        // 计算覆盖率
        double coverageRate = totalStudents > 0 ?
                Math.round((double) totalSurveyStudents / totalStudents * 10000.0) / 100.0 : 0;
        result.put("surveyCoverageRate", coverageRate);

        // 统计预警学生数（按学生ID去重）
        long totalWarnings = surveyResults.stream()
                .filter(r -> studentIds.contains(r.getStudentId()))
                .filter(r -> r.getRiskLevel() != null &&
                        (r.getRiskLevel().equals("YELLOW") ||
                                r.getRiskLevel().equals("ORANGE") ||
                                r.getRiskLevel().equals("RED")))
                .map(SurveyResult::getStudentId)
                .distinct()
                .count();
        result.put("totalWarnings", totalWarnings);

        // 统计干预记录数（按学生ID去重）
        long totalInterventions = interventionRecordRepository.findByStudentIdIn(studentIds).stream()
                .map(InterventionRecord::getStudentId)
                .distinct()
                .count();
        result.put("totalInterventions", totalInterventions);

        // 计算干预覆盖率
        double interventionCoverage = totalWarnings > 0 ?
                Math.round((double) totalInterventions / totalWarnings * 10000.0) / 100.0 : 0;
        result.put("interventionCoverageRate", interventionCoverage);

        // 获取教师负责学生的测评结果
        List<SurveyResult> teacherResults = surveyResults.stream()
                .filter(r -> studentIds.contains(r.getStudentId()))
                .collect(Collectors.toList());

        result.put("warningLevelStats", getWarningLevelStats(teacherResults));
        result.put("warningStatusStats", getTeacherWarningStatusStats(studentIds));
        result.put("warningTrendStats", getWarningTrendStats(studentIds));
        result.put("riskTagStats", getTeacherRiskTagStats(studentIds));
        result.put("genderWarningStats", getGenderWarningStatsForStudents(students));
        result.put("gradeWarningStats", getGradeWarningStatsForStudents(students));
        result.put("collegeWarningStats", getCollegeWarningStatsForStudents(students));

        return result;
    }

    private List<Map<String, Object>> getTeacherWarningStatusStats(List<Long> studentIds) {
        List<WarningRecord> warnings = warningRecordRepository.findByStudentIdInOrderByCreateTimeDesc(studentIds);
        Map<String, Long> statusCount = warnings.stream()
                .map(WarningRecord::getStudentId)
                .distinct()
                .collect(Collectors.groupingBy(
                        studentId -> {
                            WarningRecord latestWarning = warnings.stream()
                                    .filter(w -> w.getStudentId().equals(studentId))
                                    .findFirst()
                                    .orElse(null);
                            return latestWarning != null && latestWarning.getStatus() != null 
                                    ? latestWarning.getStatus() 
                                    : "未处理";
                        },
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        statusCount.forEach((status, count) -> {
            Map<String, Object> item = new HashMap<>();
            String statusName = switch (status) {
                case "UNCLAIMED" -> "未认领";
                case "CLAIMED" -> "已认领";
                case "PROCESSING" -> "处理中";
                case "CLOSED" -> "已结案";
                default -> status;
            };
            item.put("name", statusName);
            item.put("value", count);
            stats.add(item);
        });
        return stats;
    }

    private List<Map<String, Object>> getTeacherRiskTagStats(List<Long> studentIds) {
        List<WarningRecord> warnings = warningRecordRepository.findByStudentIdInOrderByCreateTimeDesc(studentIds);
        Map<String, Long> tagCount = new HashMap<>();

        // 按学生ID去重，只取每个学生的最新预警记录
        Map<Long, WarningRecord> latestWarnings = new HashMap<>();
        for (WarningRecord w : warnings) {
            Long studentId = w.getStudentId();
            if (!latestWarnings.containsKey(studentId)) {
                latestWarnings.put(studentId, w);
            }
        }

        for (WarningRecord w : latestWarnings.values()) {
            if (w.getRiskFeatures() != null && !w.getRiskFeatures().isEmpty()) {
                String[] tags = w.getRiskFeatures().split("、");
                for (String tag : tags) {
                    tag = tag.trim();
                    if (!tag.isEmpty()) {
                        tagCount.put(tag, tagCount.getOrDefault(tag, 0L) + 1);
                    }
                }
            }
        }

        List<Map<String, Object>> stats = new ArrayList<>();
        tagCount.forEach((tag, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", tag);
            item.put("value", count);
            stats.add(item);
        });

        stats.sort((a, b) -> Long.compare((Long) b.get("value"), (Long) a.get("value")));
        return stats.stream().limit(10).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getGenderWarningStatsForStudents(List<SysUser> students) {
        Map<String, Long> genderCount = students.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getGender() == null ? "未知" : s.getGender(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        genderCount.forEach((gender, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", gender);
            item.put("value", count);
            stats.add(item);
        });
        return stats;
    }

    private List<Map<String, Object>> getGradeWarningStatsForStudents(List<SysUser> students) {
        Map<String, Long> gradeCount = students.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getGrade() == null ? "未知" : s.getGrade(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        gradeCount.forEach((grade, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", grade);
            item.put("value", count);
            stats.add(item);
        });
        return stats;
    }

    private List<Map<String, Object>> getCollegeWarningStatsForStudents(List<SysUser> students) {
        Map<String, Long> collegeCount = students.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCollege() == null ? "未知学院" : s.getCollege(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> stats = new ArrayList<>();
        collegeCount.forEach((college, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", college);
            item.put("value", count);
            stats.add(item);
        });
        return stats;
    }
}