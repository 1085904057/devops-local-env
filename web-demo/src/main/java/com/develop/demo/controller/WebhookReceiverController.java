package com.develop.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.svix.Webhook;
import com.svix.exceptions.WebhookVerificationException;
import com.svix.models.MessageOut;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.util.Arrays;
import java.util.Map;

/**
 * Webhook 接收测试端点
 * 用于接收和验证 Svix 发送的 Webhook 消息
 */
@Slf4j
@RestController
@RequestMapping("/api/webhooks")
public class WebhookReceiverController {

    // 本地测试使用的 signing secret
    private static final String SIGNING_SECRET = "whsec_MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw";

    /**
     * 接收 Svix Webhook 消息
     * POST /api/webhooks/receive
     * <p>
     * 这是一个示例端点，用于演示如何接收 Webhook 消息
     * 在实际使用中，你需要实现签名验证逻辑
     */
    @PostMapping("/receive")
    public void receiveWebhook(
            // 直接获取三个请求头的值
            @RequestHeader("webhook-id") String messageId,
            @RequestHeader("webhook-timestamp") String timestamp,
            @RequestHeader("webhook-signature") String signature,
            @RequestBody String requestBody,
            HttpServletResponse response) {

        System.out.println("📬 收到 Webhook 消息：" + requestBody);
        Webhook webhook = new Webhook(SIGNING_SECRET);
        try {
            HttpHeaders headers = HttpHeaders.of(
                    Map.of(
                            "webhook-id", Arrays.asList(messageId),
                            "webhook-timestamp", Arrays.asList(timestamp),
                            "webhook-signature", Arrays.asList(signature)
                    ), (name, value) -> true
            );
            webhook.verify(requestBody, headers);
            System.out.println("✅ Webhook 签名验证通过");

            MessageOut message = MessageOut.fromJson(requestBody);
            String eventType = message.getEventType();
            String payload = (String) message.getPayload();
            System.out.println("Message Id: " + messageId);
            System.out.println("Event Type: " + eventType);
            System.out.println("Payload: " + payload);
        } catch (WebhookVerificationException e) {
            log.warn("认证异常", e);
            response.setStatus(400);
        } catch (JsonProcessingException e) {
            log.warn("Payload解析异常", e);
            response.setStatus(400);
        }
        response.setStatus(200);
    }
}
