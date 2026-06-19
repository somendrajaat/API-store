package com.api_store.repository;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.usage.UsageEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsageRepository extends JpaRepository<UsageEntity,Long> {



    @Query("""
SELECT AVG(u.latencyMs)
FROM UsageEntity u
WHERE u.api.id = :apiId
""")
    Double averageLatency(@Param("apiId") Long apiId);

    Long countByApi(ApiEntity api);
    Long countByApiAndStatusCodeBetween(
            ApiEntity api,
            Integer start,
            Integer end
    );
}
