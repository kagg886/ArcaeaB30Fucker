package com.kagg886.fuck_arc_b30.server.servlet.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.server.res.SongManager;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

/**
 * 根据songid获取歌曲详细信息
 *
 * @author kagg886
 * @date 2023/8/13 12:48
 **/
public class GetSongInfoById extends AbstractServlet {

    public static final GetSongInfoById INSTANCE = new GetSongInfoById();

    private GetSongInfoById() {
        super("res/getSongInfoById", Method.POST);
    }

    //保证纯净性，此处使用原始数据，详细定数由GetPlayData获取
    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Exception {
        String id = request.getQuery().getString("id");
        if (id == null) {
            response.send(JSON.toJSONString(Result.ERR_MUST_INPUT_ID));
        }
        JSONObject songsResult = SongManager.findSongsById(id);

        if (songsResult != null) {
            response.send(JSON.toJSONString(Result.OK(songsResult)));
            return;
        }
        response.send(JSON.toJSONString(Result.ERR_ID_NOT_EXISTS.apply(id)));
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
