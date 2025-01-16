package com.takima.backskeleton.handler;

import com.takima.backskeleton.service.LobbyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Handles raw WebSocket text messages. Delegates to LobbyManager for parsing.
 */
@Component
public class QuizWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private LobbyManager lobbyManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Called when a new client connects.
        // If you need to do any special initialization, you could do it here.
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Forward the message payload (JSON) to LobbyManager
        String payload = message.getPayload();
        lobbyManager.dispatchMessage(session, payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status)
            throws Exception {
        // Called when a client disconnects.
        // If you want to remove them from a lobby, you can do it in LobbyManager or here.
        // For example:
        // lobbyManager.terminateSession(session);
    }
}
