package com.kagg886.fuck;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.util.ArcaeaMemReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kagg886.fuck_arc_b30.server.res.UserManager.pkgInfo;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testResult() {
        System.out.println(JSON.toJSONString(Result.OK("111")));
    }

    @Test
    public void testArcDifficultyExactly() throws IOException {
        Document dom = Jsoup.connect("https://wiki.arcaea.cn/%E5%AE%9A%E6%95%B0%E8%AF%A6%E8%A1%A8").get();
        Elements elements = dom.getElementsByTag("tbody").get(0).getElementsByTag("tr");
        for (Element element : elements) {
            if (element.getElementsByTag("th").size() != 0) {
                continue;
            }
            Elements td = element.getElementsByTag("td");
            String name = td.get(0).text();
            Double pst = td.get(1).text().contains("-") ? -1 : Double.parseDouble(td.get(1).text());
            Double prs = td.get(2).text().contains("-") ? -1 : Double.parseDouble(td.get(2).text());
            Double ftr = td.get(3).text().contains("-") ? -1 : Double.parseDouble(td.get(3).text());
            Double byd = td.get(4).text().isBlank() ? -1 : Double.parseDouble(td.get(4).text());

            System.out.printf("%s,%.2f,%.2f,%.2f,%.2f\n", name, pst, prs, ftr, byd);
        }
    }

    @Test
    public void testNativePTTOnlineGet() throws IOException {

        JSONArray list = JSON.parseArray(
                Jsoup.connect("https://raw.githubusercontent.com/OllyDoge/ArcMemOffsets/main/offsets.json")
                        .proxy("127.0.0.1",7890)
                        .execute()
                        .body()
        );
        JSONObject o = list.stream()
                .map((v) -> ((JSONObject) v))
                .filter((a) -> "5.0.1c".equals(a.getString("gamever")))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("从线上配置中找不到当前版本的偏移量"));

        o = o.getJSONObject("offsets").getJSONObject("userinfo");
        ArcaeaMemReader.Profile.LocateValue = o.getString("locateValue");


        List<Long> l = Arrays.stream(o.getString("memOffset").split(",")).map((a) -> Long.parseLong(a,16)).collect(Collectors.toList());
        long[] a = new long[l.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = l.get(i);
        }

        System.out.println(o);
        ArcaeaMemReader.Profile.LocateMemOffset = a;

        ArcaeaMemReader.Profile.PTT = Long.parseLong(o.getJSONObject("struct").getString("ptt").split(",")[0],16);

        System.out.println(ArcaeaMemReader.Profile.LocateValue);
        System.out.println(Arrays.stream(ArcaeaMemReader.Profile.LocateMemOffset,0,ArcaeaMemReader.Profile.LocateMemOffset.length).mapToObj(Long::toHexString).collect(Collectors.joining(",")));
        System.out.println(Long.toHexString(ArcaeaMemReader.Profile.PTT));
    }
}