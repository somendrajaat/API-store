package com.api_store.controller;

import com.api_store.dto.request.CreateApiRequest;
import com.api_store.dto.response.ApiResponse;
import com.api_store.dto.response.SubscribeResponse;
import com.api_store.dto.response.UserSubscriptionResponse;
import com.api_store.service.ApiService;
import com.api_store.service.GatewayService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApiController {
    @Autowired
    ApiService apiService;

    @PostMapping("/apis")
    public ApiResponse createApi(@RequestBody CreateApiRequest dto) {
        return apiService.createApi(dto);
    }
    @GetMapping("/apis")
    public List<ApiResponse> getApis(){
        return apiService.getApis();
    }

    @PostMapping("/apis/{id}/subscription")
    public SubscribeResponse subscribe(@PathVariable Long id){
        return apiService.subscribeApi(id);
    }

    @GetMapping("apis/me/subscription")
    public List<UserSubscriptionResponse> getAllSubscription(){
        return apiService.getAllSubscription();
    }
    @DeleteMapping("/subscriptions/{id}")
    public void delete(@PathVariable Long id){
        apiService.delete(id);
    }
    @GetMapping("apis/{apiId}/usage")
    public ResponseEntity<?> usageState(@PathVariable Long apiId){
        return apiService.usageStat(apiId);
    }
}
