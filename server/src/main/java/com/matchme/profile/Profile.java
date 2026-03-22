package com.matchme.profile;

import com.matchme.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "profiles")
@Getter
@Setter
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One profile per user (unique user_id in DB)
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    // "About me" free text
    @Column(name = "about_me")
    private String aboutMe;

    // URL to profile picture (can be null)
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    // Simple city/location string for now
    @Column(name = "location", length = 100)
    private String location;

    // Timestamps
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

}
