package com.kagg886.fuck_arc_b30.server.servlet.impl;

import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 根据songid获取歌曲详细信息
 *
 * @author kagg886
 * @date 2023/8/13 12:48
 **/
public class GetSongInfoById extends AbstractServlet {
    private JSONArray songsList;

    private static final Result<String> ERR_MUST_INPUT_ID = Result.err(1001,"must input id!");
    private static final Function<String,Result<String>> ERR_ID_NOT_EXISTS = (id) -> Result.err(1002,"id: " + id + " not find");


    public GetSongInfoById() {
        super("getSongInfoById", Method.POST);

        try {
            String source = IOUtil.loadStringFromStream(Hooker.activity.getAssets().open("songs/songlist"));
            Log.v(getClass().getName(), "song list content:" + source);
            songsList = JSON.parseObject(source).getJSONArray("songs");
            Log.i(getClass().getName(), "Load song list successful");
        } catch (IOException e) {
            Log.e(getClass().getName(), "Load Song List Failed!", e);
        }
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Exception {
        String id = request.getQuery().getString("id");
        if (id == null) {
            response.send(JSON.toJSONString(ERR_MUST_INPUT_ID));
        }
        Optional<JSONObject> songsResult = songsList.stream()
                .map(v -> (JSONObject) v)
                .filter(songs -> songs.getString("id").equals(id)).findFirst();

        if (songsResult.isPresent()) {
            response.send(JSON.toJSONString(Result.OK(songsResult.get())));
            return;
        }
        response.send(JSON.toJSONString(ERR_ID_NOT_EXISTS.apply(id)));
//        {
//            "idx": 0,
//            "id": "sayonarahatsukoi",
//            "title_localized": {
//                "en": "Sayonara Hatsukoi"
//            },
//            "artist": "REDSHiFT",
//            "search_title": {
//                "ja": [
//                    "さよならはつこい"
//                ],
//                "ko": [
//                    "사요나라 하츠코이"
//                ]
//            },
//            "search_artist": {
//                "ja": [
//                    "れっどしふと"
//                ],
//                "ko": [
//                    "레드시프트"
//                ]
//            },
//            "bpm": "178",
//            "bpm_base": 178,
//            "set": "base",
//            "purchase": "",
//            "audioPreview": 44494,
//            "audioPreviewEnd": 76853,
//            "side": 0,
//            "bg": "base_light",
//            "bg_inverse": "base_conflict",
//            "date": 1487980800,
//            "version": "1.0",
//            "difficulties": [
//                {
//                    "ratingClass": 0,
//                    "chartDesigner": "Nitro",
//                    "jacketDesigner": "",
//                    "rating": 1
//                },
//                {
//                    "ratingClass": 1,
//                    "chartDesigner": "Nitro",
//                    "jacketDesigner": "",
//                    "rating": 4
//                },
//                {
//                    "ratingClass": 2,
//                    "chartDesigner": "Toaster",
//                    "jacketDesigner": "",
//                    "rating": 7
//                }
//            ]
//        }
    }
}
