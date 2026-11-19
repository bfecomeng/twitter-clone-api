package com.workintech.twitter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

// Sinif adi "Like" degil: LIKE hem SQL'de hem JPQL'de anahtar kelime.
@Entity
@Table(
        name = "likes",
        // Ayni kullanici ayni tweet'i ikinci kez begenemez; veritabani garanti eder.
        uniqueConstraints = @UniqueConstraint(
                name = "uk_like_user_tweet",
                columnNames = {"user_id", "tweet_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class TweetLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tweet_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tweet tweet;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
