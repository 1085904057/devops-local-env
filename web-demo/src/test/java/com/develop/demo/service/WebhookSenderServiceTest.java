package com.develop.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.svix.Webhook;
import com.svix.exceptions.WebhookSigningException;
import com.svix.models.MessageIn;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Webhook 发送服务测试
 * 演示使用 Svix 规范发送和验证 Webhook 消息
 */
@SpringBootTest
public class WebhookSenderServiceTest {

    private static final String SIGNING_SECRET = "whsec_MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw";

    /**
     * 测试：简化版的 Webhook 发送（不使用 Svix SDK 的模型类）
     */
    public static void main(String[] args) throws WebhookSigningException, JsonProcessingException {
        Webhook webhook = new Webhook(SIGNING_SECRET);

        long timestamp = System.currentTimeMillis() / 1000;
        String transId = "VS20260324001";
        String payload = new MessageIn()
                .eventType("token.paid")
                .eventId(transId)
                .payload("""
                        {
                          "transId": "VS20260324001",
                          "gooseUid": "user_abc123",
                          "amount": 1000,
                          "remark": "Purchase 100 tokens bonus"
                        }
                        """)
                .toJson();
        String signature = webhook.sign(transId, timestamp, payload);

        HashMap<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("webhook-id", Arrays.asList(transId));
        headerMap.put("webhook-timestamp", Arrays.asList(String.valueOf(timestamp)));
        headerMap.put("webhook-signature", Arrays.asList(signature));
        HttpHeaders httpHeaders = HttpHeaders.of(headerMap, (s, s2) -> true);
        RestClient restClient = RestClient.create();

        RestClient.ResponseSpec retrieve = restClient.post()
                .uri(URI.create("http://localhost:8080" + "/api/webhooks/receive"))
                .header("Content-Type", "application/json")
                .headers(headers -> {
                    httpHeaders.map().forEach((name, values) ->
                            values.forEach(value -> headers.add(name, value)));
                })
                .body(payload)
                .retrieve();

        System.out.println("📤 简单测试 - Payload: " + payload);
    }
}
