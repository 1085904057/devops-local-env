package com.develop.demo.controller;

import com.develop.demo.service.WebhookSenderService;
import com.svix.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * Svix Webhook 控制器
 * 提供核心的 Webhook 端点注册和消息发布功能
 */
@RestController
@RequestMapping("/api/svix")
public class WebhookSenderController {

    @Autowired
    private WebhookSenderService webhookSenderService;

    /**
     * 注册 Webhook 端点
     * POST /api/svix/endpoints
     * 
     * @param request 包含应用程序 ID、端点 URL 的描述
     * @return 创建的端点信息
     */
    @PostMapping("/endpoints")
    public ResponseEntity<?> createEndpoint(@RequestBody Map<String, String> request) {
        try {
            String appId = request.get("appId");
            String url = request.get("url");
            String description = request.getOrDefault("description", "Webhook endpoint");

            if (appId == null || url == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "缺少必要参数",
                        "message", "需要提供 appId 和 url 参数"
                ));
            }

            var endpoint = webhookSenderService.createEndpoint(appId, url, description);
            return ResponseEntity.ok(endpoint);
        } catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.valueOf(e.getCode()))
                    .body(Map.of(
                            "error", "API 错误",
                            "message", e.getMessage()
                    ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "IO 错误",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "创建端点失败",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * 发布 Webhook 事件消息
     * POST /api/svix/messages
     * 
     * @param request 包含应用程序 ID、事件类型和负载的请求体
     * @return 发送的消息信息
     */
    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            String appId = (String) request.get("appId");
            String eventType = (String) request.get("eventType");
            Object payload = request.get("payload");

            if (appId == null || eventType == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "缺少必要参数",
                        "message", "需要提供 appId 和 eventType 参数"
                ));
            }

            var message = webhookSenderService.sendMessage(appId, eventType, payload);
            return ResponseEntity.ok(message);
        } catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.valueOf(e.getCode()))
                    .body(Map.of(
                            "error", "API 错误",
                            "message", e.getMessage()
                    ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "IO 错误",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "发送消息失败",
                            "message", e.getMessage()
                    ));
        }
    }
}
