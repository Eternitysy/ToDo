package com.todo.task.service;

import com.todo.common.core.domain.entity.SysTask;
import reactor.core.publisher.Flux;

import java.util.List;

public interface DeepSeekService {

    /**
     *  流式输出
     *
     * @param userId
     * @param userMessage
     * @return {@link Flux }<{@link String }>
     */
    Flux<String> callDeepSeekStream(Long userId, String userMessage);

    /**
     * @param userId
     * @param userMessage
     * @return {@link String }
     */
    String callDeepSeek(Long userId, String userMessage);

    List<SysTask> generateTask(String message, int maxTokens);
}