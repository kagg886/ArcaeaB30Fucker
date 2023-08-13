package com.kagg886.fuck_arc_b30.res;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.server.model.SingleSongData;
import com.kagg886.fuck_arc_b30.util.IOUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * 歌曲统一管理类
 *
 * @author kagg886
 * @date 2023/8/13 18:31
 **/
public class SongManager {
    //歌曲的信息
    public static JSONArray songDetailsList;
    //玩家的成绩
    public static List<SingleSongData> scoreData = new ArrayList<>();

    public static JSONObject findSongsById(String id) {
        Optional<JSONObject> object = songDetailsList.stream()
                .map(v -> (JSONObject) v)
                .filter(songs -> songs.getString("id").equals(id)).findFirst();
        return object.orElse(null);
    }


    public static void init() {
        try {
            String source = IOUtil.loadStringFromStream(Hooker.activity.getAssets().open("songs/songlist"));
            songDetailsList = JSON.parseObject(source).getJSONArray("songs");
            Log.i(SongManager.class.getName(), "Load song list successful");
        } catch (IOException e) {
            Log.e(SongManager.class.getName(), "Load Song List Failed!", e);
        }

        try (SQLiteDatabase sqlite = SQLiteDatabase.openDatabase(Hooker.activity.getFilesDir() + "/st3", null, SQLiteDatabase.OPEN_READONLY)) {
            HashMap<String, Integer> clearTypes = new HashMap<>();
            //获取通关状态
            Cursor cursor = sqlite.rawQuery("select * from cleartypes", null);
            if (cursor.moveToFirst()) {
                do {
                    clearTypes.put(cursor.getString(1), cursor.getInt(3));
                    //id --> 0
                    //songId --> 1
                    //songDifficulty --> 2
                    //clearType --> 3
                    //ct --> 4
                } while (cursor.moveToNext());
            }
            cursor.close();


            //获取详细数据
            cursor = sqlite.rawQuery("select * from scores", null);

            if (cursor.moveToFirst()) {
                //id ---> 0
                //version ---> 1
                //score ---> 2
                //shinyPerfectCount ---> 3
                //perfectCount ---> 4
                //nearCount ---> 5
                //missCount ---> 6
                //date ---> 7
                //songId ---> 8
                //songDifficulty ---> 9
                //modifier ---> 10
                //health ---> 11
                //ct ---> 12

                String id;
                do {
                    id = cursor.getString(8);
                    scoreData.add(new SingleSongData(
                            id,
                            cursor.getInt(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getInt(5),
                            cursor.getInt(6),
                            SingleSongData.Difficulty.valueOf(cursor.getInt(9)),
                            clearTypes.getOrDefault(id, -1),
                            cursor.getInt(11)
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
            Log.i(SongManager.class.getName(), "load playerData successful");
        } catch (Exception e) {
            Log.e(SongManager.class.getName(), "Failed to load PlayerData", e);
        }

        Log.i(SongManager.class.getName(), "resource init success");
    }
}
