package com.demo.service.user.controller;

import com.demo.service.user.entity.UserModel;
import com.demo.service.user.vo.Msg;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    private static final List<UserModel> list = new ArrayList<>();

    static {
        for (int i = 0; i < 10; i++) {
            list.add(new UserModel(i, "name-" + i));
        }
    }

    @GetMapping
    public Msg<List<UserModel>> list() {
        return new Msg<>(list);
    }


}
