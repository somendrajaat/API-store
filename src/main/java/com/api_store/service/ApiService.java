package com.api_store.service;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.subscription.SubscriptionEntity;
import com.api_store.domain.user.UserEntity;
import com.api_store.dto.request.CreateApiRequest;
import com.api_store.dto.response.ApiResponse;
import com.api_store.dto.response.SubscribeResponse;
import com.api_store.dto.response.UserSubscriptionResponse;
import com.api_store.repository.ApiRepository;
import com.api_store.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiService {
    @Autowired
    private ApiRepository apiRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /*    This is to create API
           it takes json as
           name:
           description:
           baseurl:

           and and and it does not checks if the api is already created or not
      * */
    public ApiResponse createApi(CreateApiRequest dto) {
        UserEntity user = authService.getCurrentUser();
        ApiEntity api= new ApiEntity();
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setBaseUrl(dto.getBaseUrl());
        api.setOwner(user);

        apiRepository.save(api);

        return new ApiResponse(
                api.getId(),
                api.getName(),
                api.getDescription(),
                api.getBaseUrl(),
                null);
    }

    /*
    It takes greps the list of all the APIs a user holds

     */
    public List<ApiResponse> getApis() {
        UserEntity user = authService.getCurrentUser();
        List<ApiEntity> apiEntities = apiRepository.findByOwner(user);
        return apiEntities.stream().map(api -> new ApiResponse(
                api.getId(),
                api.getName(),
                api.getDescription(),
                api.getBaseUrl(),
                null
        )).toList();
    }

    public SubscribeResponse subscribeApi(Long id) {
        UserEntity user=authService.getCurrentUser();
        Optional<ApiEntity> api=apiRepository.findById(id);
        if(api.isEmpty()){
           return new SubscribeResponse("API does not exists");
        }
        boolean alreadySubscribed =
                subscriptionRepository.existsByUserAndApi(user, api.get());
        if(alreadySubscribed){
            return new SubscribeResponse("API already subscribed");
        }

        String apiKey=generateApiKey();
        String apiKeyHash=passwordEncoder.encode(apiKey);

        SubscriptionEntity subscriptionEntity=new SubscriptionEntity(
                user,
                api.get(),
                apiKeyHash
        );

        subscriptionRepository.save(subscriptionEntity);
        return new SubscribeResponse(
                api.get().getId(),
                apiKey,
                api.get().getName(),
                "Success"
        );
    }

    private String generateApiKey() {
        return "sk_" + UUID.randomUUID().toString().replace("-", "");
    }


    public List<UserSubscriptionResponse> getAllSubscription() {
        UserEntity user = authService.getCurrentUser();
        return subscriptionRepository.findByUser(user).stream()
                .map(sub-> new UserSubscriptionResponse(
                        sub.getId(),
                        sub.getApiKeyHash(),
                        sub.getApi().getId(),
                        sub.getApi().getName(),
                        sub.getCreatedAt()
                )).toList();
    }

    public void delete(Long id) {
        subscriptionRepository.deleteById(id);
    }
}
