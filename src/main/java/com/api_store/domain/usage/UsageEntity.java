package com.api_store.domain.usage;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.subscription.SubscriptionEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name="api_usage")
@Getter
@Setter
public class UsageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id")
    private ApiEntity api;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private SubscriptionEntity subscription;
    private String method;
    private Integer statusCode;
    private Long latencyMs;
    @CreationTimestamp
    private LocalDateTime createdAt;
    public UsageEntity(ApiEntity api,
                       SubscriptionEntity subscription,
                       String method,
                       Integer statusCode,
                       Long latencyMs){
        this.api=api;
        this.subscription=subscription;
        this.method=method;
        this.statusCode=statusCode;
        this.latencyMs=latencyMs;
    }

    public UsageEntity() {

    }
}
