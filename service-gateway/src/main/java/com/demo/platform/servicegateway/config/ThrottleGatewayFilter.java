package com.demo.platform.servicegateway.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局限流拦截器。
 * 采用bucket4j实现内存级别的简单的Ip限流。
 * 配置
 * throttle:
 *   # 令牌最大容量
 *   capacity: 1
 *   # 每次新增多少令牌
 *   refillTokens: 1
 *   # 多少秒新增一次
 *   refillPeriod: 3
 *
 *  实际使用中，应当将令牌存储在redis中。本处只是演示用法，每3s才允许请求一次。
 *
 */
@Slf4j
@Component
public class ThrottleGatewayFilter implements GlobalFilter, Ordered {

    private ThrottleProperties properties;
    private static final Map<String, Bucket> CACHE = new ConcurrentHashMap<>();

    public ThrottleGatewayFilter(ThrottleProperties properties) {
        this.properties = properties;
    }

    private Bucket createNewBucket(){
        Refill refill = Refill.greedy(properties.getRefillTokens(), Duration.ofSeconds(properties.getRefillPeriod()));
        Bandwidth limit = Bandwidth.classic(properties.getCapacity(), refill);
        return Bucket4j.builder().addLimit(limit).build();
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        Bucket bucket = CACHE.computeIfAbsent(ip, k -> createNewBucket());
        log.info("ip: {}, bucket: {}", ip, bucket.getAvailableTokens());
        boolean consumed = bucket.tryConsume(1);
        if (consumed) {
            return chain.filter(exchange);
        }
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return FilterOrderedConstant.IP_LIMIT_FILTER_ORDER;
    }
}
