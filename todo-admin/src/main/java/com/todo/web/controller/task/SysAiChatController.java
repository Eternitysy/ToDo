package com.todo.web.controller.task;

import com.todo.common.core.domain.entity.SysTask;
import com.todo.task.service.SysAiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

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
@RequestMapping("/tasks/ais")
public class SysAiChatController {
    @Autowired
    private SysAiChatService chatService;
    @PostMapping("/chat")
    public SseEmitter chat(@RequestParam String message, @RequestParam(defaultValue = "100") int maxTokens) {
       return chatService.chatStream(message,maxTokens);
    }

    @GetMapping("/generateTask")
    public List<SysTask> generateTask(@RequestParam String message,@RequestParam(defaultValue = "100") int maxTokens) {
        return chatService.generateTask(message,maxTokens);
    }
}
