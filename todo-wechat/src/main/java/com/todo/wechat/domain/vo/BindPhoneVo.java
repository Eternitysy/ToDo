package com.todo.wechat.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BindPhoneVo {

    @ApiModelProperty(value = "手机")
    private String phone;

    @ApiModelProperty(value = "openId")
    private String openId;
}