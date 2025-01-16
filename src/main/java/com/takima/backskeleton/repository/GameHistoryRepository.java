package com.takima.backskeleton.repository;

import com.takima.backskeleton.model.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the GameHistory entity, allowing CRUD operations.
 */
@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    // By extending JpaRepository, we get basic methods like
    // save(), findAll(), findById(), deleteById(), etc.
}
