package com.todo.web.controller.task;

import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.todo.task.service.ISysTaskService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.todo.common.annotation.Log;
import com.todo.common.constant.UserConstants;
import com.todo.common.core.controller.BaseController;
import com.todo.common.core.domain.AjaxResult;
import com.todo.common.core.domain.entity.SysTask;
import com.todo.common.enums.BusinessType;
import com.todo.common.utils.StringUtils;

/**
 * 任务信息
 * 
 * @author sy
 */
@RestController
@RequestMapping("/tasks/task")
public class SysTaskController extends BaseController
{
    @Autowired
    private ISysTaskService taskService;

//    @Autowired
//    private MessageService messageService;

    /**
     * 获取任务列表
     */
    //@PreAuthorize("@ss.hasPermi('tasks:task:list')")
    @GetMapping("/list")
    public AjaxResult list(SysTask task)
    {
        List<SysTask> tasks = taskService.selectTaskList(task);
        return success(tasks);
    }

    /**
     * 查询任务列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('tasks:task:list')")
    @GetMapping("/list/exclude/{taskId}")
    public AjaxResult excludeChild(@PathVariable(value = "taskId", required = false) Long taskId)
    {
        List<SysTask> tasks = taskService.selectTaskList(new SysTask());
        tasks.removeIf(d -> d.getTaskId().intValue() == taskId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), taskId + ""));
        return success(tasks);
    }

    /**
     * 根据任务编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('tasks:task:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId)
    {
        taskService.checkTaskDataScope(taskId);
        return success(taskService.selectTaskById(taskId));
    }

    /**
     * 新增任务
     */
    @PreAuthorize("@ss.hasPermi('tasks:task:add')")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysTask task)
    {
        if (!taskService.checkTaskNameUnique(task))
        {
            return error("新增任务'" + task.getTaskName() + "'失败，任务名称已存在");
        }
        task.setCreateBy(getUsername());
        return toAjax(taskService.insertTask(task));
    }

    /**
     * 新增多条任务
     */
    @Log(title = "新增任务", businessType = BusinessType.INSERT)
    @PostMapping("/addTasks")
    public AjaxResult addTasks(@Validated @RequestBody List<SysTask> taskList)
    {
        for(SysTask task:taskList){
            if (!taskService.checkTaskNameUnique(task))
            {
                return error("新增任务'" + task.getTaskName() + "'失败，任务名称已存在");
            }
        }
        return toAjax(taskService.insertTasks(taskList));
    }

    /**
     * 修改任务
     */
    @PreAuthorize("@ss.hasPermi('tasks:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysTask task)
    {
        Long taskId = task.getTaskId();
        taskService.checkTaskDataScope(taskId);
        if (!taskService.checkTaskNameUnique(task))
        {
            return error("修改任务'" + task.getTaskName() + "'失败，任务名称已存在");
        }
        else if (task.getParentId().equals(taskId))
        {
            return error("修改任务'" + task.getTaskName() + "'失败，上级任务不能是自己");
        }
        else if (StringUtils.equals(UserConstants.TASK_DISABLE, task.getStatus()) && taskService.selectNormalChildrenTaskById(taskId) > 0)
        {
            return error("该任务包含未停用的子任务！");
        }
        task.setUpdateBy(getUsername());
        return toAjax(taskService.updateTask(task));
    }

    /**
     * 删除任务
     */
    @PreAuthorize("@ss.hasPermi('tasks:task:remove')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskId}")
    public AjaxResult remove(@PathVariable Long taskId)
    {
        if (taskService.hasChildByTaskId(taskId))
        {
            return warn("存在下级任务,不允许删除");
        }
        if (taskService.checkTaskExistUser(taskId))
        {
            return warn("任务存在用户,不允许删除");
        }
        taskService.checkTaskDataScope(taskId);
        return toAjax(taskService.deleteTaskById(taskId));
    }

    @GetMapping("/findUnStarted/{status}/{page}/{limit}")
    public PageInfo<SysTask> findUnStarted(@PathVariable String status,
                                           @PathVariable int page,
                                           @PathVariable int limit){
        PageHelper.startPage(page,limit);
        List<SysTask> taskList = taskService.selectTaskByStatus(status);
        return new PageInfo<>(taskList);
    }

    @GetMapping("/findUnFinished/{status}/{page}/{limit}")
    public PageInfo<SysTask> findUnFinished(@PathVariable String status,
                                        @PathVariable int page,
                                        @PathVariable int limit){
        PageHelper.startPage(page,limit);
        List<SysTask> taskList = taskService.selectTaskByStatus(status);
        return new PageInfo<>(taskList);
    }

    @GetMapping("/findFinished/{status}/{page}/{limit}")
    public PageInfo<SysTask> findFinished(@PathVariable String status,
                                      @PathVariable int page,
                                      @PathVariable int limit){
        PageHelper.startPage(page,limit);
        List<SysTask> taskList = taskService.selectTaskByStatus(status);
        return new PageInfo<>(taskList);
    }

//    @PostMapping("pushTaskStartedMessage/{userId}/{taskId}")
//    public AjaxResult pushTaskStartedMessage(@PathVariable Long userId,
//                                             @PathVariable Long taskId) {
//        messageService.pushTaskStartedMessage(userId,taskId);
//        return success();
//    }

//    @PostMapping("pushTaskUnFinishedMessage/{userId}/{taskId}")
//    public AjaxResult pushTaskUnFinishedMessage(@PathVariable Long userId,
//                                                @PathVariable Long taskId) {
//        System.out.println("开始推送未完成任务");
//        messageService.pushTaskUnFinishedMessage(userId,taskId);
//        return success();
//    }
}
