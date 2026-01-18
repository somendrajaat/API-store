package com.api_store.repository;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiRepository extends JpaRepository<ApiEntity, Long> {

    List<ApiEntity> findByOwner(UserEntity user);
}
