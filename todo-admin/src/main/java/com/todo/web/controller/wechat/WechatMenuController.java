package com.todo.web.controller.wechat;

import com.todo.common.core.domain.AjaxResult;
import com.todo.wechat.domain.Menu;
import com.todo.wechat.domain.vo.MenuVo;
import com.todo.wechat.service.WechatMenuService;
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
@RequestMapping("/wechat/menu")
@Slf4j
public class WechatMenuController {
    @Autowired
    private WechatMenuService menuService;

    @ApiOperation(value = "获取")
    @GetMapping("list/{id}")
    public AjaxResult get(@PathVariable Long id){
        Menu menu = menuService.getById(id);
        return AjaxResult.success(menu);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public AjaxResult save(@RequestBody Menu menu){
        menuService.save(menu);
        return AjaxResult.success();
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public AjaxResult update(@RequestBody Menu menu){
        menuService.updateById(menu);
        return AjaxResult.success();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public AjaxResult remove(@PathVariable Long id){
        menuService.removeById(id);
        return AjaxResult.success();
    }

    @ApiOperation(value = "获取全部菜单")
    @GetMapping("findMenuInfo")
    public AjaxResult findMenuInfo(){
        List<MenuVo> list=menuService.findMenuInfo();
        return AjaxResult.success(list);
    }

    @ApiOperation(value = "同步菜单")
    @GetMapping("syncMenu")
    public AjaxResult createMenu() {
        menuService.syncMenu();
        return AjaxResult.success();
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("removeMenu")
    public AjaxResult removeMenu() {
        menuService.removeMenu();
        return AjaxResult.success();
    }
}

