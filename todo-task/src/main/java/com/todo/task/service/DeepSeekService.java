package com.todo.task.service;

import reactor.core.publisher.Flux;

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
}