package com.takima.backskeleton.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor

public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    @NotBlank(message = "Room name cannot be blank")
    private String name;

    @Column(nullable = false, name = "capacity")
    @NotNull(message = "Capacity must be specified")
    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;
    // Relationships

    // Many-to-Many relationship with User (users in the room)
    @ManyToMany
    @JoinTable(
            name = "user_in_room",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore // Prevent infinite recursion during serialization
    private List<User> users;

    // One-to-One relationship with RoomSettings
    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL)
    private RoomSettings roomSettings;

    // Constructors

    private Room(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.capacity = builder.capacity;
        this.users = builder.users;
        this.roomSettings = builder.roomSettings;
    }

    // Builder Class
    public static class Builder {
        private Long id;
        private String name;
        private Integer capacity;
        private List<User> users;
        private RoomSettings roomSettings;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder users(List<User> users) {
            this.users = users;
            return this;
        }

        public Builder roomSettings(RoomSettings roomSettings) {
            this.roomSettings = roomSettings;
            return this;
        }

        public Room build() {
            Room room = new Room(this);
            validateRoomObject(room);
            return room;
        }

        private void validateRoomObject(Room room) {
            // Custom validation logic
            if (room.getUsers() != null && room.getUsers().size() > room.getCapacity()) {
                throw new IllegalArgumentException("Number of users exceeds room capacity");
            }
        }
    }
}