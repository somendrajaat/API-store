package com.api_store.service;

import com.api_store.domain.api.ApiEntity;
import com.api_store.domain.user.UserEntity;
import com.api_store.dto.request.CreateApiRequest;
import com.api_store.dto.response.ApiResponse;
import com.api_store.repository.ApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiService {
    @Autowired
    private ApiRepository apiRepository;
    @Autowired
    private AuthService authService;

    public ApiResponse createApi(CreateApiRequest dto) {
        UserEntity user = authService.getCurrentUser();
        ApiEntity api= new ApiEntity();
        api.setName(dto.getName());
        api.setDescription(dto.getDescription());
        api.setBaseUrl(dto.getBaseUrl());
        api.setOwner(user);
        apiRepository.save(api);

        return new ApiResponse(api.getName(),
                api.getDescription(),
                api.getBaseUrl(),
                null);
    }

    public List<ApiResponse> getApis() {
        UserEntity user = authService.getCurrentUser();
        List<ApiEntity> apiEntities = apiRepository.findByOwner(user);
        return apiEntities.stream().map(api -> new ApiResponse(
                api.getName(),
                api.getDescription(),
                api.getBaseUrl(),
                null
        )).toList();
    }
}
