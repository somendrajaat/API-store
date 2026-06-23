package com.api_store.repository;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.subscription.SubscriptionEntity;
import com.api_store.domain.user.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity,Long> {

    boolean existsByUserAndApi(UserEntity user, ApiEntity api);

    List<SubscriptionEntity> findByUser(UserEntity user);

    Optional<Object> findByIdAndUser(Long id, UserEntity user);
}
