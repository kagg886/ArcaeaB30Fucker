package com.kagg886.fuck_arc_b30.server.servlet.impl;

import android.content.Context;
import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.Hooker;
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
    public static final RefreshResource INSTANCE = new RefreshResource();

    private RefreshResource() {
        super("res/refreshResource",Method.GET);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {
        Hooker.activity.getSharedPreferences("arc_b30_fucker_exactly_data", Context.MODE_PRIVATE).edit().remove("version");
        SongManager.init();
        response.send(JSON.toJSONString(Result.OK()));
    }
}
