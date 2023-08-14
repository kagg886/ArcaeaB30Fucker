package com.kagg886.fuck_arc_b30.server.servlet.impl;

import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.model.SingleSongData;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import com.kagg886.fuck_arc_b30.util.Utils;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 获得曲绘
 *
 * @author kagg886
 * @date 2023/8/13 19:26
 **/
public class Image extends AbstractServlet {

    public static final Image INSTANCE = new Image();

    private Image() {
        super("res/image", Method.GET);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {
        String id = request.getQuery().getString("id");


        String difficulty = request.getQuery().getString("difficulty");

        String songName;

        if (difficulty != null) {
            c:
            {
                if (difficulty.equals("PRESENT") || difficulty.equals("PAST") || difficulty.equals("BEYOND")) {
                    songName = String.valueOf(SingleSongData.Difficulty.valueOf(difficulty).getDiff());
                    break c;
                }

                if (difficulty.equals("FUTURE")) {
                    songName = "base";
                    break c;
                }

                int d;
                try {
                    d = Integer.parseInt(difficulty);
                    if (d < 0 || d > 3) {
                        throw new Exception();
                    }
                    if (d == 2) {
                        songName = "base";
                        break c;
                    }
                    songName = String.valueOf(d);
                } catch (Exception e) {
                    response.send(JSON.toJSONString(Result.PARAM_IS_ILLEGAL.apply("difficulty", Utils.newStringArray("0", "1", "2", "3", "PAST", "PRESENT", "FUTURE", "BEYOND"))));
                    return;
                }
            }
        } else {
            songName = "base";
        }


        String size = request.getQuery().getString("size");
        size = size == null ? "256" : size;
        switch (size) {
            case "512":
                size = "";
                break;
            case "256":
                size = "_256";
                break;
            default:
                response.send(JSON.toJSONString(Result.PARAM_IS_ILLEGAL.apply("size", Utils.newStringArray("512", "256"))));
                return;
        }

        String queryUrl = "songs/" + id + "/" + songName + size + ".jpg";
        Log.d(getClass().getName(), "query Image:" + queryUrl);

        try (InputStream stream = Hooker.activity.getAssets().open(queryUrl)) {
            response.send("image/jpeg", IOUtil.loadByteFromStream(stream));
        } catch (FileNotFoundException e) {
            response.send(JSON.toJSONString(Result.ERR_ID_NOT_EXISTS.apply(id)));
        }
    }
}
