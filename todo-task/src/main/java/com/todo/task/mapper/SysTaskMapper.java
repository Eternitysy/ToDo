package com.todo.task.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.todo.common.core.domain.entity.SysTask;

/**
 * 任务管理 数据层
 *
 * @author sy
 */
@Mapper
public interface SysTaskMapper
{
    /**
     * 查询任务管理数据
     *
     * @param Task 任务信息
     * @return 任务信息集合
     */
    public List<SysTask> selectTaskList(SysTask Task);

    /**
     * 根据角色ID查询任务树信息
     *
     * @param roleId 角色ID
     * @param taskCheckStrictly 任务树选择项是否关联显示
     * @return 选中任务列表
     */
    public List<Long> selectTaskListByRoleId(@Param("roleId") Long roleId, @Param("taskCheckStrictly") boolean taskCheckStrictly);

    /**
     * 根据任务ID查询信息
     *
     * @param taskId 任务ID
     * @return 任务信息
     */
    public SysTask selectTaskById(Long taskId);

    /**
     * 根据ID查询所有子任务
     *
     * @param taskId 任务ID
     * @return 任务列表
     */
    public List<SysTask> selectChildrenTaskById(Long taskId);

    /**
     * 根据ID查询所有子任务（正常状态）
     *
     * @param taskId 任务ID
     * @return 子任务数
     */
    public int selectNormalChildrenTaskById(Long taskId);

    /**
     * 是否存在子节点
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int hasChildByTaskId(Long taskId);

    /**
     * 查询任务是否存在用户
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int checkTaskExistUser(Long taskId);

    /**
     * 校验任务名称是否唯一
     *
     * @param taskName 任务名称
     * @param parentId 父任务ID
     * @return 结果
     */
    public SysTask checkTaskNameUnique(@Param("taskName") String taskName, @Param("parentId") Long parentId);

    /**
     * 新增任务信息
     *
     * @param Task 任务信息
     * @return 结果
     */
    public int insertTask(SysTask Task);

    /**
     * 修改任务信息
     *
     * @param Task 任务信息
     * @return 结果
     */
    public int updateTask(SysTask Task);

    /**
     * 修改所在任务正常状态
     *
     * @param taskIds 任务ID组
     */
    public void updateTaskStatusNormal(Long[] taskIds);

    /**
     * 修改子元素关系
     *
     * @param tasks 子元素
     * @return 结果
     */
    public int updateTaskChildren(@Param("tasks") List<SysTask> tasks);

    /**
     * 删除任务管理信息
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int deleteTaskById(Long taskId);

    List<SysTask> selectTaskByStatus(String status);
}
