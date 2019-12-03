package com.demo.platform.servicegateway.config.repository;


import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.*;

public class ClientRepositoryTest {
    @Test
    public void testEncode() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("123456");
        System.out.println(encode);
    }

}
