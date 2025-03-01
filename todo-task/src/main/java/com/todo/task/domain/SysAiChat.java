package com.todo.task.domain;

import com.todo.common.annotation.Excel;
import com.todo.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * className: SysAiChatService
 * <p>
 * description: Ai模型
 * author: sy
 * date: 2025/3/1 23:42
 * version: 1.0
 */
@Data
public class SysAiChat extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 聊天模型列表ID */
    private Long modelId;

    /** 配置URL */
    @Excel(name = "配置URL")
    private String url;

    /** apiKey */
    @Excel(name = "apiKey")
    private String apiKey;

    /** 模型版本 */
    @Excel(name = "模型版本")
    private String modelVersion;

    /** 模型名称 */
    @Excel(name = "模型名称")
    private String modelName;

    /** 敏感性词汇 */
    @Excel(name = "敏感性词汇")
    private String sensitive_word;

    /** 最大token 数 */
    @Excel(name = "最大token 数")
    private Integer tokenMax;

    /** 是否选择 */
    @Excel(name = "是否选择")
    private Boolean isChose;

}
