package com.takima.backskeleton.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Stores completed quiz data for reference/leaderboards/history.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "game_history")
public class GameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lobbyId;

    private LocalDateTime finishedAt;

    /**
     * We can store a JSON or a string describing final scores:
     * e.g. {"Alice":2,"Bob":3}
     */
    @Lob
    private String finalScoresJson;

}

