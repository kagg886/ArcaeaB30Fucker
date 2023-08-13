package com.kagg886.fuck_arc_b30.server.servlet.impl;

import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.res.SongManager;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

/**
 * 刷新资源
 *
 * @author kagg886
 * @date 2023/8/13 18:41
 **/
public class RefreshResource extends AbstractServlet {
    public RefreshResource() {
        super("refreshResource",Method.GET);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {
        SongManager.init();
        response.send(JSON.toJSONString(Result.OK()));
    }
}
