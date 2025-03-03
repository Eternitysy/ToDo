package com.todo.wechat.mapper;

import com.todo.wechat.domain.Menu;
import me.chanjar.weixin.mp.enums.WxMpApiUrl;

import java.util.List;

/**
 * <p>
 * 菜单 Mapper 接口
 * </p>
 *
 * @author sy
 * @since 2025-03-03
 */
public interface WechatMenuMapper {

    List<Menu> selectList();

    void removeById(Long id);

    void updateById(Menu menu);

    void save(Menu menu);

    Menu getById(Long id);
}
