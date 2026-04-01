package com.develop.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.svix.Svix;
import com.svix.SvixOptions;
import com.svix.Webhook;
import com.svix.api.MessageListOptions;
import com.svix.exceptions.ApiException;
import com.svix.exceptions.WebhookSigningException;
import com.svix.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Svix Webhook 服务类
 * 提供端点注册和消息发布功能
 */
@Service
public class WebhookSenderService {

    private Svix svixClient;

    @Value("${svix.auth.token:}")
    private String authToken;

    /**
     * 初始化 Svix 客户端
     */
    @PostConstruct
    public void init() {
        if (authToken != null && !authToken.isEmpty()) {
            try {
                // 构建Client
                String serverUrl = "http://localhost:1801";
                String appId = "appId";
                String authToken = "AUTH_TOKEN";
                SvixOptions svixOptions = new SvixOptions();
                svixOptions.setServerUrl(serverUrl);
                this.svixClient = new Svix(authToken, svixOptions);

                // 发起list请求
                Set<String> eventTypes = new HashSet<>();
                eventTypes.add("token.trade");
                MessageListOptions messageListOptions = new MessageListOptions();
                messageListOptions.setLimit(10L);
                messageListOptions.setBefore(OffsetDateTime.now());
                messageListOptions.setEventTypes(eventTypes);
                ListResponseMessageOut listResponse = svixClient.getMessage().list(appId, messageListOptions);
                // 获取第一个和最后一个消息ID（单据ID），作为下一轮的before或after查询的iterator参数
                String firstMessageId = listResponse.getPrevIterator();
                String lastMessageId = listResponse.getIterator();


                // 解析请求结果
                listResponse.getIterator();
                listResponse.getPrevIterator();
                for (MessageOut messageOut : listResponse.getData()) {
                    String messageId = messageOut.getId();
                    String eventType = messageOut.getEventType();
                    String payload = (String) messageOut.getPayload();
                }
            } catch (Exception e) {
                System.err.println("❌ Svix 客户端初始化失败：" + e.getMessage());
            }
        } else {
            System.out.println("⚠️  未配置 SVIX_AUTH_TOKEN，Svix 功能将不可用");
        }
    }

    public static void main(String[] args) throws WebhookSigningException, JsonProcessingException {
        String secret = "whsec_MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw";
        Webhook webhook = new Webhook(secret);

        String id = "msg_p5jXN8AQM9LWM0D4loKWxJek";
        long timestamp = System.currentTimeMillis() / 1000;
        String payload = new MessageIn()
                .eventType("invoice.paid")
                .eventId("evt_Wqb1k73rXprtTm7Qdlr38G")
                .payload("{" +
                        "\"type\": \"invoice.paid\"," +
                        "\"id\": \"invoice_WF7WtCLFFtd8ubcTgboSFNql\"," +
                        "\"status\":  \"paid\"," +
                        "\"attempt\": 2" +
                        "}")
                .toJson();
        String signature = webhook.sign(id, timestamp, payload);

        // These were all sent from the server
        HashMap<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("webhook-id", Arrays.asList(id));
        headerMap.put("webhook-timestamp", Arrays.asList(String.valueOf(timestamp)));
        headerMap.put("webhook-signature", Arrays.asList(signature));
        HttpHeaders headers = HttpHeaders.of(headerMap, (s, s2) -> true);


        try {
            webhook.verify(payload, headers);
            System.out.println("✅ 验证成功");
        } catch (Exception ex) {
            System.out.println("❌ 验证失败：" + ex.getMessage());
        }
    }

    /**
     * 创建 Webhook 端点
     *
     * @param appId       应用程序 ID
     * @param url         端点 URL（接收 webhook 的地址）
     * @param description 端点描述
     * @return 创建的端点信息
     */
    public EndpointOut createEndpoint(String appId, String url, String description) throws IOException, ApiException {
        if (svixClient == null) {
            throw new IllegalStateException("Svix 客户端未初始化");
        }

        EndpointIn endpointIn = new EndpointIn()
                .url(URI.create(url))
                .description(description);

        return svixClient.getEndpoint().create(appId, endpointIn);
    }

    /**
     * 发送 Webhook 消息
     *
     * @param appId     应用程序 ID
     * @param eventType 事件类型（如：invoice.paid, user.created）
     * @param payload   消息负载（JSON 数据）
     * @return 发送的消息信息
     */
    public MessageOut sendMessage(String appId, String eventType, Object payload) throws IOException, ApiException {
        if (svixClient == null) {
            throw new IllegalStateException("Svix 客户端未初始化");
        }

        // 将 payload 转换为 JSON 字符串
        String payloadJson = convertToJson(payload);

        MessageIn messageIn = new MessageIn()
                .eventType(eventType)
                .payload(payloadJson);

        return svixClient.getMessage().create(appId, messageIn);
    }

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param obj 要转换的对象
     * @return JSON 字符串
     */
    private String convertToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        // 简单的对象转 JSON，生产环境建议使用 Jackson 或 Gson
        try {
            return obj.toString();
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}
