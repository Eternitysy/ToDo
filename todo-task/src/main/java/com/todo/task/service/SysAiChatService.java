package com.todo.task.service;

import com.todo.common.core.domain.entity.SysTask;

import java.util.List;

/**
 * className: SysAiChatService
 * <p>
 * description:
 * author: sy
 * date: 2025/3/1 23:46
 * version: 1.0
 */
public interface SysAiChatService {
    String chat(String message, int maxTokens);

    List<SysTask> generateTask(String message, int maxTokens);
}
