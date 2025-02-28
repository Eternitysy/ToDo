package com.todo.task.mapper;

import com.todo.task.domain.SysTask;

import java.util.List;

/**
 * className: SysTaskMapper
 * <p>
 * description:
 * author: sy
 * date: 2025/2/28 19:01
 * version: 1.0
 */
public interface SysTaskMapper {
    List<SysTask> selectTaskList(SysTask task);

    int insertTask(SysTask task);

    int updateTask(SysTask task);

    int deleteTaskById(Long taskId);
}
