package com.example.mentalhealth.controller;

import com.example.mentalhealth.dto.InterventionRecordVO;
import com.example.mentalhealth.dto.InterventionSaveDTO;
import com.example.mentalhealth.dto.WarningClaimDTO;
import com.example.mentalhealth.dto.WarningDetailVO;
import com.example.mentalhealth.dto.WarningListVO;
import com.example.mentalhealth.entity.InterventionRecord;
import com.example.mentalhealth.entity.SysUser;
import com.example.mentalhealth.entity.WarningRecord;
import com.example.mentalhealth.repository.InterventionRecordRepository;
import com.example.mentalhealth.repository.SysUserRepository;
import com.example.mentalhealth.repository.WarningRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/counselor")
public class CounselorController {

    @Autowired
    private WarningRecordRepository warningRecordRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private InterventionRecordRepository interventionRecordRepository;

    @GetMapping("/warnings")
    public List<WarningListVO> getWarnings(@RequestParam(required = false) Long counselorId, @RequestParam(required = false) String role) {
        List<WarningRecord> warnings;
        
        // 如果是辅导员，只返回所带班级的学生预警
        if ("COUNSELOR".equals(role) && counselorId != null) {
            SysUser counselor = sysUserRepository.findById(counselorId).orElse(null);
            if (counselor != null) {
                String counselorClassName = counselor.getClassName();
                List<SysUser> students = sysUserRepository.findByClassName(counselorClassName);
                List<Long> studentIds = students.stream().map(SysUser::getId).collect(java.util.stream.Collectors.toList());
                warnings = warningRecordRepository.findByStudentIdInOrderByCreateTimeDesc(studentIds);
            } else {
                warnings = new ArrayList<>();
            }
        } else {
            // 管理员可以看到所有预警
            warnings = warningRecordRepository.findAllByOrderByCreateTimeDesc();
        }
        
        List<WarningListVO> result = new ArrayList<>();

        for (WarningRecord warning : warnings) {
            SysUser student = sysUserRepository.findById(warning.getStudentId()).orElse(null);

            result.add(new WarningListVO(
                    warning.getId(),
                    warning.getStudentId(),
                    student != null ? student.getRealName() : "未知学生",
                    student != null ? student.getCollege() : "",
                    student != null ? student.getGrade() : "",
                    student != null ? student.getClassName() : "",
                    student != null ? student.getGender() : "",
                    warning.getSourceType(),
                    warning.getRiskLevel(),
                    warning.getRiskScore(),
                    warning.getRiskFeatures(),
                    warning.getStatus(),
                    warning.getHandlerId(),
                    warning.getCreateTime()
            ));
        }

        return result;
    }

    @GetMapping("/warning/{warningId}")
    public WarningDetailVO getWarningDetail(@PathVariable Long warningId) {
        WarningRecord warning = warningRecordRepository.findById(warningId)
                .orElseThrow(() -> new RuntimeException("预警记录不存在"));

        SysUser student = sysUserRepository.findById(warning.getStudentId()).orElse(null);

        List<InterventionRecord> records = interventionRecordRepository.findByWarningIdOrderByCreateTimeDesc(warningId);
        List<InterventionRecordVO> recordVOList = new ArrayList<>();

        for (InterventionRecord record : records) {
            SysUser recordStudent = sysUserRepository.findById(record.getStudentId()).orElse(null);
            recordVOList.add(new InterventionRecordVO(
                    record.getId(),
                    record.getWarningId(),
                    record.getStudentId(),
                    recordStudent != null ? recordStudent.getRealName() : "未知学生",
                    recordStudent != null ? recordStudent.getCollege() : "",
                    recordStudent != null ? recordStudent.getGrade() : "",
                    recordStudent != null ? recordStudent.getClassName() : "",
                    record.getCounselorId(),
                    record.getContent(),
                    record.getActionType(),
                    record.getFollowUpTime(),
                    record.getCreateTime()
            ));
        }

        return new WarningDetailVO(
                warning.getId(),
                warning.getStudentId(),
                student != null ? student.getRealName() : "未知学生",
                student != null ? student.getCollege() : "",
                student != null ? student.getGrade() : "",
                student != null ? student.getClassName() : "",
                student != null ? student.getGender() : "",
                warning.getSourceType(),
                warning.getRiskLevel(),
                warning.getRiskScore(),
                warning.getRiskFeatures(),
                warning.getStatus(),
                warning.getHandlerId(),
                warning.getCreateTime(),
                recordVOList
        );
    }

