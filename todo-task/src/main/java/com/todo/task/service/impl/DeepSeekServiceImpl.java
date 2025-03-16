package com.todo.task.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.common.constant.CacheConstants;
import com.todo.common.core.domain.entity.SysTask;
import com.todo.common.core.redis.RedisCache;
import com.todo.common.utils.StringUtils;
import com.todo.task.domain.DeepSeekRequest;
import com.todo.task.domain.DeepSeekResponse;
import com.todo.task.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    private static final Logger logger = Logger.getLogger(DeepSeekServiceImpl.class.getName());

    private static final String USER = "user";
    private static final String SYSTEM = "system";

    private final WebClient webClient;

    @Autowired
    private RedisCache redisCache;

    // 设置默认缓存时间为30分钟
    @Value("${deepSeek.cache.expiration.minutes:30}")
    private int cacheExpirationMinutes;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeepSeekServiceImpl(@Value("${deepSeek.api.url}") String apiUrl,
                               @Value("${deepSeek.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public Flux<String> callDeepSeekStream(Long userId, String userMessage) {
        return callDeepSeekAsyncStream(userId, userMessage);
    }

    @Override
    public String callDeepSeek(Long userId, String userMessage) {
        Mono<String> stringMono = callDeepSeekAsync(userId, userMessage);
        return stringMono.block();
    }

    /**
     *  异步调用DeepSeek API
     *
     * @param userId
     * @param userMessage
     * @return {@link Mono }<{@link String }>
     */
    public Mono<String> callDeepSeekAsync(Long userId, String userMessage) {
        String chatKey = getChatKey(userId);

        // 获取或初始化用户的对话历史
        Object object = redisCache.getCacheObject(chatKey);
        List<DeepSeekRequest.Message> messages = Objects.isNull(object) ? new ArrayList<>() : (List<DeepSeekRequest.Message>) object;

        // 添加新的用户消息到对话历史
        messages.add(new DeepSeekRequest.Message(USER, userMessage));

        // 构建请求体
        DeepSeekRequest request = new DeepSeekRequest();
        request.setModel("deepseek-chat");
        request.setMessages(messages);

        // 发送异步请求
        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DeepSeekResponse.class)
                .map(response -> {
                    if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                        String content = response.getChoices().get(0).getMessage().getContent();
                        messages.add(new DeepSeekRequest.Message(SYSTEM, content));
                        redisCache.setCacheObject(chatKey,messages,cacheExpirationMinutes, TimeUnit.MINUTES);
                        return content;
                    } else {
                        throw new RuntimeException("Failed to call DeepSeek API: No choices in response");
                    }
                })
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to call DeepSeek API: " + e.getMessage())));
    }

    /**
     *  异步流式调用DeepSeek API
     *
     * @param userId
     * @param userMessage
     * @return {@link Flux }<{@link String }>
     */
    public Flux<String> callDeepSeekAsyncStream(Long userId, String userMessage) {
        String chatKey = getChatKey(userId);

        // 获取或初始化用户的对话历史
        Object object = redisCache.getCacheObject(chatKey);
        List<DeepSeekRequest.Message> messages = Objects.isNull(object) ? new ArrayList<>() : (List<DeepSeekRequest.Message>) object;

        // 添加新的用户消息到对话历史
        messages.add(new DeepSeekRequest.Message(USER, userMessage));

        // 构建请求体
        DeepSeekRequest request = new DeepSeekRequest();
        request.setModel("deepseek-chat");
        request.setMessages(messages);
        request.setStream(true);

        // 用于累积回答片段
        StringBuffer accumulatedContent = new StringBuffer();

        // 发送异步请求
        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(chunk -> {
                    logger.info("接收部分: " + chunk);
                    if ("[DONE]".equals(chunk.trim())) {
                        return Flux.empty();
                    }
                    try {
                        JsonNode rootNode = objectMapper.readTree(chunk);
                        JsonNode choicesNode = rootNode.path("choices");
                        if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                            JsonNode deltaNode = choicesNode.get(0).path("delta");
                            String content = deltaNode.path("content").asText("");
                            if (!content.isEmpty()) {
                                // 累积回答片段
                                accumulatedContent.append(content);
                                return Flux.just(content);
                            }
                        } else {
                            logger.warning("Choices 不能为空.");
                        }
                    } catch (Exception e) {
                        logger.severe("Failed to parse DeepSeek API response: " + e.getMessage());
                        return Flux.error(new RuntimeException("Failed to parse DeepSeek API response: " + e.getMessage()));
                    }
                    // 忽略非数据行
                    return Flux.empty();
                })
                .onErrorResume(e -> {
                    logger.severe("Failed to call DeepSeek API: " + e.getMessage());
                    return Flux.error(new RuntimeException("Failed to call DeepSeek API: " + e.getMessage()));
                })
                .doOnComplete(() -> {
                    // 确保在流式输出完成后保存完整的对话历史到 Redis
                    if (accumulatedContent.length() > 0) {
                        messages.add(new DeepSeekRequest.Message(SYSTEM, accumulatedContent.toString()));
                        redisCache.setCacheObject(chatKey,messages,cacheExpirationMinutes, TimeUnit.MINUTES);
                    }
                });
    }

    private String getChatKey(Long userId) {
        return CacheConstants.DEEP_SEEK_CHAT_KEY + userId;
    }

    public List<SysTask> generateTask(String message, int maxTokens) {
        String template = "请根据以下目标任务，拆解成一个详细的任务列表。返回的结果必须是 JSON 格式；每个任务包含以下字段： taskName（任务名称）、description（任务描述）、orderNum（处理次序）、parentId（父级任务id，目标任务的 id 固定为10）、ancestors（组级列表）、startTime（任务开始日期，格式YYYY-MM-DD）、deadline（截止日期，格式YYYY-MM-DD）。目标任务：";
        String instructions = template + message;
        String result = callDeepSeek(1L,instructions);
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
}