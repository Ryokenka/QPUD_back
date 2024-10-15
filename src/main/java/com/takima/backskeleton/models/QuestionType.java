package com.takima.backskeleton.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "question_type")
@Getter
@NoArgsConstructor
public class QuestionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question type cannot be blank")
    @Column(nullable = false, name = "type", unique = true)
    private String type;

    // Bidirectional Many-to-Many relationship with RoomSettings
    @ManyToMany(mappedBy = "questionTypes")
    @JsonIgnore // Prevent infinite recursion during serialization
    private List<RoomSettings> roomSettings;

    // Private constructor to enforce immutability via Builder
    private QuestionType(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.roomSettings = builder.roomSettings;
    }

    // Builder Class
    public static class Builder {
        private Long id;
        private String type;
        private List<RoomSettings> roomSettings;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setRoomSettings(List<RoomSettings> roomSettings) {
            this.roomSettings = roomSettings;
            return this;
        }

        public QuestionType build() {
            QuestionType questionType = new QuestionType(this);
            validateQuestionType(questionType);
            return questionType;
        }

        private void validateQuestionType(QuestionType questionType) {
            // Additional custom validations can be added here
        }
    }
}
