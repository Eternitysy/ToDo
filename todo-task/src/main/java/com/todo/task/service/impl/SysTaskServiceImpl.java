package com.todo.task.service.impl;

import com.todo.common.annotation.DataScope;
import com.todo.common.constant.UserConstants;
import com.todo.common.core.domain.TreeSelect;
import com.todo.common.core.domain.entity.SysTask;
import com.todo.common.core.domain.entity.SysRole;
import com.todo.common.core.domain.entity.SysUser;
import com.todo.common.core.text.Convert;
import com.todo.common.exception.ServiceException;
import com.todo.common.utils.SecurityUtils;
import com.todo.common.utils.StringUtils;
import com.todo.common.utils.spring.SpringUtils;
import com.todo.system.mapper.SysRoleMapper;
import com.todo.task.mapper.SysTaskMapper;
import com.todo.task.service.ISysTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务管理 服务实现
 * 
 * @author sy
 */
@Service
public class SysTaskServiceImpl implements ISysTaskService
{
    @Autowired
    private SysTaskMapper taskMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 查询任务管理数据
     * 
     * @param task 任务信息
     * @return 任务信息集合
     */
    @Override
    @DataScope(taskAlias = "t")
    public List<SysTask> selectTaskList(SysTask task)
    {
        return taskMapper.selectTaskList(task);
    }

    /**
     * 查询任务树结构信息
     * 
     * @param task 任务信息
     * @return 任务树信息集合
     */
    @Override
    public List<TreeSelect> selectTaskTreeList(SysTask task)
    {
        List<SysTask> tasks = SpringUtils.getAopProxy(this).selectTaskList(task);
        return buildTaskTreeSelect(tasks);
    }

    /**
     * 构建前端所需要树结构
     * 
     * @param tasks 任务列表
     * @return 树结构列表
     */
    @Override
    public List<SysTask> buildTaskTree(List<SysTask> tasks)
    {
        List<SysTask> returnList = new ArrayList<SysTask>();
        List<Long> tempList = tasks.stream().map(SysTask::getTaskId).collect(Collectors.toList());
        for (SysTask task : tasks)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(task.getParentId()))
            {
                recursionFn(tasks, task);
                returnList.add(task);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = tasks;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param tasks 任务列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildTaskTreeSelect(List<SysTask> tasks)
    {
        List<SysTask> taskTrees = buildTaskTree(tasks);
        return taskTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询任务树信息
     * 
     * @param roleId 角色ID
     * @return 选中任务列表
     */
    @Override
    public List<Long> selectTaskListByRoleId(Long roleId)
    {
        SysRole role = roleMapper.selectRoleById(roleId);
        return taskMapper.selectTaskListByRoleId(roleId, role.isTaskCheckStrictly());
    }

    /**
     * 根据任务ID查询信息
     * 
     * @param taskId 任务ID
     * @return 任务信息
     */
    @Override
    public SysTask selectTaskById(Long taskId)
    {
        return taskMapper.selectTaskById(taskId);
    }

    /**
     * 根据ID查询所有子任务（正常状态）
     * 
     * @param taskId 任务ID
     * @return 子任务数
     */
    @Override
    public int selectNormalChildrenTaskById(Long taskId)
    {
        return taskMapper.selectNormalChildrenTaskById(taskId);
    }

    /**
     * 是否存在子节点
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public boolean hasChildByTaskId(Long taskId)
    {
        int result = taskMapper.hasChildByTaskId(taskId);
        return result > 0;
    }

    /**
     * 查询任务是否存在用户
     * 
     * @param taskId 任务ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkTaskExistUser(Long taskId)
    {
        int result = taskMapper.checkTaskExistUser(taskId);
        return result > 0;
    }

    /**
     * 校验任务名称是否唯一
     * 
     * @param task 任务信息
     * @return 结果
     */
    @Override
    public boolean checkTaskNameUnique(SysTask task)
    {
        Long taskId = StringUtils.isNull(task.getTaskId()) ? -1L : task.getTaskId();
        SysTask info = taskMapper.checkTaskNameUnique(task.getTaskName(), task.getParentId());
        if (StringUtils.isNotNull(info) && info.getTaskId().longValue() != taskId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验任务是否有数据权限
     * 
     * @param taskId 任务id
     */
    @Override
    public void checkTaskDataScope(Long taskId)
    {
        if (!SysUser.isAdmin(SecurityUtils.getUserId()) && StringUtils.isNotNull(taskId))
        {
            SysTask task = new SysTask();
            task.setTaskId(taskId);
            List<SysTask> tasks = SpringUtils.getAopProxy(this).selectTaskList(task);
            if (StringUtils.isEmpty(tasks))
            {
                throw new ServiceException("没有权限访问任务数据！");
            }
        }
    }

    /**
     * 新增保存任务信息
     * 
     * @param task 任务信息
     * @return 结果
     */
    @Override
    public int insertTask(SysTask task)
    {
        SysTask info = taskMapper.selectTaskById(task.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.TASK_NORMAL.equals(info.getStatus()))
        {
            throw new ServiceException("任务停用，不允许新增");
        }
        task.setAncestors(info.getAncestors() + "," + task.getParentId());
        return taskMapper.insertTask(task);
    }

    /**
     * 修改保存任务信息
     * 
     * @param task 任务信息
     * @return 结果
     */
    @Override
    public int updateTask(SysTask task)
    {
        SysTask newParentTask = taskMapper.selectTaskById(task.getParentId());
        SysTask oldTask = taskMapper.selectTaskById(task.getTaskId());
        if (StringUtils.isNotNull(newParentTask) && StringUtils.isNotNull(oldTask))
        {
            String newAncestors = newParentTask.getAncestors() + "," + newParentTask.getTaskId();
            String oldAncestors = oldTask.getAncestors();
            task.setAncestors(newAncestors);
            updateTaskChildren(task.getTaskId(), newAncestors, oldAncestors);
        }
        int result = taskMapper.updateTask(task);
        if (UserConstants.TASK_NORMAL.equals(task.getStatus()) && StringUtils.isNotEmpty(task.getAncestors())
                && !StringUtils.equals("0", task.getAncestors()))
        {
            // 如果该任务是启用状态，则启用该任务的所有上级任务
            updateParentTaskStatusNormal(task);
        }
        return result;
    }

    /**
     * 修改该任务的父级任务状态
     * 
     * @param task 当前任务
     */
    private void updateParentTaskStatusNormal(SysTask task)
    {
        String ancestors = task.getAncestors();
        Long[] taskIds = Convert.toLongArray(ancestors);
        taskMapper.updateTaskStatusNormal(taskIds);
    }

    /**
     * 修改子元素关系
     * 
     * @param taskId 被修改的任务ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateTaskChildren(Long taskId, String newAncestors, String oldAncestors)
    {
        List<SysTask> children = taskMapper.selectChildrenTaskById(taskId);
        for (SysTask child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            taskMapper.updateTaskChildren(children);
        }
    }

    /**
     * 删除任务管理信息
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int deleteTaskById(Long taskId)
    {
        return taskMapper.deleteTaskById(taskId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysTask> list, SysTask t)
    {
        // 得到子节点列表
        List<SysTask> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysTask tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysTask> getChildList(List<SysTask> list, SysTask t)
    {
        List<SysTask> tlist = new ArrayList<SysTask>();
        Iterator<SysTask> it = list.iterator();
        while (it.hasNext())
        {
            SysTask n = (SysTask) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getTaskId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysTask> list, SysTask t)
    {
        return getChildList(list, t).size() > 0;
    }
}
