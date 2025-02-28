package com.todo.task.controller;

import com.todo.common.core.controller.BaseController;
import com.todo.common.core.domain.AjaxResult;
import com.todo.common.core.page.TableDataInfo;
import com.todo.task.domain.SysTask;
import com.todo.task.service.ISysTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * className: taskController
 * <p>
 * description: 任务处理
 * author: sy
 * date: 2025/2/28 17:29
 * version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/system/task")
public class SysTaskController extends BaseController {
    @Autowired
    private ISysTaskService taskService;

    @GetMapping("/list")
    public TableDataInfo list(SysTask task){
        startPage();
        List<SysTask> tasks=taskService.selectTaskList(task);
        return getDataTable(tasks);
    }

    @GetMapping("/list/exclude/taskId")
    public AjaxResult excludeChild(@PathVariable Long taskId){
        List<SysTask> task=taskService.selectTaskList(new SysTask());
        task.removeIf(t->t.getTaskId().intValue()==taskId);
        return success();
    }

    @PostMapping("/add")
    public AjaxResult add(@RequestBody SysTask task){
        return toAjax(taskService.insertTask(task));
    }

    @PutMapping("/update")
    public AjaxResult update(@RequestBody SysTask task){
        return toAjax(taskService.updateTask(task));
    }

    @DeleteMapping("/delete")
    public AjaxResult delete(@PathVariable Long taskId){
        return toAjax(taskService.deleteTaskById(taskId));
    }



}
