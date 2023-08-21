package com.kagg886.fuck_arc_b30.util;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

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

    public static void runUntilNoError(Runnable r) {
        new Thread(() -> runUntilNoErrorSync(r)).start();
    }

    private static void runUntilNoErrorSync(Runnable r) {
        Throwable cause;
        do {
            try {
                r.run();
                cause = null;
            } catch (Throwable e) {
                cause = e;
                Log.d(Utils.class.getName(), "run the protect block error:", e);
            }
        } while (cause != null);
    }


    //此方法会在无网络是让 Arcaea 崩溃
    // by ChinaYuanGe
    public static String getLocalIp() {
        try (Socket socket = new Socket("baidu.com", 80)) {
            return socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
