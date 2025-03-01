package com.todo.web.controller.task;

import cn.hutool.json.JSONUtil;
import com.todo.common.core.domain.entity.SysTask;
import com.todo.task.service.SysAiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * className: SysAiChatController
 * <p>
 * description:
 * author: sy
 * date: 2025/3/1 19:11
 * version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/tasks/ai")
public class SysAiChatController {
    @Autowired
    private SysAiChatService chatService;
    @PostMapping("/chat")
    public String chat(@RequestParam String message,@RequestParam(defaultValue = "100") int maxTokens) {
       return chatService.chat(message,maxTokens);
    }

    @GetMapping("/generateTask")
    public List<SysTask> generateTask(@RequestParam String message,@RequestParam(defaultValue = "100") int maxTokens) {
        return chatService.generateTask(message,maxTokens);
    }
}
