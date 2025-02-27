package com.todo.system.task.controller;

import com.todo.system.task.service.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiChatService aiChatService;

    public AiController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/suggestion")
    public ResponseEntity<String> getAdvice(@RequestBody String userQuestion) {
        String advice = aiChatService.getSmartAdvice(userQuestion);
        return ResponseEntity.ok(advice);
    }
}
