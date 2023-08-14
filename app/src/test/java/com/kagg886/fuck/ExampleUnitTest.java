package com.kagg886.fuck;

import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.server.model.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

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
}