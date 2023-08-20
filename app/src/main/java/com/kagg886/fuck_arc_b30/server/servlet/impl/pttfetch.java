package com.kagg886.fuck_arc_b30.server.servlet.impl;

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
    public static final Version INSTANCE = new Version();

    private Version() {
        super("version", Method.GET);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        response.send(JSON.toJSONString(Result.OK(AppVersionInfo.INSTANCE)));
    }

    public static class AppVersionInfo {
        public static AppVersionInfo INSTANCE = new AppVersionInfo();

        private final String versionName;
        private final long versionCode;

        public AppVersionInfo() {
            versionName = BuildConfig.VERSION_NAME;
            versionCode = BuildConfig.VERSION_CODE;
        }

        public AppVersionInfo(String s, int i) {
            this.versionName = s;
            this.versionCode = i;
        }

        public String getVersionName() {
            return versionName;
        }

        public long getVersionCode() {
            return versionCode;
        }
    }
}
