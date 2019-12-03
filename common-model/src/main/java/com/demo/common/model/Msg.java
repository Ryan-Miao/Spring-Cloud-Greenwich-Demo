package com.demo.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Msg<T> {
    public static Integer SUCCESS = 0;
    private T data;
    private Integer code;
    private String msg;

    public Msg(T data) {
        this.data = data;
        this.code = SUCCESS;
        this.msg = "success";
    }

    public Msg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
