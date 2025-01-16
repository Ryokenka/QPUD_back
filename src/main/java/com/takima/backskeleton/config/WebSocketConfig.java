package com.takima.backskeleton.config;

import com.takima.backskeleton.handler.QuizWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configures WebSocket endpoints and enables scheduling for lobby cleanup tasks.
 */
@Configuration
@EnableWebSocket
@EnableScheduling
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private QuizWebSocketHandler quizWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Map your WebSocket handler to an endpoint, e.g. "/game"
        // .setAllowedOrigins("*") is only for dev/demo; restrict in production
        registry.addHandler(quizWebSocketHandler, "/game")
                .setAllowedOrigins("*");
    }
}
