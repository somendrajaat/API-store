package com.api_store.controller;

import com.api_store.service.GatewayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    @Autowired
    GatewayService gatewayService;


    @RequestMapping("/{apiId}/**")
    public ResponseEntity<?> proxy(
            @PathVariable long apiId,
            HttpServletRequest request){
        System.out.println("gateway hit");
        String apiKey=request.getHeader("X-API-KEY");
        Object response= gatewayService.proxy(apiId,apiKey,request);
        return ResponseEntity.ok(response);
    }

}
