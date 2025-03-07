//package com.todo.wechat.service.impl;
//
//import cn.hutool.core.date.DateTime;
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.todo.common.core.domain.entity.SysTask;
//import com.todo.common.core.domain.entity.SysUser;
//import com.todo.system.service.ISysUserService;
//import com.todo.task.service.ISysTaskService;
//import com.todo.wechat.service.MessageService;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import me.chanjar.weixin.common.error.WxErrorException;
//import me.chanjar.weixin.mp.api.WxMpService;
//import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
//import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
//import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.Resource;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.Map;
//
///**
// * className: MessageServiceImpl
// * <p>
// * description:
// * author: sy
// * date: 2025/1/21 15:55
// * version: 1.0
// */
//@Slf4j
//@Service
//public class MessageServiceImpl implements MessageService {
//
//    @Autowired
//    private WxMpService wxMpService;
//
//    @Autowired
//    private ISysUserService sysUserService;
//
//    @Autowired
//    private ISysTaskService sysTaskService;
//
//    @SneakyThrows
//    @Override
//    public void pushTaskStartedMessage(Long userId, Long taskId) {
//        SysUser sysUser = sysUserService.selectUserById(userId);
//        String openid = sysUser.getOpenId();
//        //方便测试，给默认值（开发者本人的openId）
//        if(StringUtils.isEmpty(openid)) {
//            openid = "oPr1L7KKxrCz-44wP88GItVbhVSY";
//        }
//        SysTask sysTask = sysTaskService.selectTaskById(taskId);
//        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
//        .toUser(openid)//要推送的用户openid
//        .templateId("jmhtzmyWVLALRMlaNS5xhMo8PP6b7NgLqrdPXLD_aTw")//任务开始模板id
//        .url("http://eternitysy.ss5.tunnelfrp.com/#/show/"+taskId)//点击模板消息要访问的网址
//        .build();
//        long day = ChronoUnit.DAYS.between(LocalDate.now(), sysTask.getDeadline());
//        templateMessage.addData(new WxMpTemplateData("first", "新任务开始，请注意查看!", "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword1", sysTask.getTaskName(), "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword2", sysTask.getDeadline().toString(), "#272727"));
//         templateMessage.addData(new WxMpTemplateData("keyword3", "剩余"+ day +"天", "#272727"));
//        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
//        log.info("推送消息返回：{}", msg);
//    }
//
//    @SneakyThrows
//    @Override
//    public void pushTaskUnFinishedMessage(Long userId, Long taskId) {
//        SysUser sysUser = sysUserService.selectUserById(userId);
//        String openid = sysUser.getOpenId();
//        //方便测试，给默认值（开发者本人的openId）
//        if(StringUtils.isEmpty(openid)) {
//            openid = "oPr1L7KKxrCz-44wP88GItVbhVSY";
//        }
//        SysTask sysTask = sysTaskService.selectTaskById(taskId);
//        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
//                .toUser(openid)//要推送的用户openid
//                .templateId("Rp0HZ69N4bM_GTbvxIt9iMMxSWU8XzAd6DiS2iR1ZY4")//任务结束模板id
//                .url("http://eternitysy.ss5.tunnelfrp.com/#/show/"+taskId)//点击模板消息要访问的网址
//                .build();
//        long day = ChronoUnit.DAYS.between(LocalDate.now(), sysTask.getDeadline());
//        templateMessage.addData(new WxMpTemplateData("first", "任务即将结束，请注意查看!", "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword1", sysTask.getTaskName(), "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword2", sysTask.getDeadline().toString(), "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword3", "剩余"+ day +"天", "#272727"));
//        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
//        log.info("推送消息返回：{}", msg);
//    }
//
////    @Scheduled(cron = "0 29,30,32 * ?")
////    public void test() throws WxErrorException {
////        //方便测试，给默认值（开发者本人的openId）
////        System.out.println("推送结束任务");
////        String openid = "oPr1L7KKxrCz-44wP88GItVbhVSY";
////        Long taskId= 1L;
////        SysTask sysTask = sysTaskService.selectTaskById(taskId);
////        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
////                .toUser(openid)//要推送的用户openid
////                .templateId("Rp0HZ69N4bM_GTbvxIt9iMMxSWU8XzAd6DiS2iR1ZY4")//任务结束模板id
////                .url("http://eternitysy.ss5.tunnelfrp.com/#/show/"+taskId)//点击模板消息要访问的网址
////                .build();
////        long day = ChronoUnit.DAYS.between(LocalDate.now(), sysTask.getDeadline());
////        templateMessage.addData(new WxMpTemplateData("first", "任务即将结束，请注意查看!", "#272727"));
////        templateMessage.addData(new WxMpTemplateData("keyword1", sysTask.getTaskName(), "#272727"));
////        templateMessage.addData(new WxMpTemplateData("keyword2", sysTask.getDeadline().toString(), "#272727"));
////        templateMessage.addData(new WxMpTemplateData("keyword3", "剩余"+ day +"天", "#272727"));
////        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
////        log.info("推送消息返回：{}", msg);
////    }
//
//}