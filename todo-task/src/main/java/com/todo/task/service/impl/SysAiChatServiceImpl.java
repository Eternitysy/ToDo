package com.todo.task.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.todo.common.core.domain.entity.SysTask;
import com.todo.task.mapper.SysAiChatMapper;
import com.todo.task.mapper.SysTaskMapper;
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
    @Autowired
    private SysTaskMapper sysTaskMapper;

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
        String template = "请根据以下目标任务，拆解成一个详细的任务列表。返回的结果必须是 JSON 格式，每个任务包含以下字段： taskName（任务名称） description（任务描述） orderNum（处理次序，1，2，3）parentId(父级任务id，如果你拆分的任务是同级，则为该目标的id(规定为10)，因为这些任务都是由目标任务的子级,目标任务的父级id为0) ancestors(组级列表，0加上所有的父级id) deadline（截止日期，格式YYYY-MM-DD） 目标任务：";
        String instructions = template+message;
        String result = chat(instructions, maxTokens);
        List<SysTask> taskList = createTaskList(result);
        return taskList;
    }

    private List<SysTask> createTaskList(String result) {
        // 去除前后空白以及不必要的包装标记
        String cleaned = result.trim();
        // 如果返回结果中包含 markdown 代码块标记，则需要移除它们
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim(); // 去除 ```json
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim(); // 去除结尾的 ```
        }
        if (!cleaned.startsWith("[")) {
            throw new RuntimeException("返回的内容格式错误，无法解析为JSONArray");
        }
        JSONArray jsonArray = JSONUtil.parseArray(cleaned);
        List<SysTask> taskList = JSONUtil.toList(jsonArray, SysTask.class);
        return taskList;
    }
}
