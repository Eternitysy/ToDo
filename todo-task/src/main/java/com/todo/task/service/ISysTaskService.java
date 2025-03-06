package com.todo.task.service;

import com.todo.common.annotation.DataScope;
import com.todo.common.core.domain.TreeSelect;
import com.todo.common.core.domain.entity.SysTask;

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

    @DataScope(taskAlias = "d")
    List<SysTask> selectTaskList(SysTask task);

    List<TreeSelect> selectTaskTreeList(SysTask task);

    List<SysTask> buildTaskTree(List<SysTask> tasks);

    List<TreeSelect> buildTaskTreeSelect(List<SysTask> tasks);

    List<Long> selectTaskListByRoleId(Long roleId);

    SysTask selectTaskById(Long taskId);

    int selectNormalChildrenTaskById(Long taskId);

    boolean hasChildByTaskId(Long taskId);

    boolean checkTaskExistUser(Long taskId);

    boolean checkTaskNameUnique(SysTask task);

    void checkTaskDataScope(Long taskId);

    int insertTask(SysTask task);

    int updateTask(SysTask task);

    int deleteTaskById(Long taskId);

    int insertTasks(List<SysTask> taskList);

    List<SysTask> selectTaskByStatus(String status);
}
