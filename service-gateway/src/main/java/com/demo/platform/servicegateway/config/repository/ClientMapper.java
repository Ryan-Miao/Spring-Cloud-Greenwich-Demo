package com.demo.platform.servicegateway.config.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClientMapper {
    @Select("Select * from gateway_client where status=0")
    List<ClientModel> list();
}
