package com.kagg886.fuck_arc_b30.server.res;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.server.model.SingleSongData;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import com.kagg886.fuck_arc_b30.util.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 歌曲统一管理类
 *
 * @author kagg886
 * @date 2023/8/13 18:31
 **/
public class SongManager {
    //排除id，如愚人节曲目
    private static List<String> excludeSongId = new ArrayList<>() {{
        add("overdead");
        add("redandblueandgreen");
        add("ignotusafterburn");
        add("singularityvvvip");
        add("mismal");
        add("ifirmx");
    }};

    //歌曲的信息
    public static JSONArray songDetailsList;
    //玩家的成绩
    public static List<SingleSongData> scoreData = new ArrayList<>();

    public static Map<String, Object> exactlyDiff = new HashMap<>();

    public static JSONObject findSongsById(String id) {
        Optional<JSONObject> object = songDetailsList.stream()
                .map(v -> (JSONObject) v)
                .filter(songs -> songs.getString("id").equals(id)).findFirst();
        return object.orElse(null);
    }

    public static List<SingleSongData> findDataById(String id) {
        return scoreData.stream().filter(songs -> songs.getId().equals(id)).collect(Collectors.toList());
    }

    private static void loadSongList() {
        try {
            String source = IOUtil.loadStringFromStream(Hooker.activity.getAssets().open("songs/songlist"));
            songDetailsList = JSON.parseObject(source).getJSONArray("songs");
            Log.i(SongManager.class.getName(), "Load song list:" + songDetailsList.size());
            Log.v(SongManager.class.getName(), "song list dump:" + source);
        } catch (IOException e) {
            Log.e(SongManager.class.getName(), "Load Song List Failed!", e);
        }
    }

    private static void loadScoreData() {
        try (SQLiteDatabase sqlite = SQLiteDatabase.openDatabase(Hooker.activity.getFilesDir() + "/st3", null, SQLiteDatabase.OPEN_READONLY)) {
            HashMap<String, Integer> clearTypes = new HashMap<>();
            //获得通关状态
            Cursor cursor = sqlite.rawQuery("select * from cleartypes", null);
            if (cursor.moveToFirst()) {
                do {
                    if (excludeSongId.contains(cursor.getString(1))) {
                        continue;
                    }
                    //同一id可能有不同难度
                    clearTypes.put(cursor.getString(1) + "_" + cursor.getString(2), cursor.getInt(3));
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
                scoreData.clear();
                String id;
                do {
                    id = cursor.getString(8);
                    if (excludeSongId.contains(id)) {
                        continue;
                    }
                    scoreData.add(new SingleSongData(
                            id,
                            cursor.getInt(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getInt(5),
                            cursor.getInt(6),
                            SingleSongData.Difficulty.valueOf(cursor.getInt(9)),
                            clearTypes.getOrDefault(id + "_" + cursor.getString(9), -1),
                            cursor.getInt(11)
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();

            Log.i(SongManager.class.getName(), "load playerData successful");
            Log.v(SongManager.class.getName(), "scoreData dump:" + scoreData);
            Log.v(SongManager.class.getName(), "clearTypesOrigin dump:" + clearTypes);
        } catch (Exception e) {
            Log.e(SongManager.class.getName(), "Failed to load PlayerData", e);
        }
    }


    public static void init() throws InterruptedException {
        //初始化曲目详情
        loadSongList();
        //初始化歌曲成绩
        loadScoreData();

        CountDownLatch latch = new CountDownLatch(1);
        Utils.runUntilNoError(() -> {
            SharedPreferences exactlyData = Hooker.activity.getSharedPreferences("arc_b30_fucker_exactly_data", Context.MODE_PRIVATE);
            String dataVersion = exactlyData.getString("version", null);
            PackageInfo info;
            try {
                info = Hooker.activity.getPackageManager().getPackageInfo(Hooker.activity.getPackageName(), 0);
                if (info.versionName.equals(dataVersion)) {
                    exactlyDiff = JSON.parseObject(exactlyData.getString("list", null), HashMap.class);
                    Log.i(SongManager.class.getName(), "loaded exactly diff from cache");
                    Log.v(SongManager.class.getName(), String.format("exactlyDiff(offline) dump:\nVersion:%s\nDump:%s", dataVersion, exactlyDiff.toString()));
                    latch.countDown();
                    return;
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }

            //初始化定数详单
            JSONObject ex_diff_online = null;
            AtomicReference<String> errSongId = new AtomicReference<>();
            try {
                Document dom = Jsoup.connect("https://wiki.arcaea.cn/index.php?title=Template:ChartConstant.json&action=edit").get();
                ex_diff_online = JSON.parseObject(dom.getElementById("wpTextbox1").text());
                //unit:
//                "alexandrite": [
//                    {
//                        "constant": 4.5,
//                            "old": false
//                    },
//                    {
//                        "constant": 7,
//                            "old": false
//                    },
//                    {
//                        "constant": 10,
//                        "old": false
//                    }
//                ],

                ex_diff_online.forEach((id, value1) -> {
                    errSongId.set(id);
                    double[] value = ((JSONArray) value1)
                            .stream()
                            .mapToDouble((v) -> v == null ? -1 : ((JSONObject) v).getDoubleValue("constant")
                            )
                            .toArray();
                    exactlyDiff.put(id, value);
                });
            } catch (Exception e) {
                Log.e(SongManager.class.getName(), "fetch ex_diff_online error: " + errSongId.get() + "->" + ex_diff_online.get(errSongId.get()).toString());
                Hooker.activity.runOnUiThread(() -> {
                    Toast.makeText(Hooker.activity, "在线定数表拉取失败，请尝试重新启动Arcaea!", Toast.LENGTH_SHORT).show();
                    Hooker.activity.finish();
                });
                return;
            }

            //开始校验：
            for (Object obj : SongManager.songDetailsList) {
                String songId = ((JSONObject) obj).getString("id");
                if (exactlyDiff.containsKey(songId)) {
                    continue;
                }
                Log.e("%s 's ex_diff not find", songId);
                Hooker.activity.runOnUiThread(() -> {
                    Toast.makeText(Hooker.activity, "云端定数表校验失败\n请等待Arcaea Wiki更新最新的定数表", Toast.LENGTH_SHORT).show();
                    Hooker.activity.finish();
                });
                return;
            }

            exactlyData.edit().putString("list", JSON.toJSONString(exactlyDiff)).putString("version", info.versionName).apply();
            Log.i(SongManager.class.getName(), "loaded exactly diff from arcaea wiki");
            Log.v(SongManager.class.getName(), String.format("exactlyDiff(online) dump:\nVersion:%s\nDump:%s", info.versionName, JSON.toJSONString(exactlyDiff)));
            latch.countDown();
        });

        latch.await(); //阻塞程序，直到diff被加载
        Log.i(SongManager.class.getName(), "resource init success");
    }
}
