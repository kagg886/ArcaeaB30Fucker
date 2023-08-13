package com.kagg886.fuck_arc_b30.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 类
 *
 * @author kagg886
 * @date 2023/8/13 11:32
 **/
public class Utils {

    public static String[] newStringArray(String... arr) {
        return arr;
    }
    public static void runAsync(Runnable r) {
        new Thread(r).start();
    }
    public static String getLocalIp() {
        try(Socket socket = new Socket("baidu.com", 80)) {
            return socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
