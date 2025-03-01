package com.todo.task.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.todo.common.core.domain.entity.SysTask;
import com.todo.task.mapper.SysAiChatMapper;
import com.todo.task.service.SysAiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * className: SysAiChatServiceImpl
 * <p>
 * description:
 * author: sy
 * date: 2025/3/1 23:49
 * version: 1.0
 */
@Service
public class SysAiChatServiceImpl implements SysAiChatService {
    @Autowired
    private SysAiChatMapper sysAiChatMapper;

    @Override
    public String chat(String message, int maxTokens) {
        System.out.println("message = " + message);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer sk-1ca6b0f714764820a27e48e5640ac5a4");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("stream", false);
        body.put("max_tokens", maxTokens);

        Map<String, String> msg = new HashMap<>();
        msg.put("role", "user");
        msg.put("content", message);
        body.put("messages", Arrays.asList(msg));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        String result= restTemplate.postForObject("https://api.deepseek.com/chat/completions", entity, String.class);
        System.out.println("result = " + result);

        // 保存聊天记录到数据库
//        ChatRecord record = new ChatRecord();
//        record.setPrompt(prompt);
//        record.setResponse(result);
//        record.setCreateTime(LocalDateTime.now());
//        record.setModel("deepseek-chat");
//        record.setMaxTokens(maxTokens);
//        chatRecordRepository.save(record);

        // 使用 Hutool JSONUtil 解析 JSON 数据
        JSONObject json = JSONUtil.parseObj(result);
        JSONArray choices = json.getJSONArray("choices");
        if (choices != null && choices.size() > 0) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject answer = firstChoice.getJSONObject("message");
            String content = answer.getStr("content");
            System.out.println("content = " + content);
            // 返回提取后的回答
            return content;
        }
        return "请稍后再试。";
    }

    @Override
    public List<SysTask> generateTask(String message, int maxTokens) {
        return null;
    }
}
