package com.takima.backskeleton.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "room_settings")
@Getter
@Setter
@NoArgsConstructor
public class RoomSettings {

    @Id
    private Long roomId;

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "room_id")
    private Room room;

    @NotNull(message = "Number of questions must be specified")
    @Min(value = 1, message = "There must be at least one question")
    @Column(nullable = false, name = "num_questions")
    private Integer numQuestions;

    @Min(value = 30, message = "Time limit must be at least 30 second")
    @Column(name = "time_limit")
    private Integer timeLimit;

    // Many-to-Many relationship with Theme
    @ManyToMany
    @JoinTable(
            name = "room_settings_themes",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private List<Theme> themes;

    // Many-to-Many relationship with QuestionType
    @ManyToMany
    @JoinTable(
            name = "room_settings_types",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private List<QuestionType> questionTypes;

    // Private constructor for Builder
    private RoomSettings(Builder builder) {
        this.roomId = builder.roomId;
        this.room = builder.room;
        this.numQuestions = builder.numQuestions;
        this.timeLimit = builder.timeLimit;
        this.themes = builder.themes;
        this.questionTypes = builder.questionTypes;
    }

    // Builder Class
    public static class Builder {
        private Long roomId;
        private Room room;
        private Integer numQuestions;
        private Integer timeLimit;
        private List<Theme> themes;
        private List<QuestionType> questionTypes;

        public Builder room(Room room) {
            this.room = room;
            this.roomId = room.getId();
            return this;
        }

        public Builder numQuestions(Integer numQuestions) {
            this.numQuestions = numQuestions;
            return this;
        }

        public Builder timeLimit(Integer timeLimit) {
            this.timeLimit = timeLimit;
            return this;
        }

        public Builder themes(List<Theme> themes) {
            this.themes = themes;
            return this;
        }

        public Builder questionTypes(List<QuestionType> questionTypes) {
            this.questionTypes = questionTypes;
            return this;
        }

        public RoomSettings build() {
            RoomSettings roomSettings = new RoomSettings(this);
            validateRoomSettings(roomSettings);
            return roomSettings;
        }

        private void validateRoomSettings(RoomSettings roomSettings) {
            // Additional custom validations can be added here
        }
    }
}
