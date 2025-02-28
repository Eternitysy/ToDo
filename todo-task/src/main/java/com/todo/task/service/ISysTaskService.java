package com.todo.task.service;

import com.todo.task.domain.SysTask;

import java.util.List;

/**
 * className: ISysTaskService
 * <p>
 * description:
 * author: sy
 * date: 2025/2/28 18:14
 * version: 1.0
 */
public interface ISysTaskService {
    List<SysTask> selectTaskList(SysTask task);

    int insertTask(SysTask task);

    int updateTask(SysTask task);

    int deleteTaskById(Long taskId);
}
