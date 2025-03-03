package com.todo.wechat.domain;

import com.todo.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "菜单")
public class Menu extends BaseEntity {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "网页 链接，用户点击菜单可打开链接")
    private String url;

    @ApiModelProperty(value = "菜单KEY值，用于消息接口推送")
    private String meunKey;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}