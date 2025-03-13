package com.todo.task.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.todo.common.core.domain.entity.SysTask;
import com.todo.common.utils.StringUtils;
import com.todo.task.mapper.SysAiChatMapper;
import com.todo.task.mapper.SysTaskMapper;
import com.todo.task.service.SysAiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class SysAiChatServiceImpl implements SysAiChatService {

    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final String AUTH_TOKEN = "Bearer sk-1ca6b0f714764820a27e48e5640ac5a4";

    /**
     * 异步流式生成回答，返回 SseEmitter 用于前端 Server-Sent Events 接收流式数据
     */
    @Override
    public SseEmitter chatStream(String message, int maxTokens) {
        // 设置超时时间，例如 30 秒
        SseEmitter emitter = new SseEmitter(30000L);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", AUTH_TOKEN);
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body = new HashMap<>();
                body.put("model", "deepseek-chat");
                // stream参数设为true，启用流式响应
                body.put("stream", true);
                body.put("max_tokens", maxTokens);

                Map<String, String> msg = new HashMap<>();
                msg.put("role", "user");
                msg.put("content", message);
                body.put("messages", Arrays.asList(msg));

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

                // 使用 RestTemplate 发送请求，并获取响应数据流
                ResponseEntity<Resource> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, Resource.class);
                InputStream is = response.getBody().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                // 模拟流式数据处理：每读取一行数据就推送给前端
                while ((line = reader.readLine()) != null) {
                    // 此处可以根据实际返回的数据格式进行处理，例如过滤无效数据
                    if (!line.trim().isEmpty()) {
                        // 推送当前行到前端
                        emitter.send(SseEmitter.event().name("message").data(line));
                        System.out.println("line = " + line);
                    }
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        executor.shutdown();
        return emitter;
    }

    @Override
    public List<SysTask> generateTask(String message, int maxTokens) {
        String template = "请根据以下目标任务，拆解成一个详细的任务列表。返回的结果必须是 JSON 格式；每个任务包含以下字段： taskName（任务名称）、description（任务描述）、orderNum（处理次序）、parentId（父级任务id，目标任务的 id 固定为10）、ancestors（组级列表）、startTime（任务开始日期，格式YYYY-MM-DD）、deadline（截止日期，格式YYYY-MM-DD）。目标任务：";
        String instructions = template + message;
        String result = chat(instructions, maxTokens);
        System.out.println("result = " + result);
        List<SysTask> taskList = createTaskList(result);
        return taskList;
    }

    private List<SysTask> createTaskList(String result) {
        // 预处理
        String cleaned = preprocessJSON(result);
        System.out.println("cleaned = " + cleaned);
        JSONArray jsonArray = JSONUtil.parseArray(cleaned);
        List<SysTask> taskList = JSONUtil.toList(jsonArray, SysTask.class);
        System.out.println("taskList = " + taskList);
        return taskList;
    }

    // JSON预处理方法
    private String preprocessJSON(String raw) {
        // 1. 去除首尾无效字符
        String trimmed = raw.trim()
                .replaceAll("^[^\\[]*", "")  // 去除JSON数组前的无效字符
                .replaceAll("[^\\]]*$", ""); // 去除JSON数组后的无效字符

        // 2. 检查括号平衡
        int openBrackets = StringUtils.countMatches(trimmed, '{');
        int closeBrackets = StringUtils.countMatches(trimmed, '}');
        if (openBrackets > closeBrackets) {
            trimmed += "}".repeat(openBrackets - closeBrackets);
        }
        int b = StringUtils.countMatches(trimmed, '[');
        int c = StringUtils.countMatches(trimmed, ']');
        if (b > c) {
            trimmed += "]".repeat(b - c);
        }

        // 3. 修复未闭合的对象
        return trimmed.replaceAll("}(\\s*)([^]}]*)$", "}$1]");
    }


    @Override
    public String chat(String message, int maxTokens) {
        // 同步方式实现，用于任务拆解调用
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTH_TOKEN);
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

        String result = restTemplate.postForObject(API_URL, entity, String.class);
        JSONObject json = JSONUtil.parseObj(result);
        JSONArray choices = json.getJSONArray("choices");
        if (choices != null && choices.size() > 0) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject answer = firstChoice.getJSONObject("message");
            String content = answer.getStr("content");
            return content;
        }
        return "请稍后再试。";
    }
}
