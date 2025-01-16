package com.takima.backskeleton.service;

import com.takima.backskeleton.model.Question;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the actual quiz logic: distributing questions, checking answers, scoring, etc.
 */
public class QuizInstance {

    private final Lobby lobby;
    private final LobbyManager manager;  // reference to store history
    private final List<Question> questions;

    private int currentQuestionIndex = 0;
    private boolean quizInProgress = false;

    // sessionId -> score
    private final Map<String, Integer> scores = new ConcurrentHashMap<>();

    public QuizInstance(Lobby lobby, List<Question> questions, LobbyManager manager) {
        this.lobby = lobby;
        this.questions = questions;
        this.manager = manager;
    }

    public void start() {
        quizInProgress = true;
        currentQuestionIndex = 0;
        // initialize scores for each player
        for (String sid : lobby.getPlayers().keySet()) {
            scores.put(sid, 0);
        }
        lobby.broadcast("server.quiz.start", Map.of("message", "The quiz has started!"));
        sendCurrentQuestion();
    }

    private void sendCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            endQuiz();
            return;
        }
        Question question = questions.get(currentQuestionIndex);
        var data = Map.of(
                "questionText", question.getQuestionText(),
                "choices", new String[] {
                        question.getChoiceA(),
                        question.getChoiceB(),
                        question.getChoiceC(),
                        question.getChoiceD()
                },
                "questionIndex", currentQuestionIndex,
                "totalQuestions", questions.size()
        );
        lobby.broadcast("server.quiz.question", data);
    }

    public void processAnswer(WebSocketSession playerSession, int answerIndex) {
        if (!quizInProgress || currentQuestionIndex >= questions.size()) {
            return;
        }
        Question currentQuestion = questions.get(currentQuestionIndex);

        if (answerIndex == currentQuestion.getCorrectAnswer()) {
            String sessionId = playerSession.getId();
            int newScore = scores.getOrDefault(sessionId, 0) + 1;
            scores.put(sessionId, newScore);

            // Lookup player's username
            String username = lobby.getPlayers().get(sessionId).getUsername();

            lobby.broadcast("server.quiz.correctAnswer", Map.of(
                    "username", username,    // <-- use username here
                    "score", newScore
            ));

            currentQuestionIndex++;
            sendCurrentQuestion();
        } else {
            String sessionId = playerSession.getId();
            String username = lobby.getPlayers().get(sessionId).getUsername();

            lobby.broadcast("server.quiz.wrongAnswer", Map.of(
                    "username", username     // or "playerId" if you prefer
            ));
        }
    }

    private void endQuiz() {
        quizInProgress = false;
        lobby.setFinished(true);

        // finalScores = sessionId -> score
        Map<String, Integer> finalScores = new HashMap<>(scores);

        // Convert to username-based map for broadcast or DB
        Map<String, Integer> usernameScores = buildUsernameScores(lobby, finalScores);

        // 1) Save to DB using manager
        manager.storeHistory(lobby.getId(), usernameScores);

        // 2) Broadcast final results
        lobby.broadcast("server.quiz.gameOver", Map.of("scores", usernameScores));
    }

    private Map<String, Integer> buildUsernameScores(Lobby lobby, Map<String, Integer> sessionScores) {
        Map<String, Integer> result = new HashMap<>();
        for (var entry : sessionScores.entrySet()) {
            String sessionId = entry.getKey();
            int score = entry.getValue();
            String uname = lobby.getPlayers().get(sessionId).getUsername();
            result.put(uname, score);
        }
        return result;
    }
    public Map<String, Object> getCurrentQuestionData() {
        if (currentQuestionIndex >= questions.size()) return null;
        Question currentQuestion = questions.get(currentQuestionIndex);

        return Map.of(
                "questionText", currentQuestion.getQuestionText(),
                "choices", new String[] {
                        currentQuestion.getChoiceA(),
                        currentQuestion.getChoiceB(),
                        currentQuestion.getChoiceC(),
                        currentQuestion.getChoiceD()
                },
                "questionIndex", currentQuestionIndex,
                "totalQuestions", questions.size()
        );
    }
    public int getTotalQuestions() {
        return questions.size();
    }

    // GETTERS
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }
}


