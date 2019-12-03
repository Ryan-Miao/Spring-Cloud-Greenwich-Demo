package com.demo.platform.servicegateway.config.repository;

import lombok.Data;

import java.util.List;

@Data
public class ClientModel {
    private String clientId;
    private String clientSecret;
    private List<String> authPattern;
    private String authPatternStr;

}
