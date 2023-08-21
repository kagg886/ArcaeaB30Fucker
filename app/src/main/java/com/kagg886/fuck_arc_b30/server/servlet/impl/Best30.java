package com.kagg886.fuck_arc_b30.server.servlet.impl;

import android.annotation.SuppressLint;
import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.kagg886.fuck_arc_b30.server.res.SongManager;
import com.kagg886.fuck_arc_b30.server.model.Best30Model;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>获取玩家的B30</h1>
 * 当游玩过一个谱面后，系统会根据当前谱面的定数与本次游玩的游玩成绩计算出该次游玩所对应的潜力值，具体如下：<br>
 * ≥ 10,000,000（即PM） 定数+2 (此时屏蔽物量影响)<br>
 * ≥ 9,800,000 且 < 10,000,000 定数+1+(分数 - 9,800,000)/200,000<br>
 * < 9,800,000 定数+(分数 - 9,500,000)/300,000 (下限为0)<br>
 *
 * @author kagg886
 * @date 2023/8/14 9:13
 **/
public class Best30 extends AbstractServlet {

    public static final Best30 INSTANCE = new Best30();

    private Best30() {
        super("data/b30", Method.POST);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {

        List<Best30Model> models;
        try {
            models = getB30Models();
        } catch (Exception e) {
            response.send(JSON.toJSONString(Result.ERR_ID_NOT_EXISTS.apply(e.getMessage())));
            return;
        }
        response.send(JSON.toJSONString(Result.OK(models)));
//        response.send(JSON.toJSONString(Result.OK(models)));
    }

    public static List<Best30Model> getB30Models() {
        List<Best30Model> models = new ArrayList<>();

        SongManager.scoreData.forEach((v) -> {
            String id = v.getId();
            double ptt;
            double ex_diff;
            try {
                ex_diff = ((double[]) SongManager.exactlyDiff.get(id))[v.getDifficulty().getDiff()];
            } catch (Exception e) {
                try {
                    ex_diff = ((JSONArray) SongManager.exactlyDiff.get(id)).getDouble(v.getDifficulty().getDiff());
                } catch (Exception p) {
                    Log.e(Best30.class.getName(),"ex_diff:" + id + "not found!");
                    throw new IllegalStateException(id);
                }
            }
            if (v.getScore() > 10_000_000) {
                ptt = ex_diff + 2;
            } else if (v.getScore() < 9_800_000) {
                ptt = (ex_diff + v.getScore() - 9_500_000) / 300_000.0;
            } else {
                ptt = ex_diff + 1 + (v.getScore() - 9_800_000) / 200_000.0;
            }

            if (ptt < 0) {
                ptt = 0;
            }

            @SuppressLint("DefaultLocale")
            Best30Model model = new Best30Model(
                    SongManager.findSongsById(id).getJSONObject("title_localized").getString("en"),
                    v,
                    Double.parseDouble(String.format("%.2f", ptt)),
                    ex_diff
            );
            models.add(model);
        });

        models.sort((a, b) -> Double.compare(b.getPtt(), a.getPtt()));
        return models.subList(0,Math.min(30,models.size()));
    }
}
