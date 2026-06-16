package com.api_store.service;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.subscription.SubscriptionEntity;
import com.api_store.repository.ApiRepository;
import com.api_store.repository.SubscriptionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GatewayService {
    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    ApiRepository apiRepository;
    @Autowired
    WebClient webClient;
    @Autowired
    PasswordEncoder passwordEncoder;


    // this function just works. but soon it'll need serious exception handling + optimization
    public ResponseEntity<String> proxy(Long apiId, String apiKey,
                                        HttpServletRequest request) {

        List<SubscriptionEntity> subs=subscriptionRepository.findAll();
        SubscriptionEntity validSubscription = null;

        for (SubscriptionEntity sub : subs) {

            if (passwordEncoder.matches(
                    apiKey,
                    sub.getApiKeyHash())) {

                validSubscription = sub;
                break;
            }
        }
        if(validSubscription == null){
            throw new RuntimeException("Invalid API Key");
        }
        if(!validSubscription.getApi()
                .getId()
                .equals(apiId)) {

            throw new RuntimeException(
                    "API key does not belong to this API");
        }
        ApiEntity api = apiRepository
                .findById(apiId)
                .orElseThrow(() ->
                        new RuntimeException("API not found"));
        String requestUri =
                request.getRequestURI();

        String prefix =
                "/gateway/" + apiId;

        String remainingPath =
                requestUri.substring(prefix.length());
        String targetUrl =
                api.getBaseUrl() + remainingPath;
        if(request.getQueryString() != null){
            targetUrl += "?" + request.getQueryString();
        }
        HttpMethod method =
                HttpMethod.valueOf(
                        request.getMethod()
                );
        // need to optimize later
        ResponseEntity<String> response= webClient
                .method(method)
                .uri(targetUrl)
                .retrieve()
                .toEntity(String.class)
                .block();
        return response;
    }
}
