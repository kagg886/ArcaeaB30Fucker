package com.kagg886.fuck_arc_b30.server.servlet.impl;

import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * 获得曲绘
 *
 * @author kagg886
 * @date 2023/8/13 19:26
 **/
public class Image extends AbstractServlet {

    public static final Image INSTANCE = new Image();

    private static final List<String> non_download = List.of(
            "arcahv",
            "brandnewworld",
            "chronostasis",
            "clotho",
            "dandelion",
            "dement",
            "dialnote"
    );

    private Image() {
        super("res/image", Method.GET);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {
        String id = request.getQuery().getString("id");
        if (id == null) {
            response.send(JSON.toJSONString(Result.ERR_MUST_INPUT_ID));
            return;
        }
        /*难度---难度代号---查询url路由
         * PAST---0---0
         * PRESENT---1---1
         * FUTURE---2---base
         * BEYOND---3---3
         * */
        String difficulty = Optional.ofNullable(
                request.getQuery().getString("difficulty")
        ).orElse("FUTURE").toUpperCase();

        switch (difficulty) {
            case "PAST" -> {
                difficulty = "0";
            }
            case "PRESENT" -> {
                difficulty = "1";
            }
            case "FUTURE" -> {
                difficulty = "base";
            }
            case "BEYOND" -> {
                difficulty = "3";
            }

            default -> {
                //检查是否是数字代号
                try {
                    int a = Integer.parseInt(difficulty);
                    if (a == 2) {
                        difficulty = "base";
                    }
                } catch (NumberFormatException e) {
                    response.send(JSON.toJSONString(Result.ERR_PARAM_IS_ILLEGAL.apply("id", new String[]{"PAST/0", "PRESENT/1", "FUTURE/2", "BEYOND/3"})));
                    return;
                }
            }


        }

        String size = Optional.ofNullable(
                request.getQuery().getString("size")
        ).orElse("256");

        switch (size) {
            case "256" -> {
                size = "_256";
            }
            case "512" -> {
                size = "";
            }
            default -> {
                response.send(JSON.toJSONString(Result.ERR_PARAM_IS_ILLEGAL.apply("size", new String[]{"256", "512"})));
                return;
            }
        }

        String queryUrl = "songs/" + (non_download.contains(id) ? "" : "dl_") + id + "/1080_" + difficulty + size + ".jpg";
        try (InputStream stream = Hooker.activity.getAssets().open(queryUrl)) {
            response.send("image/jpeg", IOUtil.loadByteFromStream(stream));
        } catch (FileNotFoundException e1) {
            Log.d(Image.class.getName(), "find[" + queryUrl + "]failed");
            //可能不带'1080'前缀
            //排除列表都含1080，所以是dl_
            queryUrl = "songs/dl_" + id + "/" + difficulty + size + ".jpg";
            try (InputStream stream = Hooker.activity.getAssets().open(queryUrl)) {
                response.send("image/jpeg", IOUtil.loadByteFromStream(stream));
            } catch (FileNotFoundException e2) {
                //有一些byd没有特殊曲绘，此时需要按ftr查找
                queryUrl = "songs/dl_" + id + "/" + (id.equals("amazingmightyyyy") ? "" : "1080_") + "base" + size + ".jpg";
                try (InputStream stream = Hooker.activity.getAssets().open(queryUrl)) {
                    response.send("image/jpeg", IOUtil.loadByteFromStream(stream));
                } catch (FileNotFoundException e3) {
                    Log.d(Image.class.getName(), "find[" + queryUrl + "]failed");
                    response.send(JSON.toJSONString(Result.ERR_ID_NOT_EXISTS.apply(id)));
                }
            }

        }
//
//        try (InputStream stream = Hooker.activity.getAssets().open(queryUrl)) {
//            response.send("image/jpeg", IOUtil.loadByteFromStream(stream));
//        } catch (FileNotFoundException e) {
//            id = "dl_" + id;
//            queryUrl = "songs/" + id + "/" + songName + size + ".jpg";
//            try (InputStream stream = Hooker.activity.getAssets().open(queryUrl)) {
//                response.send("image/jpeg", IOUtil.loadByteFromStream(stream));
//            } catch (FileNotFoundException e0) {
//                response.send(JSON.toJSONString(Result.ERR_ID_NOT_EXISTS.apply(id)));
//            }
//        }
    }
}
