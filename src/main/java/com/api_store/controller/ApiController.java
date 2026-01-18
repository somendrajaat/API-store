package com.api_store.controller;

import com.api_store.dto.request.CreateApiRequest;
import com.api_store.dto.response.ApiResponse;
import com.api_store.service.ApiService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
