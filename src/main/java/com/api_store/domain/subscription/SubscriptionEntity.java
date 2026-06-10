package com.api_store.domain.subscription;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "subscriptions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_subscription_user_api",
                        columnNames = {"user_id", "api_id"}
                )
        }
)
@Getter
@Setter
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "api_id")
    private ApiEntity api;

    @Column(name = "api_key_hash", nullable = false)
    private String apiKeyHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected SubscriptionEntity() {
    }

    public SubscriptionEntity(UserEntity user, ApiEntity api, String apiKeyHash) {
        this.user = user;
        this.api = api;
        this.apiKeyHash = apiKeyHash;
        this.createdAt = LocalDateTime.now();
    }



}