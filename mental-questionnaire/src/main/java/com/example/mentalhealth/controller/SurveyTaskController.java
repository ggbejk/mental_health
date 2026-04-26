package com.example.mentalhealth.controller;

import com.example.mentalhealth.entity.Scale;
import com.example.mentalhealth.entity.SurveyResult;
import com.example.mentalhealth.entity.SurveyTask;
import com.example.mentalhealth.repository.ScaleRepository;
import com.example.mentalhealth.repository.SurveyResultRepository;
import com.example.mentalhealth.repository.SurveyTaskRepository;
import com.example.mentalhealth.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/survey")
public class SurveyTaskController {

    @Autowired
    private SurveyTaskRepository surveyTaskRepository;

    @Autowired
    private ScaleRepository scaleRepository;

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @GetMapping("/tasks")
    public List<Map<String, Object>> getTasks() {
        List<SurveyTask> tasks = surveyTaskRepository.findAllByOrderByCreateTimeDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (SurveyTask task : tasks) {
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("id", task.getId());
            taskMap.put("title", task.getTitle());
            taskMap.put("scaleId", task.getScaleId());
            taskMap.put("startTime", task.getStartTime());
            taskMap.put("endTime", task.getEndTime());
            taskMap.put("targetRange", task.getTargetRange());
            taskMap.put("status", task.getStatus());
            taskMap.put("createBy", task.getCreateBy());
            taskMap.put("createTime", task.getCreateTime());

            Scale scale = scaleRepository.findById(task.getScaleId()).orElse(null);
            taskMap.put("scaleName", scale != null ? scale.getName() : "未知量表");

            long completedCount = surveyResultRepository.countByTaskId(task.getId());
            long totalCount = userInfoRepository.count();
            taskMap.put("totalCount", totalCount);
            taskMap.put("completedCount", completedCount);

            result.add(taskMap);
        }

        return result;
    }

    @PostMapping("/task/create")
    public String createTask(@RequestParam String title,
                            @RequestParam Long scaleId,
                            @RequestParam(required = false) String college,
                            @RequestParam(required = false) String grade,
                            @RequestParam String startTime,
                            @RequestParam String endTime,
                            @RequestParam(required = false) Long createBy) {
        SurveyTask task = new SurveyTask();
        task.setTitle(title);
        task.setScaleId(scaleId);
        
        String targetRange = "";
        if (college != null && !college.isEmpty()) {
            targetRange = college;
        }
        if (grade != null && !grade.isEmpty()) {
            targetRange = targetRange.isEmpty() ? grade : targetRange + "/" + grade;
        }
        if (targetRange.isEmpty()) {
            targetRange = "全校";
        }
        task.setTargetRange(targetRange);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        task.setStartTime(LocalDateTime.parse(startTime, formatter));
        task.setEndTime(LocalDateTime.parse(endTime, formatter));
        task.setStatus(0);
        task.setCreateBy(createBy != null ? createBy : 1L); // 默认值为1
        task.setCreateTime(LocalDateTime.now());

        surveyTaskRepository.save(task);

        return "创建成功";
    }

    @GetMapping("/task/{taskId}")
    public Map<String, Object> getTaskDetail(@PathVariable Long taskId) {
        SurveyTask task = surveyTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", task.getId());
        result.put("title", task.getTitle());
        result.put("scaleId", task.getScaleId());
        result.put("startTime", task.getStartTime());
        result.put("endTime", task.getEndTime());
        result.put("targetRange", task.getTargetRange());
        result.put("status", task.getStatus());
        result.put("createBy", task.getCreateBy());
        result.put("createTime", task.getCreateTime());

        Scale scale = scaleRepository.findById(task.getScaleId()).orElse(null);
        result.put("scaleName", scale != null ? scale.getName() : "未知量表");

        long completedCount = surveyResultRepository.countByTaskId(task.getId());
        long totalCount = userInfoRepository.count();
        result.put("totalCount", totalCount);
        result.put("completedCount", completedCount);

        return result;
    }
}
