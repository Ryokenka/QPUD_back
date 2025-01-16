package com.takima.backskeleton.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a quiz question with four multiple-choice options.
 * correctAnswer is stored as an integer:
 *   0 -> choiceA
 *   1 -> choiceB
 *   2 -> choiceC
 *   3 -> choiceD
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")  // matches the table name in PostgreSQL
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "choice_a")
    private String choiceA;

    @Column(name = "choice_b")
    private String choiceB;

    @Column(name = "choice_c")
    private String choiceC;

    @Column(name = "choice_d")
    private String choiceD;

    /**
     * Integer that indicates the correct choice:
     *  - 0 for choiceA
     *  - 1 for choiceB
     *  - 2 for choiceC
     *  - 3 for choiceD
     */
    @Column(name = "correct_answer")
    private int correctAnswer;
}
