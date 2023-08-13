package com.kagg886.fuck_arc_b30.server.servlet.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.res.SongManager;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.model.SingleSongData;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.util.List;
import java.util.stream.Collectors;

import static com.kagg886.fuck_arc_b30.res.SongManager.scoreData;

/**
 * 获取单曲数据
 *
 * @author kagg886
 * @date 2023/8/13 16:27
 **/
public class GetPlayerDataById extends AbstractServlet {

    public GetPlayerDataById() {
        super("data/getPlayerDataById", Method.POST);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {
        String id = request.getQuery().getString("id");
        if (id == null) {
            response.send(JSON.toJSONString(Result.ERR_MUST_INPUT_ID));
        }

        List<SingleSongData> data = SongManager.findDataById(id);
        if (data.size() != 0) {
            response.send(JSON.toJSONString(Result.OK(data)));
            return;
        }

        JSONObject songsResult = SongManager.findSongsById(id);
        if (songsResult != null) {
            response.send(JSON.toJSONString(Result.ERR_NOT_PLAYED.apply(id)));
            return;
        }

        response.send(JSON.toJSONString(Result.ERR_ID_NOT_EXISTS.apply(id)));
    }
}
