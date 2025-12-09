package com.sky.websocket;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    private static final Map<String, Session> sessionMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("与客户端 {} 建立WebSocket连接", sid);
        sessionMap.put(sid, session);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到客户端 {} 的消息: {}", sid, message);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("与客户端 {} 断开WebSocket连接", sid);
        sessionMap.remove(sid);
    }

    public void sendToAll(String message) {
        for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
            String sid = entry.getKey();
            Session session = entry.getValue();
            try {
                session.getBasicRemote().sendText(message);
                log.info("向客户端 {} 发送消息: {}", sid, message);
            } catch (Exception e) {
                log.error("向客户端 {} 发送消息失败: {}", sid, e.getMessage());
            }
        }
    }

    public void sendToAll(Object message) {
        String json = JSON.toJSONString(message);
        sendToAll(json);
    }
}
