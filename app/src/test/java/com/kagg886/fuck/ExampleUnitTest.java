package com.kagg886.fuck;

import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.server.model.Result;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        System.out.println(JSON.toJSONString(Result.OK("111")));
    }
}