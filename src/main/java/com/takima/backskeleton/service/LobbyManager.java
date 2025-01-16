package com.takima.backskeleton.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takima.backskeleton.model.GameHistory;
import com.takima.backskeleton.model.Question;
import com.takima.backskeleton.repository.GameHistoryRepository; // NEW
import com.takima.backskeleton.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LobbyManager {

    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();

    @Autowired
    private QuestionRepository questionRepository;

    // 1) Autowire the GameHistoryRepository
    @Autowired
    private GameHistoryRepository gameHistoryRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Scheduled(cron = "0 */5 * * * *")
    public void cleanupLobbies() {
        Instant now = Instant.now();
        for (Iterator<Map.Entry<String, Lobby>> it = lobbies.entrySet().iterator(); it.hasNext();) {
            var entry = it.next();
            var lobby = entry.getValue();
            if (lobby.isExpiredOrFinished(now)) {
                it.remove();
            }
        }
    }

    public void dispatchMessage(WebSocketSession session, String messagePayload) {
        try {
            JsonNode root = mapper.readTree(messagePayload);
            String event = root.get("event").asText();
            JsonNode data = root.get("data");

            switch (event) {
                case "client.quiz.createLobby":
                    onCreateLobby(session, data);
                    break;
                case "client.quiz.joinLobby":
                    onJoinLobby(session, data);
                    break;
                case "client.quiz.start":
                    onStartQuiz(session);
                    break;
                case "client.quiz.answer":
                    onQuizAnswer(session, data);
                    break;
                case "client.quiz.requestState":
                    onRequestState(session);
                    break;
                case "client.reconnectQuiz":
                    onReconnectQuiz(session, data);
                    break;
                default:
                    // unknown event
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onCreateLobby(WebSocketSession session, JsonNode data) {
        int questionCount = data.has("questionCount") ? data.get("questionCount").asInt() : 5;
        String username = data.has("username") ? data.get("username").asText() : "Player-" + session.getId();

        List<Question> questions = questionRepository.findRandomQuestions(questionCount);
        Lobby lobby = new Lobby(questions, this); // pass "this" so QuizInstance can call back
        lobbies.put(lobby.getId(), lobby);

        lobby.addPlayer(session, username);
        WebSocketUtils.sendToSession(session, "server.lobbyCreated", Map.of("lobbyId", lobby.getId()));
    }

    private void onJoinLobby(WebSocketSession session, JsonNode data) {
        String lobbyId = data.get("lobbyId").asText();
        String username = data.has("username") ? data.get("username").asText() : "Player-" + session.getId();

        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            WebSocketUtils.sendToSession(session, "server.error", Map.of("message", "Lobby not found"));
            return;
        }
        if (lobby.isQuizStarted()) {
            WebSocketUtils.sendToSession(session, "server.error", Map.of("message", "Quiz already started"));
            return;
        }

        lobby.addPlayer(session, username);
        WebSocketUtils.sendToSession(session, "server.lobbyJoined", Map.of("lobbyId", lobbyId));
    }

    private void onStartQuiz(WebSocketSession session) {
        Lobby lobby = findLobbyBySession(session);
        if (lobby == null) {
            WebSocketUtils.sendToSession(session, "server.error", Map.of("message", "Not in a lobby"));
            return;
        }
        lobby.startQuiz();
    }

    private void onQuizAnswer(WebSocketSession session, JsonNode data) {
        int answerIndex = data.get("answerIndex").asInt();
        Lobby lobby = findLobbyBySession(session);
        if (lobby == null) {
            WebSocketUtils.sendToSession(session, "server.error", Map.of("message", "Not in a lobby"));
            return;
        }
        lobby.getQuizInstance().processAnswer(session, answerIndex);
    }

    private void onRequestState(WebSocketSession session) {
        Lobby lobby = findLobbyBySession(session);
        if (lobby == null) {
            WebSocketUtils.sendToSession(session, "server.error", Map.of("message", "Not in a lobby"));
            return;
        }
        var quiz = lobby.getQuizInstance();
        int questionIndex = quiz.getCurrentQuestionIndex();
        Map<String, Integer> scoreboard = quiz.getScores();

        Map<String, String> sessionIdToUsername = new ConcurrentHashMap<>();
        for (var p : lobby.getPlayers().values()) {
            sessionIdToUsername.put(p.getSessionId(), p.getUsername());
        }

        Map<String, Object> usernameScoreMap = new ConcurrentHashMap<>();
        for (var entry : scoreboard.entrySet()) {
            String sid = entry.getKey();
            int sc = entry.getValue();
            String uname = sessionIdToUsername.getOrDefault(sid, sid);
            usernameScoreMap.put(uname, sc);
        }

        WebSocketUtils.sendToSession(session, "server.quiz.state", Map.of(
                "questionIndex", questionIndex,
                "scores", usernameScoreMap
        ));
    }

    private Lobby findLobbyBySession(WebSocketSession session) {
        for (Lobby lobby : lobbies.values()) {
            if (lobby.getPlayers().containsKey(session.getId())) {
                return lobby;
            }
        }
        return null;
    }

    // 2) Method that stores final scoreboard
    public void storeHistory(String lobbyId, Map<String, Integer> usernameScores) {
        try {
            String scoresJson = new ObjectMapper().writeValueAsString(usernameScores);

            var history = new GameHistory();
            history.setLobbyId(lobbyId);
            history.setFinishedAt(LocalDateTime.now());
            history.setFinalScoresJson(scoresJson);

            gameHistoryRepository.save(history);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void onReconnectQuiz(WebSocketSession session, JsonNode data) {
        String lobbyId = data.get("lobbyId").asText();
        String username = data.get("username").asText();

        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null || !lobby.isQuizStarted()) {
            WebSocketUtils.sendToSession(session, "server.error", Map.of(
                    "message", "No active quiz found in the specified lobby."
            ));
            return;
        }

        // Add player back to the lobby
        lobby.addPlayer(session, username);

        // Send current quiz state
        QuizInstance quiz = lobby.getQuizInstance();
        WebSocketUtils.sendToSession(session, "server.quiz.state", Map.of(
                "questionIndex", quiz.getCurrentQuestionIndex(),
                "scores", quiz.getScores(),
                "totalQuestions", quiz.getTotalQuestions(),
                "currentQuestion", quiz.getCurrentQuestionData()
        ));
    }

}


