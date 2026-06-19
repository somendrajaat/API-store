package com.api_store.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UsageStat {
    private Long totalRequests;
    private Double averageLatency;
    private Double successRate;

    public UsageStat(Long total, Double averageLatency, Double successRate) {
        this.averageLatency=averageLatency;
        this.totalRequests=total;
        this.successRate=successRate;
    }
}
