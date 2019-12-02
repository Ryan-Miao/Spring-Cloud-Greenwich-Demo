package com.demo.service.user.vo;

import lombok.Data;

@Data
public class Msg<T> {

    public static Integer SUCCESS = 0;
    private Integer code;
    private String msg;
    private T data;

    public Msg(T t) {
        this.data = t;
        this.code = SUCCESS;
        this.msg = "success";
    }

    public Msg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
