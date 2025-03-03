package com.sy.wechat.controller;


import com.sy.common.result.Result;
import com.sy.model.wechat.Menu;
import com.sy.vo.wechat.MenuVo;
import com.sy.wechat.service.WechatMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author sy
 * @since 2025-01-20
 */
@Api(tags = "公众号菜单管理")
@RestController
@RequestMapping("/admin/wechat/menu")
@Slf4j
public class WechatMenuController {
    @Autowired
    private WechatMenuService service;

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        Menu menu = service.getById(id);
        return Result.ok(menu);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody Menu menu){
        service.save(menu);
        return Result.ok();
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result update(@RequestBody Menu menu){
        service.updateById(menu);
        return Result.ok();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        service.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "获取全部菜单")
    @GetMapping("findMenuInfo")
    public Result findMenuInfo(){
        List<MenuVo> list=service.findMenuInfo();
        return Result.ok(list);
    }

    @ApiOperation(value = "同步菜单")
    @GetMapping("syncMenu")
    public Result createMenu() {
        service.syncMenu();
        return Result.ok();
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("removeMenu")
    public Result removeMenu() {
        service.removeMenu();
        return Result.ok();
    }
}

