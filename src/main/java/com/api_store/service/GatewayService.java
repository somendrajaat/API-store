package com.api_store.service;

import com.api_store.domain.usage.UsageEntity;
import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.subscription.SubscriptionEntity;
import com.api_store.exception.ForbiddenException;
import com.api_store.exception.ResourceNotFoundException;
import com.api_store.exception.TooManyRequestsException;
import com.api_store.repository.ApiRepository;
import com.api_store.repository.SubscriptionRepository;
import com.api_store.repository.UsageRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GatewayService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ApiRepository apiRepository;
    @Autowired
    private WebClient webClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RateLimitService rateLimitService;
    @Autowired
    private UsageRepository usageRepository;


    public ResponseEntity<String> proxy(Long apiId,String apiKey,HttpServletRequest request) {


        SubscriptionEntity subscription =
                validateApiKey(apiKey);

        if (!rateLimitService.allowRequest(apiKey)) {
            throw new TooManyRequestsException("Too Many Request");
        }
        validateApiOwnership(subscription, apiId);

        ApiEntity api = getApi(apiId);
        long start = System.currentTimeMillis();
        String targetUrl =
                buildTargetUrl(api, request, apiId);

        String body =
                extractBody(request);

        HttpMethod method =
                HttpMethod.valueOf(request.getMethod());

        try {

            ResponseEntity<String> response =
                    webClient
                            .method(method)
                            .uri(targetUrl)
                            .headers(headers ->
                                    copyHeaders(request, headers))
                            .bodyValue(body)
                            .retrieve()
                            .toEntity(String.class)
                            .block();

            saveUsage(
                    api,
                    subscription,
                    request.getMethod(),
                    response.getStatusCode().value(),
                    System.currentTimeMillis() - start
            );

            return response;

        } catch (WebClientResponseException e) {

            saveUsage(
                    api,
                    subscription,
                    request.getMethod(),
                    e.getStatusCode().value(),
                    System.currentTimeMillis() - start
            );

            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }
    }

    private void saveUsage(ApiEntity api, SubscriptionEntity subscription, String method, int value, long latency) {
        UsageEntity usage=new UsageEntity(api,subscription,method,value,latency);
        usageRepository.save(usage);
    }


    private void copyHeaders(
            HttpServletRequest request,
            HttpHeaders headers) {

        Enumeration<String> headerNames =
                request.getHeaderNames();

        while (headerNames.hasMoreElements()) {

            String header =
                    headerNames.nextElement();

            if (!header.equalsIgnoreCase("X-API-KEY")
                    && !header.equalsIgnoreCase("Host")
                    && !header.equalsIgnoreCase("Connection")) {

                headers.add(
                        header,
                        request.getHeader(header)
                );
            }
        }
    }

    private SubscriptionEntity validateApiKey(
            String apiKey) {
        List<SubscriptionEntity> subs=subscriptionRepository.findAll();
        SubscriptionEntity validSubscription = null;
        // its O(n) not sure if i can optimize that
        for (SubscriptionEntity sub : subs) {

            if (passwordEncoder.matches(
                    apiKey,
                    sub.getApiKeyHash())) {

                validSubscription = sub;
                break;
            }
        }

        //need to add better exceptoon handling
        if(validSubscription == null){
            throw new ResourceNotFoundException("Subscription does not exists");
        }
       return validSubscription;

    }
    private void validateApiOwnership(
            SubscriptionEntity subscription,
            Long apiId) {

        if(!subscription.getApi()
                .getId()
                .equals(apiId)) {

            throw new ResourceNotFoundException("Api key does not belong to the current owner");
        }

    }
    private ApiEntity getApi(Long apiId) {
        return apiRepository
                .findById(apiId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Api with Id: "+apiId+" does not exists"));
    }
    private String buildTargetUrl(
            ApiEntity api,
            HttpServletRequest request,
            Long apiId) {
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
        return targetUrl;
    }
    private String extractBody(
            HttpServletRequest request) {
        String body;
        try {
            body = request.getReader()
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new ForbiddenException("Failed to read request body "+ e);
        }
        return body;
    }



}
