package com.kagg886.fuck_arc_b30.util;

import android.annotation.SuppressLint;
import android.util.Log;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

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
            return "null";
        }
    }

    @SuppressLint({"TrustAllX509TrustManager", "CustomX509TrustManager"})
    static public void ignoreALLSSL() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");

        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }
}