    @PostMapping("/warning/claim")
    public String claimWarning(@RequestBody WarningClaimDTO dto) {
        WarningRecord warning = warningRecordRepository.findById(dto.getWarningId())
                .orElseThrow(() -> new RuntimeException("预警记录不存在"));

        if (!"UNCLAIMED".equals(warning.getStatus())) {
            throw new RuntimeException("该预警已被认领或已处理");
        }

        warning.setHandlerId(dto.getCounselorId());
        warning.setStatus("CLAIMED");
        warningRecordRepository.save(warning);

        return "认领成功";
    }

    @PostMapping("/warning/updateStatus")
    public String updateWarningStatus(@RequestParam Long warningId, @RequestParam String status) {
        WarningRecord warning = warningRecordRepository.findById(warningId)
                .orElseThrow(() -> new RuntimeException("预警记录不存在"));

        warning.setStatus(status);
        warningRecordRepository.save(warning);

        return "状态更新成功";
    }

    @PostMapping("/intervention/save")
    public String saveIntervention(@RequestBody InterventionSaveDTO dto) {
        WarningRecord warning = warningRecordRepository.findById(dto.getWarningId())
                .orElseThrow(() -> new RuntimeException("预警记录不存在"));

        InterventionRecord record = new InterventionRecord();
        record.setWarningId(dto.getWarningId());
        record.setStudentId(dto.getStudentId());
        record.setCounselorId(dto.getCounselorId());
        record.setContent(dto.getContent());
        record.setActionType(dto.getActionType());

        if (dto.getFollowUpTime() != null && !dto.getFollowUpTime().isEmpty()) {
            record.setFollowUpTime(LocalDateTime.parse(dto.getFollowUpTime()));
        }

        record.setCreateTime(LocalDateTime.now());
        interventionRecordRepository.save(record);

        if ("CLAIMED".equals(warning.getStatus())) {
            warning.setStatus("PROCESSING");
            warningRecordRepository.save(warning);
        }

        return "干预记录保存成功";
    }

    @GetMapping("/students")
    public List<SysUser> getStudents(@RequestParam(required = false) Long counselorId) {
        if (counselorId != null) {
            SysUser counselor = sysUserRepository.findById(counselorId).orElse(null);
            if (counselor != null) {
                String className = counselor.getClassName();
                return sysUserRepository.findByClassNameAndRole(className, "STUDENT");
            }
        }
        // 如果没有提供counselorId，返回所有学生
        return sysUserRepository.findByRole("STUDENT");
    }

    @GetMapping("/interventions")
    public List<InterventionRecordVO> getInterventions(@RequestParam(required = false) Long counselorId,
                                                       @RequestParam(required = false) Long studentId) {
        List<InterventionRecord> records;
        
        if (studentId != null) {
            // 如果提供了studentId，返回该学生的所有干预记录
            records = interventionRecordRepository.findByStudentIdOrderByCreateTimeDesc(studentId);
        } else if (counselorId != null) {
            // 如果只提供了counselorId，返回该辅导员创建的所有干预记录
            records = interventionRecordRepository.findByCounselorIdOrderByCreateTimeDesc(counselorId);
        } else {
            // 如果都没有提供，返回所有干预记录
            records = interventionRecordRepository.findAll();
        }
        
        List<InterventionRecordVO> result = new ArrayList<>();

        for (InterventionRecord record : records) {
            SysUser student = sysUserRepository.findById(record.getStudentId()).orElse(null);
            result.add(new InterventionRecordVO(
                    record.getId(),
                    record.getWarningId(),
                    record.getStudentId(),
                    student != null ? student.getRealName() : "未知学生",
                    student != null ? student.getCollege() : "",
                    student != null ? student.getGrade() : "",
                    student != null ? student.getClassName() : "",
                    record.getCounselorId(),
                    record.getContent(),
                    record.getActionType(),
                    record.getFollowUpTime(),
                    record.getCreateTime()
            ));
        }

        return result;
    }

    @GetMapping("/intervention/{interventionId}")
    public InterventionRecordVO getInterventionDetail(@PathVariable Long interventionId) {
        InterventionRecord record = interventionRecordRepository.findById(interventionId)
                .orElseThrow(() -> new RuntimeException("干预记录不存在"));
        
        SysUser student = sysUserRepository.findById(record.getStudentId()).orElse(null);
        
        return new InterventionRecordVO(
                record.getId(),
                record.getWarningId(),
                record.getStudentId(),
                student != null ? student.getRealName() : "未知学生",
                student != null ? student.getCollege() : "",
                student != null ? student.getGrade() : "",
                student != null ? student.getClassName() : "",
                record.getCounselorId(),
                record.getContent(),
                record.getActionType(),
                record.getFollowUpTime(),
                record.getCreateTime()
        );
    }
}