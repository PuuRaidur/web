package com.matchme.bio;

import com.matchme.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "bios")
@Getter
@Setter
public class Bio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One bio per user (unique user_id in DB)
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Bio data points for recommendations
    @Column(name = "interests", columnDefinition = "text[]")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> interests;

    @Column(name = "hobbies", columnDefinition = "text[]")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> hobbies;

    @Column(name = "music_taste", columnDefinition = "text[]")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> musicTaste;

    @Column(name = "age")
    private Integer age;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "company", length = 100)
    private String company;

    @Column(name = "education", length = 100)
    private String education;

    @Column(name = "relationship_status", length = 50)
    private String relationshipStatus;

    // Timestamps
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
}
