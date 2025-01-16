package com.takima.backskeleton.model;

import org.springframework.web.socket.WebSocketSession;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a single player in a lobby, with a chosen username.
 */
@Data
@AllArgsConstructor
public class Player {

    private String sessionId;
    private String username;
    private WebSocketSession session;

}
