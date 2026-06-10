package com.api_store.repository;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.subscription.SubscriptionEntity;
import com.api_store.domain.user.UserEntity;
import com.api_store.dto.response.UserSubscriptionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface subscriptionRepository extends JpaRepository<SubscriptionEntity,Long> {

    boolean existsByUserAndApi(UserEntity user, ApiEntity api);

    List<UserSubscriptionResponse> findByUser(UserEntity user);
}
