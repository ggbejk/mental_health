package com.example.mentalhealth.controller;

import com.example.mentalhealth.dto.ParentContactRecordVO;
import com.example.mentalhealth.entity.ParentContactRecord;
import com.example.mentalhealth.entity.SysUser;
import com.example.mentalhealth.repository.ParentContactRecordRepository;
import com.example.mentalhealth.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/counselor/parent-contact")
public class ParentContactController {

    @Autowired
    private ParentContactRecordRepository parentContactRecordRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @GetMapping("/list")
    public List<ParentContactRecordVO> getParentContactList(
            @RequestParam(required = false) Long counselorId) {
        List<ParentContactRecord> records;
        if (counselorId != null) {
            records = parentContactRecordRepository
                    .findByCounselorIdOrderByCreateTimeDesc(counselorId);
        } else {
            // 如果没有提供counselorId，返回所有记录
            records = parentContactRecordRepository.findAll();
        }

        return records.stream().map(record -> {
            Optional<SysUser> studentOpt = sysUserRepository.findById(record.getStudentId());
            Optional<SysUser> counselorOpt = sysUserRepository.findById(record.getCounselorId());

            return new ParentContactRecordVO(
                    record.getId(),
                    record.getStudentId(),
                    studentOpt.map(SysUser::getRealName).orElse("未知学生"),
                    record.getCounselorId(),
                    counselorOpt.map(SysUser::getRealName).orElse("未知辅导员"),
                    record.getContactTime(),
                    record.getContactMethod(),
                    record.getContactPurpose(),
                    record.getContactContent(),
                    record.getContactResult(),
                    record.getFollowUpPlan(),
                    record.getCreateTime()
            );
        }).collect(Collectors.toList());
    }

    @GetMapping("/student/:studentId")
    public List<ParentContactRecordVO> getStudentParentContacts(@PathVariable Long studentId) {
        List<ParentContactRecord> records = parentContactRecordRepository
                .findByStudentIdOrderByCreateTimeDesc(studentId);

        return records.stream().map(record -> {
            Optional<SysUser> studentOpt = sysUserRepository.findById(record.getStudentId());
            Optional<SysUser> counselorOpt = sysUserRepository.findById(record.getCounselorId());

            return new ParentContactRecordVO(
                    record.getId(),
                    record.getStudentId(),
                    studentOpt.map(SysUser::getRealName).orElse("未知学生"),
                    record.getCounselorId(),
                    counselorOpt.map(SysUser::getRealName).orElse("未知辅导员"),
                    record.getContactTime(),
                    record.getContactMethod(),
                    record.getContactPurpose(),
                    record.getContactContent(),
                    record.getContactResult(),
                    record.getFollowUpPlan(),
                    record.getCreateTime()
            );
        }).collect(Collectors.toList());
    }

    @PostMapping("/save")
    public String saveParentContact(@RequestBody ParentContactRecord record) {
        if (record.getStudentId() == null) {
            throw new RuntimeException("学生ID不能为空");
        }

        ParentContactRecord newRecord = new ParentContactRecord();
        newRecord.setStudentId(record.getStudentId());
        newRecord.setCounselorId(record.getCounselorId());
        newRecord.setContactTime(record.getContactTime() != null ? record.getContactTime() : LocalDateTime.now());
        newRecord.setContactMethod(record.getContactMethod());
        newRecord.setContactPurpose(record.getContactPurpose());
        newRecord.setContactContent(record.getContactContent());
        newRecord.setContactResult(record.getContactResult());
        newRecord.setFollowUpPlan(record.getFollowUpPlan());
        newRecord.setCreateTime(LocalDateTime.now());

        parentContactRecordRepository.save(newRecord);
        return "保存成功";
    }
}
