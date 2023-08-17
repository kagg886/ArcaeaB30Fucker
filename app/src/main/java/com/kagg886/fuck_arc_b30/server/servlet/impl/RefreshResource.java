package com.kagg886.fuck_arc_b30.server.servlet.impl;

import android.content.Context;
import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.server.res.SongManager;
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
        String ex_boolean = request.getQuery().getString("forceRefreshEx_diff");
        if (ex_boolean == null) {
            ex_boolean = "false";
        }

        if (ex_boolean.equals("true") || ex_boolean.equals("false")) {
            if (Boolean.parseBoolean(ex_boolean)) {
                Hooker.activity.getSharedPreferences("arc_b30_fucker_exactly_data", Context.MODE_PRIVATE).edit().remove("version").apply();
            }
            SongManager.init();
            response.send(JSON.toJSONString(Result.OK()));
            return;
        }
        response.send(JSON.toJSONString(Result.PARAM_IS_ILLEGAL.apply("forceRefreshEx_diff",new String[] {"true","false"})));

    }
}
