package com.kagg886.fuck_arc_b30.server.servlet.impl;

import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 获取游戏Bitmap资源
 *
 * @author kagg886
 * @date 2023/8/14 19:05
 **/
public class AssetsGet extends AbstractServlet {
    public static AssetsGet INSTANCE = new AssetsGet();
    private AssetsGet() {
        super("res/assets",Method.GET);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {
        String path = request.getQuery().getString("path");
        Log.d(getClass().getName(),path);
        if (path == null) {
            response.send(JSON.toJSONString(Result.ERR_MUST_INPUT_ID));
            return;
        }
        try (InputStream stream = Hooker.activity.getAssets().open(path)) {
            File random = Hooker.activity.getExternalCacheDir().toPath().resolve(path.split("/")[path.split("/").length - 1]).toFile();
            random.createNewFile();
            IOUtil.writeByteToFile(random,IOUtil.loadByteFromStream(stream));
            response.sendFile(random);
        } catch (FileNotFoundException e) {
            response.send(JSON.toJSONString(Result.ERR_ID_NOT_EXISTS.apply(path)));
        }
    }
}
