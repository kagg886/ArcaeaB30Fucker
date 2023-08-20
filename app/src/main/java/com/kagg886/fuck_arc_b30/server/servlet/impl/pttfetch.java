package com.kagg886.fuck_arc_b30.server.servlet.impl;

import com.alibaba.fastjson2.JSON;
//import com.kagg886.fuck_arc_b30.BuildConfig;
import com.kagg886.fuck_arc_b30.util.ArcaeaMemReader;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;


/**
 * @author kagg886
 * @date 2023/8/13 11:52
 **/
public class pttfetch extends AbstractServlet {
    public static final pttfetch INSTANCE = new pttfetch();

    private pttfetch() {
        super("userptt", Method.GET);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {
        //TODO: 处理一下响应
        response.send(JSON.toJSONString(Result.OK("PTT="+String.valueOf(ArcaeaMemReader.Profile.GetUserPtt()))));
        //response.send(ArcaeaMemReader.Test());
    }
}
