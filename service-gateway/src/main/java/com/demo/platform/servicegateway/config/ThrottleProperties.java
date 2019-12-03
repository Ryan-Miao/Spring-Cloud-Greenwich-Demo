package com.demo.platform.servicegateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "throttle")
public class ThrottleProperties {
    private int capacity=1;
    private int refillTokens=1;
    private int refillPeriod=3;
}
