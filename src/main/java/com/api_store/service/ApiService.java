package com.api_store.service;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.subscription.SubscriptionEntity;
import com.api_store.domain.usage.UsageEntity;
import com.api_store.domain.user.UserEntity;
import com.api_store.dto.request.CreateApiRequest;
import com.api_store.dto.response.ApiResponse;
import com.api_store.dto.response.SubscribeResponse;
import com.api_store.dto.response.UsageStat;
import com.api_store.dto.response.UserSubscriptionResponse;
import com.api_store.exception.BadRequestException;
import com.api_store.exception.ResourceNotFoundException;
import com.api_store.repository.ApiRepository;
import com.api_store.repository.SubscriptionRepository;
import com.api_store.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private UsageRepository usageRepository;

    /*    This is to create API
           it takes json as
           name:
           description:
           baseurl:

           and and and it does not checks if the api is already created or not
      * */
    public ResponseEntity<?> createApi(CreateApiRequest dto) {
        UserEntity user = authService.getCurrentUser();
        ApiEntity api= new ApiEntity();
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setBaseUrl(dto.getBaseUrl());
        api.setOwner(user);

        apiRepository.save(api);

        return ResponseEntity.ok(new ApiResponse(
                api.getId(),
                api.getName(),
                api.getDescription(),
                api.getBaseUrl(),
                null));
    }

    /*
    It takes greps the list of all the APIs a user holds

     */
    public ResponseEntity<?> getApis() {
        UserEntity user = authService.getCurrentUser();
        List<ApiEntity> apiEntities = apiRepository.findByOwner(user);
        return ResponseEntity.ok(apiEntities.stream().map(api -> new ApiResponse(
                api.getId(),
                api.getName(),
                api.getDescription(),
                api.getBaseUrl(),
                null
        )).toList());
    }

    public ResponseEntity<?> subscribeApi(Long id) {
        UserEntity user=authService.getCurrentUser();
        ApiEntity api=apiRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Api does not exist"));

        boolean alreadySubscribed =
                subscriptionRepository.existsByUserAndApi(user, api);

        if(alreadySubscribed){
            throw new BadRequestException("Api Already Subscribed");
        }

        String apiKey=generateApiKey();
        String apiKeyHash=passwordEncoder.encode(apiKey);

        SubscriptionEntity subscriptionEntity=new SubscriptionEntity(
                user,
                api,
                apiKeyHash
        );

        subscriptionRepository.save(subscriptionEntity);
        return ResponseEntity.ok(new SubscribeResponse(
                api.getId(),
                apiKey,
                api.getName(),
                "Success"
                ,subscriptionEntity.getId()
        ));
    }

    private String generateApiKey() {
        return "sk_" + UUID.randomUUID().toString().replace("-", "");
    }


    public ResponseEntity<?> getAllSubscription() {
        UserEntity user = authService.getCurrentUser();
        return ResponseEntity.ok(subscriptionRepository.findByUser(user).stream()
                .map(sub-> new UserSubscriptionResponse(
                        sub.getId(),
                        sub.getApiKeyHash(),
                        sub.getApi().getId(),
                        sub.getApi().getName(),
                        sub.getCreatedAt()
                )).toList());
    }

    public ResponseEntity<?> delete(Long id) {
        UserEntity user = authService.getCurrentUser();
        subscriptionRepository.findByIdAndUser(id,user)
                .orElseThrow(
                        ()->new ResourceNotFoundException(
                        "Subscription does not exits"
                ));
        subscriptionRepository.deleteById(id);
        return ResponseEntity.ok("Subscription with ID: "+id+" deleted successfully" );
    }


    public ResponseEntity<?> usageStat(Long apiId) {
        UserEntity user = authService.getCurrentUser();
        Optional<ApiEntity> api = apiRepository.findById(apiId);
        if(api.isEmpty()){
            return ResponseEntity.badRequest().body("API does not exists");
        }
//        if (!api.get().getOwner().getEmail().equals(user.getEmail())) {
//            return ResponseEntity.badRequest().body("Bad UserId");
//        }

        Long total=usageRepository.countByApi(api.get());
        Double averageLatency =
                usageRepository.averageLatency(apiId);

        Long success = usageRepository.countByApiAndStatusCodeBetween(api.get(),200,299);

        Double successRate = total == 0 ? 0.0 : (success * 100.0) / total;

        UsageStat dto = new UsageStat(total,averageLatency,successRate);

        return ResponseEntity.ok(dto);

    }
}
