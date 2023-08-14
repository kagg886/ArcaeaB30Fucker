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
import com.kagg886.fuck_arc_b30.util.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

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

    public static HashMap<String, double[]> exactlyDiff = new HashMap<>();

    public static JSONObject findSongsById(String id) {
        Optional<JSONObject> object = songDetailsList.stream()
                .map(v -> (JSONObject) v)
                .filter(songs -> songs.getString("id").equals(id)).findFirst();
        return object.orElse(null);
    }

    public static List<SingleSongData> findDataById(String id) {
        return scoreData.stream().filter(songs -> songs.getId().equals(id)).collect(Collectors.toList());
    }


    public static void init() throws IOException, InterruptedException {
        //初始化曲目详情
        try {
            String source = IOUtil.loadStringFromStream(Hooker.activity.getAssets().open("songs/songlist"));
            songDetailsList = JSON.parseObject(source).getJSONArray("songs");
            Log.i(SongManager.class.getName(), "Load song list:" + songDetailsList.size());
        } catch (IOException e) {
            Log.e(SongManager.class.getName(), "Load Song List Failed!", e);
        }

        //初始化歌曲成绩
        try (SQLiteDatabase sqlite = SQLiteDatabase.openDatabase(Hooker.activity.getFilesDir() + "/st3", null, SQLiteDatabase.OPEN_READONLY)) {
            HashMap<String, Integer> clearTypes = new HashMap<>();
            //获得通关状态
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
                scoreData.clear();
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

        CountDownLatch latch = new CountDownLatch(1);
        Utils.runUntilNoError(() -> {
            //初始化定数详单
            Document dom;
            try {
                dom = Jsoup.connect("https://wiki.arcaea.cn/%E5%AE%9A%E6%95%B0%E8%AF%A6%E8%A1%A8").get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements elements = dom.getElementsByTag("tbody").get(0).getElementsByTag("tr");
            for (Element element : elements) {
                if (element.getElementsByTag("th").size() != 0) {
                    continue;
                }
                Elements td = element.getElementsByTag("td");
                String name = td.get(0).text();
                double pst = td.get(1).text().contains("-") ? -1 : Double.parseDouble(td.get(1).text()); //Last Moment只有byd9
                double prs = td.get(2).text().contains("-") ? -1 : Double.parseDouble(td.get(2).text());
                double ftr = td.get(3).text().contains("-") ? -1 : Double.parseDouble(td.get(3).text());
                double byd = td.get(4).text().isBlank() ? -1 : Double.parseDouble(td.get(4).text());

                if (byd != -1 && name.equals("Quon")) { //因为Quon的名字有2个
                    name = "quonwacca";
                } else  {
                    String finalName = name;
                    JSONObject object;
                    try {
                        object = songDetailsList.stream().map((v) -> (JSONObject) v)
                                .filter((v) -> v.getJSONObject("title_localized")
                                        .getString("en")
                                        .equals(finalName)
                                ).findFirst()
                                .orElse(null);
                        if (object == null) {
                            throw new NullPointerException();
                        }
                    } catch (Exception e) {
                        Log.d(SongManager.class.getName(), "An error was founded when we find the id: '" + name + "' ", e);
                        continue;
                    }
                    name = object.getString("id");
                }
                exactlyDiff.put(name, new double[]{pst, prs, ftr, byd});
            }
            exactlyDiff.put("ii",new double[] {5.0,8.4,10.8}); //好像wiki上的ii和arc内部文件的ii拼写不一样
            //理想层面此值应该比内部文件的歌曲详情少两份，因为那两份是新手教程
            Log.i(SongManager.class.getName(), "exactly diff loaded:" + exactlyDiff.size());
            latch.countDown();
        });

        latch.await(); //阻塞程序，直到diff被加载
        Log.i(SongManager.class.getName(), "resource init success");
    }
}
