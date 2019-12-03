package com.demo.platform.servicegateway.config;

import com.demo.common.model.Msg;
import com.demo.platform.servicegateway.config.repository.ClientModel;
import com.demo.platform.servicegateway.config.repository.ClientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 客户端认证。
 * 客户端clientId, clientSecret match即可访问。
 */
@Slf4j
@Component
public class ClientAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String HEADER_KEY_CLIENT = "client-id";
    private static final String HEADER_KEY_SECRET = "client-secret";
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ObjectMapper objectMapper;
    private final ClientRepository clientRepository;

    public ClientAuthenticationFilter(ObjectMapper objectMapper, ClientRepository clientRepository) {
        this.objectMapper = objectMapper;
        this.clientRepository = clientRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String clientId = headers.getFirst(HEADER_KEY_CLIENT);
        String clientSecret = headers.getFirst(HEADER_KEY_SECRET);
        log.info("id: {}, secret: {}", clientId, clientSecret);
        if (StringUtils.isBlank(clientId) || StringUtils.isBlank(clientSecret)) {
            return clientNotFound(exchange);
        }


        //check client
        ClientModel clientModel = clientRepository.get(clientId);
        if (clientModel == null) {
            return clientNotFound(exchange);
        }

        boolean matches = passwordEncoder.matches(clientSecret, clientModel.getClientSecret());
        if (!matches) {
            return clientSecretIncorrect(exchange);
        }

        //check authentication
        List<String> authPattern = clientModel.getAuthPattern();
        if (authPattern != null && !authPattern.isEmpty()) {
            ServerWebExchangeDecorator decorator = (ServerWebExchangeDecorator) exchange;
            String originalPath = decorator.getDelegate().getRequest().getPath().toString();
            for (String pattern : authPattern) {
                boolean match = pathMatcher.match(pattern, originalPath);
                if (match) {
                    return chain.filter(exchange);
                }
            }
        }

        return clientForbidden(exchange);
    }

    private Mono<Void> clientForbidden(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        String body = HEADER_KEY_CLIENT + "没权限访问";
        Msg<String> msg = new Msg<>(HttpStatus.FORBIDDEN.value(), body);
        return writeBody(response, msg);
    }

    private Mono<Void> clientSecretIncorrect(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        String body = HEADER_KEY_SECRET + "不正确";
        Msg<String> msg = new Msg<>(HttpStatus.UNAUTHORIZED.value(), body);
        return writeBody(response, msg);
    }

    private Mono<Void> clientNotFound(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        String body = "header " + HEADER_KEY_CLIENT + "和" + HEADER_KEY_SECRET + "不能为空或client不存在";
        Msg<String> msg = new Msg<>(HttpStatus.UNAUTHORIZED.value(), body);
        return writeBody(response, msg);
    }

    private Mono<Void> writeBody(ServerHttpResponse response, Msg<String> msg) {
        DataBuffer write = response.bufferFactory().allocateBuffer().write(serialize(msg).getBytes(StandardCharsets.UTF_8));
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        return response.writeAndFlushWith(Mono.just(Mono.just(write)));
    }

    @Override
    public int getOrder() {
        return FilterOrderedConstant.CLIENT_AUTHENTICATION_FILTER_ORDER;
    }

    private <T> String serialize(T t) {
        String str;
        try {
            str = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        return str;
    }
}
