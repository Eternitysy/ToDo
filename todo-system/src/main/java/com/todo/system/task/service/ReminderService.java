package com.todo.system.task.service;

import org.springframework.stereotype.Service;

/**
 * className: ReminderService
 * <p>
 * description:
 * author: sy
 * date: 2025/2/27 22:59
 * version: 1.0
 */
@Service
public class ReminderService {

    @Scheduled(fixedRate = 86400000)  // 每天执行一次
    public void checkTaskReminders() {
        // 检查任务的到期时间，并发送提醒
        System.out.println("Checking task reminders...");
    }

    // 提醒发送接口（例如：发送邮件、短信或站内通知）
    public void sendReminder(Long userId, String message) {
        // 通过邮件、短信等方式发送提醒
        System.out.println("Sending reminder to user " + userId + ": " + message);
    }
}
