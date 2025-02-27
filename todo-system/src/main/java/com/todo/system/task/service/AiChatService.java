package com.todo.system.task.service;

import org.springframework.stereotype.Service;

/**
 * className: AiChatService
 * <p>
 * description:
 * author: sy
 * date: 2025/2/27 22:59
 * version: 1.0
 */
@Service
public class AiChatService {

    public String getSmartAdvice(String userQuestion) {
        // 模拟大模型生成的建议，实际应用中这里会调用DeepSeek或其他AI接口
        return "优化建议：将学习时间段分配为25分钟一小节，并适当休息，提升效率。";
    }
}
