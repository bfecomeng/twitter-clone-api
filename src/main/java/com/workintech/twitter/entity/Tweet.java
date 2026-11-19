package com.workintech.twitter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tweets")
@Getter
@Setter
@NoArgsConstructor
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tweet bos olamaz")
    @Size(max = 280, message = "Tweet en fazla 280 karakter olabilir")
    @Column(nullable = false, length = 280)
    private String content;

    // Cok tweet -> tek kullanici. Anonim tweet olmamali: hem @NotNull hem nullable=false.
    // LAZY: tweet'i cektigimizde kullaniciyi ancak ihtiyac olursa ayri sorguyla getirir.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Kayit her guncellendiginde (UPDATE'ten hemen once) cagrilir.
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
