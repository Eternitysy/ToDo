package com.todo.wechat.service;

import com.todo.wechat.domain.Menu;
import com.todo.wechat.domain.vo.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author sy
 * @since 2025-01-20
 */
public interface WechatMenuService {

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();

    Menu getById(Long id);

    void save(Menu menu);

    void updateById(Menu menu);

    void removeById(Long id);
}
