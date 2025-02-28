package com.todo.task.domain;

import com.todo.common.annotation.Excel;
import com.todo.common.annotation.Excel.ColumnType;
import com.todo.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 任务表
 * 
 * @author sy
 */
@Data
public class SysTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    @Excel(name = "任务序号", cellType = ColumnType.NUMERIC)
    private Long taskId;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String taskName;

    /** 任务组名 */
    @Excel(name = "任务组名")
    private String taskGroup;

    /** 任务描述 */
    @Excel(name = "任务描述")
    private String description;

    /** 任务优先级 */
    @Excel(name = "任务优先级", readConverterExp = "0=高,1=中,-1=低")
    private String priority;

    /** 任务状态 */
    @Excel(name = "任务状态", readConverterExp = "0=已完成,1=进行中,-1=未开始")
    private String status;

}
