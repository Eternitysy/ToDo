package com.todo.task.service.impl;

import com.todo.task.domain.SysTask;
import com.todo.task.mapper.SysTaskMapper;
import com.todo.task.service.ISysTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * className: SysTaskServiceImpl
 * <p>
 * description:
 * author: sy
 * date: 2025/2/28 18:15
 * version: 1.0
 */
@Service
public class SysTaskServiceImpl implements ISysTaskService {
    @Autowired
    private SysTaskMapper taskMapper;
    @Override
    public List<SysTask> selectTaskList(SysTask task) {
        return taskMapper.selectTaskList(task);
    }

    @Override
    public int insertTask(SysTask task) {
        return taskMapper.insertTask(task);
    }

    @Override
    public int updateTask(SysTask task) {
        return taskMapper.updateTask(task);
    }

    @Override
    public int deleteTaskById(Long taskId) {
        return taskMapper.deleteTaskById(taskId);
    }
}
