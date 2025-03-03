package com.sy.wechat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sy.auth.service.SysUserService;
import com.sy.common.jwt.JwtHelper;
import com.sy.common.result.Result;
import com.sy.model.system.SysUser;
import com.sy.vo.wechat.BindPhoneVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

/**
 * className: WechatController
 * <p>
 * description:
 * author: sy
 * date: 2025/1/21 15:55
 * version: 1.0
 */
@Api(tags = "微信授权登录")
@Controller
@RequestMapping("admin/wechat")
@CrossOrigin
public class WechatController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private WxMpService mpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;

    @ApiOperation(value = "用户授权")
    @GetMapping("authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl,
                            HttpServletRequest request){
        System.out.println("returnUrl = " + returnUrl);
        //buildAuthorizationUrl 三个参数
        // 第一个参数：授权路径，在哪个路径获取微信信息
        // 第二个参数：固定值，授权类型  WxConsts.OAuth2Scope.SNSAPI_USERINFO
        // 第三个参数：授权成功之后，跳转路径 ‘sy’ 替换成 ‘#’
        String url = mpService.getOAuth2Service()
                .buildAuthorizationUrl(userInfoUrl,
                        WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                        URLEncoder.encode(returnUrl.replace("sy", "#")) );
        return "redirect:" + url;
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws WxErrorException {
        WxOAuth2AccessToken accessToken = mpService.getOAuth2Service().getAccessToken(code);
        String openId = accessToken.getOpenId();
        System.out.println("openId = " + openId);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getOpenId,openId);
        SysUser user = userService.getOne(wrapper);
        String token="";
        if(user!=null){
            token= JwtHelper.createToken(user.getId(),user.getUsername());
        }if(returnUrl.indexOf("?") == -1) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }

    @ApiOperation(value = "绑定手机号")
    @PostMapping("bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        String phone = bindPhoneVo.getPhone();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone,phone);
        SysUser user = userService.getOne(wrapper);
        if(user!=null){
            user.setOpenId(bindPhoneVo.getOpenId());
            userService.updateById(user);
            String token = JwtHelper.createToken(user.getId(), user.getUsername());
            return Result.ok(token);
        }else{
            return Result.fail("手机号码不存在，绑定失败");
        }
    }
}
