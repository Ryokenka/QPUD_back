package com.takima.backskeleton.service;

import com.takima.backskeleton.model.Player;
import com.takima.backskeleton.model.Question;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a single lobby (room) where players gather for a quiz game.
 * The QuizInstance handles the actual quiz logic.
 */
public class Lobby {

    private final String id = UUID.randomUUID().toString();
    private final Instant createdAt = Instant.now();

    // Key: sessionId, Value: Player object
    private final Map<String, Player> players = new ConcurrentHashMap<>();

    private final QuizInstance quizInstance;
    private final LobbyManager manager; // <== reference to the manager
    private volatile boolean quizStarted = false;
    private volatile boolean finished = false;

    /**
     * Updated constructor: we now accept a LobbyManager reference
     * so QuizInstance can call manager.storeHistory(...) at end.
     */
    public Lobby(List<Question> questions, LobbyManager manager) {
        this.manager = manager;
        this.quizInstance = new QuizInstance(this, questions, manager);
    }

    /**
     * Adds a player to this lobby with the given username.
     */
    public void addPlayer(WebSocketSession session, String username) {
        Player player = new Player(session.getId(), username, session);
        players.put(session.getId(), player);

        broadcastLobbyState();
    }

    public void removePlayer(WebSocketSession session) {
        players.remove(session.getId());
        broadcastLobbyState();
    }

    public void startQuiz() {
        if (!quizStarted) {
            quizStarted = true;
            quizInstance.start();
        }
    }

    public void setFinished(boolean fin) {
        this.finished = fin;
    }

    public boolean isExpiredOrFinished(Instant now) {
        long ageSeconds = now.getEpochSecond() - createdAt.getEpochSecond();
        return finished || ageSeconds > (30 * 60);
    }

    private void broadcastLobbyState() {
        var data = Map.of(
                "playersCount", players.size(),
                "lobbyId", id,
                "quizStarted", quizStarted,
                "players", players.values().stream()
                        .map(Player::getUsername)
                        .toArray()
        );
        broadcast("server.lobbyState", data);
    }

    public void broadcast(String event, Object data) {
        for (Player p : players.values()) {
            WebSocketUtils.sendToSession(p.getSession(), event, data);
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public QuizInstance getQuizInstance() {
        return quizInstance;
    }

    public boolean isQuizStarted() {
        return quizStarted;
    }

    public LobbyManager getManager() {
        return manager;
    }
}
