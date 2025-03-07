package com.todo.common.core.domain.entity;

import com.todo.common.annotation.Excel;
import com.todo.common.annotation.Excel.ColumnType;
import com.todo.common.core.domain.BaseEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    /** 父任务ID */
    private Long parentId;

    /** 父任务名称 */
    private String parentName;

    /** 祖级列表 */
    private String ancestors;

    /** 任务组名 */
    @Excel(name = "任务组名")
    private String taskGroup;

    /** 任务描述 */
    @Excel(name = "任务描述")
    private String description;

    /** 任务截止日期 */
    @Excel(name = "任务截止日期")
    private LocalDate deadline;


    /** 任务优先级(显示顺序) */
    @Excel(name = "任务优先级")
    private Integer orderNum;

    /** 任务状态 */
    @Excel(name = "任务状态", readConverterExp = "2=已完成,1=进行中,0=未开始")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 子任务 */
    private List<SysTask> children = new ArrayList<SysTask>();

}
