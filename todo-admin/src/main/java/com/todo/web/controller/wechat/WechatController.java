package com.todo.web.controller.wechat;

import com.todo.common.core.domain.AjaxResult;
import com.todo.common.core.domain.entity.SysUser;
import com.todo.common.core.domain.model.LoginUser;
import com.todo.common.utils.SecurityUtils;
import com.todo.framework.web.service.TokenService;
import com.todo.system.service.ISysUserService;
import com.todo.wechat.domain.vo.BindPhoneVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

import static com.todo.common.core.domain.AjaxResult.error;
import static com.todo.common.core.domain.AjaxResult.success;

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
@RequestMapping("/todo/wechat")
@CrossOrigin
public class WechatController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private WxMpService mpService;

    @Autowired
    private TokenService tokenService;


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
        // 第三个参数：授权成功之后，跳转路径 ‘todos’ 替换成 ‘#’
        System.out.println("绑定？？？？");
        String url = mpService.getOAuth2Service()
                .buildAuthorizationUrl(userInfoUrl,
                        WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                        URLEncoder.encode(returnUrl.replace("todo", "#")) );
        return "redirect:" + url;
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws WxErrorException {
        WxOAuth2AccessToken accessToken = mpService.getOAuth2Service().getAccessToken(code);
        String openId = accessToken.getOpenId();
        System.out.println("openId = " + openId);
        BindPhoneVo bindPhoneVo=new BindPhoneVo();
        bindPhoneVo.setOpenId(openId);
        SysUser user = userService.selectUserByOpenId(openId);
        String token="";
        if(user!=null){
            LoginUser loginUser= new LoginUser(user);
            token= tokenService.createToken(loginUser);
        }if(returnUrl.indexOf("?") == -1) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }

    @ApiOperation(value = "绑定手机号")
    @PostMapping("bindPhone")
    @ResponseBody
    public AjaxResult bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        String phone = bindPhoneVo.getPhone();
        SysUser user = userService.selectUserByPhoneNumber(phone);
        if(user!=null){
            user.setOpenId(bindPhoneVo.getOpenId());
            userService.updateUser(user);
            LoginUser loginUser= new LoginUser(user);
            String token= tokenService.createToken(loginUser);
            return success(token);
        }else{
            return error("手机号码不存在，绑定失败");
        }
    }
}
