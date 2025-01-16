package com.takima.backskeleton.repository;

import com.takima.backskeleton.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Question entity.
 * Provides basic CRUD methods and can define custom queries.
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Example of a custom method to retrieve a certain number of random questions.
     * NOTE: 'random()' is PostgreSQL-specific. For MySQL, you might use 'rand()'.
     */
    @Query(value = "SELECT * FROM questions ORDER BY random() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);
}

