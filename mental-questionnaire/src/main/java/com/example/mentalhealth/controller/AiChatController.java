package com.example.mentalhealth.controller;

import com.example.mentalhealth.dto.AiChatRecordVO;
import com.example.mentalhealth.entity.AiChatRecord;
import com.example.mentalhealth.repository.AiChatRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/student/ai-chat")
public class AiChatController {

    @Autowired
    private AiChatRecordRepository aiChatRecordRepository;

    @PostMapping("/save")
    public String saveChatRecord(@RequestBody AiChatRecord record) {
        if (record.getStudentId() == null) {
            throw new RuntimeException("学生ID不能为空");
        }

        AiChatRecord chatRecord = new AiChatRecord();
        chatRecord.setStudentId(record.getStudentId());
        chatRecord.setContent(record.getContent());
        chatRecord.setEmotionTag(record.getEmotionTag());
        chatRecord.setSourceType(record.getSourceType());
        chatRecord.setCreateTime(LocalDateTime.now());

        aiChatRecordRepository.save(chatRecord);
        return "保存成功";
    }

    @GetMapping("/history")
    public List<AiChatRecordVO> getChatHistory(@RequestParam Long studentId) {
        List<AiChatRecord> records = aiChatRecordRepository
                .findTop50ByStudentIdOrderByCreateTimeDesc(studentId);

        return records.stream()
                .map(record -> new AiChatRecordVO(
                        record.getId(),
                        record.getStudentId(),
                        record.getContent(),
                        record.getEmotionTag(),
                        record.getSourceType(),
                        record.getCreateTime()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/emotion-stats")
    public Object getEmotionStats(@RequestParam Long studentId) {
        List<AiChatRecord> records = aiChatRecordRepository
                .findByStudentIdOrderByCreateTimeAsc(studentId);

        long anxietyCount = records.stream().filter(r -> "焦虑".equals(r.getEmotionTag())).count();
        long sadCount = records.stream().filter(r -> "低落".equals(r.getEmotionTag())).count();
        long angryCount = records.stream().filter(r -> "愤怒".equals(r.getEmotionTag())).count();
        long lonelyCount = records.stream().filter(r -> "孤独".equals(r.getEmotionTag())).count();
        long confusedCount = records.stream().filter(r -> "迷茫".equals(r.getEmotionTag())).count();
        long neutralCount = records.stream().filter(r -> "neutral".equals(r.getEmotionTag())).count();

        return new Object() {
            public final long anxiety = anxietyCount;
            public final long sad = sadCount;
            public final long angry = angryCount;
            public final long lonely = lonelyCount;
            public final long confused = confusedCount;
            public final long neutral = neutralCount;
            public final long total = records.size();
        };
    }
}
