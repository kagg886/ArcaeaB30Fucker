package com.kagg886.fuck_arc_b30.server.servlet.impl;

import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

/**
 * 获取Arcaea侧日志
 *
 * @author kagg886
 * @date 2023/8/16 11:31
 **/
public class DumpLog extends AbstractServlet {

    public static DumpLog INSTANCE = new DumpLog();

    public DumpLog() {
        super("res/log", Method.GET);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {
        response.sendFile(Hooker.logBase);
    }
}
