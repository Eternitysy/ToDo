package com.sy.wechat.service;

import com.sy.model.wechat.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sy.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author sy
 * @since 2025-01-20
 */
public interface WechatMenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();
}
