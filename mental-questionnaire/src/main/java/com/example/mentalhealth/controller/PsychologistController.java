package com.example.mentalhealth.controller;

import com.example.mentalhealth.entity.*;
import com.example.mentalhealth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/psychologist")
public class PsychologistController {

    @Autowired
    private WarningRecordRepository warningRecordRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private InterventionRecordRepository interventionRecordRepository;

    @Autowired
    private ScaleRepository scaleRepository;

    @Autowired
    private ScaleQuestionRepository scaleQuestionRepository;

    @Autowired
    private ConsultationRecordRepository consultationRecordRepository;

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @PostMapping("/assess")
    public String assessWarning(@RequestBody Map<String, Object> params) {
        Long warningId = Long.parseLong(params.get("warningId").toString());
        String assessmentLevel = params.get("assessmentLevel").toString();
        String assessmentOpinion = params.get("assessmentOpinion").toString();
        String interventionSuggestion = params.get("interventionSuggestion") != null ? 
                params.get("interventionSuggestion").toString() : "";

        WarningRecord warning = warningRecordRepository.findById(warningId)
                .orElseThrow(() -> new RuntimeException("预警记录不存在"));

        warning.setRiskLevel(assessmentLevel);
        warning.setStatus("CLAIMED");
        warningRecordRepository.save(warning);

        // 可以在这里创建干预记录或咨询预约

        return "评估成功";
    }

    @GetMapping("/scales")
    public List<Map<String, Object>> getScales() {
        List<Scale> scales = scaleRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Scale scale : scales) {
            Map<String, Object> scaleMap = new HashMap<>();
            scaleMap.put("id", scale.getId());
            scaleMap.put("name", scale.getName());
            scaleMap.put("description", scale.getDescription());
            scaleMap.put("status", scale.getStatus());
            scaleMap.put("createTime", scale.getCreateTime());
            scaleMap.put("updateTime", scale.getUpdateTime());
            
            // 计算题目数量
            long questionCount = scaleQuestionRepository.findByScaleIdOrderByQuestionOrderAsc(scale.getId()).size();
            scaleMap.put("questionCount", questionCount);
            
            // 计算使用次数（通过关联的任务和结果）
            long usageCount = 0;
            // 这里需要根据实际业务逻辑计算使用次数
            // 暂时返回0，后续可以根据survey_result表中的记录计算
            scaleMap.put("usageCount", usageCount);
            
            result.add(scaleMap);
        }
        
