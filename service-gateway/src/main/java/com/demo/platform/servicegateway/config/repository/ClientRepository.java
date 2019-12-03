package com.demo.platform.servicegateway.config.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ClientRepository {

    private final Map<String, ClientModel> clientModelMap = new ConcurrentHashMap<>();

    private final ClientMapper clientMapper;

    @Autowired
    public ClientRepository(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    @Scheduled(fixedDelay = 10000)
    public void refreshClients() {
        List<ClientModel> list = clientMapper.list();
        for (ClientModel clientModel : list) {
            String authPatternStr = clientModel.getAuthPatternStr();
            List<String> authPatternList = new ArrayList<>();
            if (StringUtils.isNotBlank(authPatternStr)) {
                String[] split = authPatternStr.split(",");
                authPatternList.addAll(Arrays.asList(split));
            }
            clientModel.setAuthPattern(authPatternList);
            clientModelMap.put(clientModel.getClientId(), clientModel);
        }
        //remove not active client
        Set<String> clientIds = list.stream().map(ClientModel::getClientId).collect(Collectors.toSet());
        clientModelMap.entrySet().removeIf(entry -> !clientIds.contains(entry.getKey()));
    }

    public ClientModel get(String clientId) {
        return clientModelMap.get(clientId);
    }
}
