package com.takima.backskeleton.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

public class WebSocketUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendToSession(WebSocketSession session, String event, Object data) {
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            String payload = objectMapper.writeValueAsString(
                    // e.g. {"event": "server.quiz.question", "data": {...}}
                    Map.of("event", event, "data", data)
            );
            session.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
