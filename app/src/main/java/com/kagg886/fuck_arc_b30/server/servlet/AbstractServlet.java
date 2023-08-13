package com.kagg886.fuck_arc_b30.server.servlet;

import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

/**
 * 服务注册器
 *
 * @author kagg886
 * @date 2023/8/13 11:47
 **/
public abstract class AbstractServlet implements HttpServerRequestCallback {
    private final String path;
    private final Method method;

    //path不需要加 '/'
    public AbstractServlet(String path, Method method) {
        this.path = "/arcapi/v1/" + path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public Method getMethod() {
        return method;
    }

    public enum Method {
        GET,POST
    }

    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        Log.i(getClass().getName(),String.format("Route accessed: %s",request.getPath()));
        try {
            onRequest0(request,response);
        } catch (Throwable e) {
            Log.e(getClass().getName(),"Route work error!",e);
            response.send(JSON.toJSONString(new Result<>(-1,e.getClass().getName(),e.getMessage())));
        }
    }

    public abstract void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable;
}
