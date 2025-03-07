package com.todo.wechat.service.impl;

import com.todo.common.core.domain.entity.SysTask;
import com.todo.system.service.ISysUserService;
import com.todo.task.service.ISysTaskService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Component
public class WechatScheduledTasks {
    //@Scheduled(cron="0 29,30,32 * ?")   //每5秒执行一次
    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private ISysTaskService sysTaskService;

    @Scheduled(cron = "0 0 8 * * ?")   //每天8点触发
    public void pushTaskStartedMessage() throws WxErrorException {
        List<SysTask> taskList = sysTaskService.selectTaskByStatus("0");
        for(SysTask task:taskList){
            long day = ChronoUnit.DAYS.between(LocalDate.now(), task.getDeadline());
            if(day==1){
                needPushTaskStartedMessage(task);
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * ?")   //每天8点触发
    public void pushTaskUnFinishedMessage() throws WxErrorException {
        List<SysTask> taskList = sysTaskService.selectTaskByStatus("2");
        for(SysTask task:taskList){
            long day = ChronoUnit.DAYS.between(LocalDate.now(), task.getDeadline());
            if(day==1){
                needPushTaskUnFinishedMessage(task);
            }
        }
    }

    public void needPushTaskStartedMessage(SysTask task) throws WxErrorException {
        //方便测试，给默认值（开发者本人的openId）
        String openid = "oPr1L7KKxrCz-44wP88GItVbhVSY";
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("jmhtzmyWVLALRMlaNS5xhMo8PP6b7NgLqrdPXLD_aTw")//任务开始模板id
                .url("http://eternitysy.ss5.tunnelfrp.com/#/show/" + task.getTaskId())//点击模板消息要访问的网址
                .build();
        long day = ChronoUnit.DAYS.between(LocalDate.now(), task.getDeadline());
        templateMessage.addData(new WxMpTemplateData("first", "新任务开始，请注意查看!", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", task.getTaskName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", task.getDeadline().toString(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", "剩余" + day + "天", "#272727"));
        wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
    }

    public void needPushTaskUnFinishedMessage(SysTask task) throws WxErrorException {
        //方便测试，给默认值（开发者本人的openId）
        String openid = "oPr1L7KKxrCz-44wP88GItVbhVSY";
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("zK3E0NKoaVmv4r_vsNDJ7KZ1-B0-Hez7ZqGlTldBf8Y")//任务结束模板id
                .url("http://eternitysy.ss5.tunnelfrp.com/#/show/" + task.getTaskId())//点击模板消息要访问的网址
                .build();
        long day = ChronoUnit.DAYS.between(LocalDate.now(), task.getDeadline());
        templateMessage.addData(new WxMpTemplateData("keyword1", task.getTaskName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", task.getDeadline().toString(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", day + "天就要结束，请尽快完成 !", "#272727"));
        wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
    }
}