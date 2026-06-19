package com.api_store.repository;

import com.api_store.domain.usage.UsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageRepository extends JpaRepository<UsageEntity,Long> {

}