        return result;
    }

    @GetMapping("/scale/{scaleId}")
    public Map<String, Object> getScaleDetail(@PathVariable Long scaleId) {
        Scale scale = scaleRepository.findById(scaleId)
                .orElseThrow(() -> new RuntimeException("量表不存在"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", scale.getId());
        result.put("name", scale.getName());
        result.put("description", scale.getDescription());
        result.put("status", scale.getStatus());
        result.put("createTime", scale.getCreateTime());
        result.put("updateTime", scale.getUpdateTime());
        
        // 计算题目数量
        long questionCount = scaleQuestionRepository.findByScaleIdOrderByQuestionOrderAsc(scaleId).size();
        result.put("questionCount", questionCount);
        
        // 计算使用次数
        long usageCount = 0;
        result.put("usageCount", usageCount);
        
        return result;
    }

    @GetMapping("/scale/{scaleId}/questions")
    public List<ScaleQuestion> getScaleQuestions(@PathVariable Long scaleId) {
        return scaleQuestionRepository.findByScaleIdOrderByQuestionOrderAsc(scaleId);
    }

    @PostMapping("/scale/create")
    public String createScale(@RequestParam String name, @RequestParam String description) {
        Scale scale = new Scale();
        scale.setName(name);
        scale.setDescription(description);
        scale.setStatus("ACTIVE");
        scale.setCreateTime(LocalDateTime.now());
        scaleRepository.save(scale);
        return "量表创建成功";
    }

    @PostMapping("/scale/{scaleId}/update")
    public String updateScale(@PathVariable Long scaleId, 
                             @RequestParam String name, 
                             @RequestParam String description) {
        Scale scale = scaleRepository.findById(scaleId)
                .orElseThrow(() -> new RuntimeException("量表不存在"));
        scale.setName(name);
        scale.setDescription(description);
        scale.setUpdateTime(LocalDateTime.now());
        scaleRepository.save(scale);
        return "量表更新成功";
    }

    @GetMapping("/consultations")
    public List<Map<String, Object>> getConsultations() {
        List<ConsultationRecord> records = consultationRecordRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (ConsultationRecord record : records) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", record.getId());
            map.put("studentId", record.getStudentId());
            map.put("studentName", record.getStudentName());
            map.put("counselorId", record.getCounselorId());
            map.put("counselorName", record.getCounselorName());
            map.put("consultationType", record.getConsultationType());
            map.put("appointmentTime", record.getAppointmentTime());
            map.put("status", record.getStatus());
            map.put("notes", record.getNotes());
            map.put("createTime", record.getCreateTime());
            
            // 查询学生信息
            SysUser student = sysUserRepository.findById(record.getStudentId()).orElse(null);
            if (student != null) {
                map.put("college", student.getCollege());
                map.put("grade", student.getGrade());
                map.put("className", student.getClassName());
            }
            
            result.add(map);
        }
        
        return result;
    }

    @PostMapping("/consultation/schedule")
    public String scheduleConsultation(@RequestParam Long consultationId,
                                      @RequestParam String appointmentTime,
                                      @RequestParam Long counselorId) {
        ConsultationRecord consultation = consultationRecordRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("咨询记录不存在"));
        
        consultation.setCounselorId(counselorId);
        consultation.setAppointmentTime(LocalDateTime.parse(appointmentTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        consultation.setStatus("SCHEDULED");
        
        consultationRecordRepository.save(consultation);
        return "咨询安排成功";
    }

    @PostMapping("/consultation/book")
    public String bookConsultation(@RequestParam Long studentId,
                                   @RequestParam String studentName,
                                   @RequestParam String consultationType,
                                   @RequestParam String appointmentTime,
                                   @RequestParam String notes) {
        ConsultationRecord record = new ConsultationRecord();
        record.setStudentId(studentId);
        record.setStudentName(studentName);
        record.setConsultationType(consultationType);
        record.setAppointmentTime(LocalDateTime.parse(appointmentTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        record.setNotes(notes);
        record.setStatus("PENDING");
        record.setCreateTime(LocalDateTime.now());
        consultationRecordRepository.save(record);
        return "预约成功";
    }

    @GetMapping("/consultation/{consultationId}")
    public Map<String, Object> getConsultationDetail(@PathVariable Long consultationId) {
        ConsultationRecord record = consultationRecordRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("咨询记录不存在"));
        
        Map<String, Object> map = new HashMap<>();
        map.put("id", record.getId());
        map.put("studentId", record.getStudentId());
        map.put("studentName", record.getStudentName());
        map.put("counselorId", record.getCounselorId());
        map.put("counselorName", record.getCounselorName());
        map.put("consultationType", record.getConsultationType());
        map.put("appointmentTime", record.getAppointmentTime());
        map.put("status", record.getStatus());
        map.put("notes", record.getNotes());
        map.put("createTime", record.getCreateTime());
        
        // 查询学生信息
        SysUser student = sysUserRepository.findById(record.getStudentId()).orElse(null);
        if (student != null) {
            map.put("college", student.getCollege());
            map.put("grade", student.getGrade());
            map.put("className", student.getClassName());
        }
        
        return map;
    }

    @GetMapping("/counselors")
    public List<SysUser> getCounselors() {
        return sysUserRepository.findByRole("COUNSELOR");
    }
}

