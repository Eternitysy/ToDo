package com.todo.system.task.service;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;

@Service
public class PlanSplitService {

    public String[] splitPlanIntoTasks(String goal, int days) {
        // 模拟计划拆解逻辑，实际可以调用AI模型进行任务拆解
        String[] tasks = {
            "任务1：了解学习目标，准备学习资料",
            "任务2：制定每日学习时间表",
            "任务3：开始学习第一章",
            "任务4：复习第一章",
            "任务5：进行总结并制定下一步计划"
        };
        return tasks;
    }
}
