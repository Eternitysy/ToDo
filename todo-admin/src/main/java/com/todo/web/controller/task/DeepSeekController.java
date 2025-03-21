package com.todo.web.controller.task;

import com.todo.common.core.controller.BaseController;
import com.todo.common.core.domain.entity.SysTask;
import com.todo.task.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@CrossOrigin
@RestController
@RequestMapping("/tasks/ai")
public class DeepSeekController extends BaseController {
    private static final Logger logger = Logger.getLogger(DeepSeekController.class.getName());

    @Autowired
    private DeepSeekService deepSeekService;

    //@PreAuthorize("@ss.hasPermi('tasks:ai:chatStream')")
    @PostMapping(value = "/chatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestParam String message) {
        SseEmitter emitter = new SseEmitter(180000L);
        Long userId = 1L;
        try {
            logger.info("开始处理userId的请求： " + userId + ", message: " + message);
            deepSeekService.callDeepSeekStream(userId, message)
                    .subscribe(
                            content -> {
                                try {
                                    emitter.send(content, MediaType.TEXT_PLAIN);
                                } catch (IOException e) {
                                    logger.severe("发送内容错误: " + e.getMessage());
                                    emitter.completeWithError(e);
                                }
                            },
                            error -> {
                                try {
                                    logger.severe("处理出现异常: " + error.getMessage());
                                    emitter.send(SseEmitter.event().data("Error: " + error.getMessage()));
                                } catch (IOException e) {
                                    logger.severe("发送了异常信息: " + e.getMessage());
                                    emitter.completeWithError(e);
                                } finally {
                                    emitter.complete();
                                }
                            },
                            () -> {
                                logger.info("该用户请求已处理完成 userId: " + userId);
                                emitter.complete();
                            }
                    );
        } catch (Exception e) {
            try {
                logger.severe("Unexpected error: " + e.getMessage());
                emitter.send(SseEmitter.event().data("Error: " + e.getMessage()));
            } catch (IOException ex) {
                logger.severe("Error sending unexpected error message: " + ex.getMessage());
                emitter.completeWithError(ex);
            } finally {
                emitter.complete();
            }
        }
        return emitter;
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String message) {
        return deepSeekService.callDeepSeek(getUserId(),message);
    }

    @GetMapping("/generateTask")
    public List<SysTask> generateTask(@RequestParam String message, @RequestParam(defaultValue = "100") int maxTokens) {
        return deepSeekService.generateTask(message,maxTokens);
    }
}