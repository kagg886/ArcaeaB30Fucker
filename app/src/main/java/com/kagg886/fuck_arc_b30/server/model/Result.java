package com.kagg886.fuck_arc_b30.server.model;

import java.util.function.Function;

/**
 * 结果返回器
 *
 * @author kagg886
 * @date 2023/8/13 12:17
 **/
public class Result<T> {

    public static final Result<String> ERR_MUST_INPUT_ID = Result.err(1001,"must input id!");
    public static final Function<String,Result<String>> ERR_ID_NOT_EXISTS = (id) -> Result.err(1002,"id: " + id + " not find");

    public static final Function<String,Result<String>> ERR_NOT_PLAYED = (id) -> Result.err(1003,"id: " + id + " not played");


    private final int code;
    private final String msg;
    private final T data;

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result<?> OK() {
        return OK(null);
    }

    public static <T> Result<T> OK(T obj) {
        if (obj == null) {
            return new Result<>(200, "success", null);
        }
        return new Result<>(200, "success", obj);
    }

    public static <T> Result<T> err(int code, String msg) {
        return new Result<>(code,msg,null);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
