package com.api_store.dto.response;

import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribeResponse {

    Long Id;
    Long apiId;
    String apiName;
    String apiKey;
    String message;
    public SubscribeResponse(Long apiId, String apiKey, String apiName, String message,Long id){
        this.apiId=apiId;
        this.apiKey=apiKey;
        this.apiName=apiName;
        this.message=message;
        this.Id=id;
    }
    public SubscribeResponse(String message){
        this.message=message;
    }


}
