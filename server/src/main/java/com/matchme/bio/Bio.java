package com.matchme.bio;

import com.matchme.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "bio")
@Getter
@Setter
public class Bio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // one bio per user (unique user_id in DB)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // matching fields
    @Column(name = "hobbies")
    private String hobbies;

    @Column(name = "music_preferences")
    private String musicPreferences;

    @Column(name = "food_preferences")
    private String foodPreferences;

    @Column(name = "interests")
    private String interests;

    @Column(name = "looking_for")
    private String lookingFor;

    // timestamps
    @Column (name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
}
