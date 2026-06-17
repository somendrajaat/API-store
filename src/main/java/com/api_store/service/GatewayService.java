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

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

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


    public ResponseEntity<String> proxy(Long apiId,String apiKey,HttpServletRequest request) {

        SubscriptionEntity subscription =
                validateApiKey(apiKey);

        validateApiOwnership(subscription, apiId);

        ApiEntity api = getApi(apiId);

        String targetUrl =
                buildTargetUrl(api, request, apiId);

        String body =
                extractBody(request);

        return forwardRequest(
                request,
                targetUrl,
                body
        );
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
            throw new RuntimeException("Invalid API Key");
        }
       return validSubscription;

    }
    private void validateApiOwnership(
            SubscriptionEntity subscription,
            Long apiId) {

        if(!subscription.getApi()
                .getId()
                .equals(apiId)) {

            throw new RuntimeException(
                    "API key does not belong to this API");
        }

    }
    private ApiEntity getApi(Long apiId) {
        return apiRepository
                .findById(apiId)
                .orElseThrow(() ->
                        new RuntimeException("API not found"));
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
            throw new RuntimeException("Failed to read request body", e);
        }
        return body;
    }
    private ResponseEntity<String> forwardRequest(
            HttpServletRequest request,
            String targetUrl,
            String body) {
        HttpMethod method =
                HttpMethod.valueOf(
                        request.getMethod()
                );
        return webClient
                .method(method)
                .uri(targetUrl)
                .headers(headers -> {

                    Enumeration<String> headerNames =
                            request.getHeaderNames();

                    while(headerNames.hasMoreElements()) {

                        String header =
                                headerNames.nextElement();

                        if(
                                !header.equalsIgnoreCase("X-API-KEY")
                                        && !header.equalsIgnoreCase("Host")
                                        && !header.equalsIgnoreCase("Connection")
                        ) {
                            headers.add(
                                    header,
                                    request.getHeader(header)
                            );
                        }
                    }
                })
                .bodyValue(body)
                .retrieve()
                //this helps in returning the status code of the API call. our server is the client at this point.
                .toEntity(String.class)
                .block();
    }

}
