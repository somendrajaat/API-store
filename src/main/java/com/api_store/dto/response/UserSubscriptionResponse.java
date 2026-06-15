package com.api_store.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserSubscriptionResponse {
    Long subscriptionId;
    Long apiId;
    String apiName;
    String apiKey;
    LocalDateTime subscribedAt;
    public UserSubscriptionResponse(Long subscriptionId,String apiKey,Long apiId, String apiName,LocalDateTime subscribedAt){
        this.subscriptionId=subscriptionId;
        this.apiKey=apiKey;
        this.apiId=apiId;
        this.apiName=apiName;
        this.subscribedAt=subscribedAt;
    }

}
