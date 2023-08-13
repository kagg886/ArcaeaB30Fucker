package com.kagg886.fuck_arc_b30.server.model;

/**
 * 结果返回器
 *
 * @author kagg886
 * @date 2023/8/13 12:17
 **/
public class Result<T> {
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
