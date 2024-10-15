package com.takima.backskeleton.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "theme")
@Getter
@NoArgsConstructor
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Theme name cannot be blank")
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    // Bidirectional Many-to-Many relationship with RoomSettings
    @ManyToMany(mappedBy = "themes")
    @JsonIgnore // Prevent infinite recursion during serialization
    private List<RoomSettings> roomSettings;


    private Theme(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.roomSettings = builder.roomSettings;
    }

    // Builder Class
    public static class Builder {
        private Long id;
        private String name;
        private List<RoomSettings> roomSettings;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setRoomSettings(List<RoomSettings> roomSettings) {
            this.roomSettings = roomSettings;
            return this;
        }

        public Theme build() {
            Theme theme = new Theme(this);
            validateTheme(theme);
            return theme;
        }

        private void validateTheme(Theme theme) {
            // Additional custom validations can be added here
        }
    }
}
