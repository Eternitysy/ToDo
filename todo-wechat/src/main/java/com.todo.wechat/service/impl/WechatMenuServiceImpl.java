package com.todo.wechat.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.todo.wechat.domain.Menu;
import com.todo.wechat.domain.vo.MenuVo;
import com.todo.wechat.mapper.WechatMenuMapper;
import com.todo.wechat.service.WechatMenuService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author sy
 * @since 2025-01-20
 */
@Service
public class WechatMenuServiceImpl implements WechatMenuService {

    @Autowired
    private WxMpService mpService;

    @Autowired
    private WechatMenuMapper menuMapper;
    @Override
    public List<MenuVo> findMenuInfo() {
        List<MenuVo> menuVoList = new ArrayList<>();
        List<Menu> menuList=menuMapper.selectList();
        List<Menu> oneMenuList = menuList
                .stream()
                .filter(menu -> menu.getParentId()
                        .longValue() == 0)
                .collect(Collectors.toList());
        for (Menu oneMenu : oneMenuList) {
            MenuVo oneMenuVo = new MenuVo();
            BeanUtils.copyProperties(oneMenu, oneMenuVo);

            List<Menu> twoMenuList = menuList.stream()
                    .filter(menu -> menu.getParentId().longValue() == oneMenu.getId())
                    .sorted(Comparator.comparing(Menu::getSort))
                    .collect(Collectors.toList());
            List<MenuVo> children = new ArrayList<>();
            for (Menu twoMenu : twoMenuList) {
                MenuVo twoMenuVo = new MenuVo();
                BeanUtils.copyProperties(twoMenu, twoMenuVo);
                children.add(twoMenuVo);
            }
            oneMenuVo.setChildren(children);
            menuVoList.add(oneMenuVo);
        }
        return menuVoList;
    }

    @Override
    public void syncMenu() {
        // 封装数据
        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for(MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if(CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://787a93c9.r40.cpolar.top/#"+oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for(MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if(twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://787a93c9.r40.cpolar.top#"+twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        //菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        // 调用mp方法完成推送
        try {
            mpService.getMenuService().menuCreate(button.toString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void removeMenu() {
        try {
            mpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Menu getById(Long id) {
        return menuMapper.getById(id);
    }

    @Override
    public void save(Menu menu) {
        menuMapper.save(menu);
    }

    @Override
    public void updateById(Menu menu) {
        menuMapper.updateById(menu);
    }

    @Override
    public void removeById(Long id) {
        menuMapper.removeById(id);
    }
}
