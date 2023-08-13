package com.kagg886.fuck_arc_b30.server.servlet.impl;

import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.BuildConfig;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

/**
 * @author kagg886
 * @date 2023/8/13 11:52
 **/
public class Version extends AbstractServlet {

    public Version() {
        super("version", Method.GET);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        response.send(JSON.toJSONString(Result.OK(Info.INSTANCE)));
    }

    private static class Info {
        public static Info INSTANCE = new Info();

        private final String versionName;
        private final long versionCode;

        public Info() {
            versionName = BuildConfig.VERSION_NAME;
            versionCode = BuildConfig.VERSION_CODE;
        }

        public String getVersionName() {
            return versionName;
        }

        public long getVersionCode() {
            return versionCode;
        }
    }
}
