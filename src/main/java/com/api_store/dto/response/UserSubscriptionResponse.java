package com.api_store.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

@Getter
@Setter
public class UserSubscriptionResponse {
    Long apiId;
    String apiName;
    public UserSubscriptionResponse(Long apiId, String apiName){
        this.apiId=apiId;
        this.apiName=apiName;
    }
}
