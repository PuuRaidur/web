package com.matchme.recommendations;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "dismissed_recommendations")
@Getter
@Setter
public class DismissedRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "dismissed_user_id", nullable = false)
    private Long dismissedUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
