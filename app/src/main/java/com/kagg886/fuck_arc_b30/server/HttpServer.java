package com.kagg886.fuck_arc_b30.server;

import android.util.Log;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.koushikdutta.async.http.server.AsyncHttpServer;

/**
 * 微型服务器
 *
 * @author kagg886
 * @date 2023/8/13 11:30
 **/
public class HttpServer {

    private static volatile HttpServer mInstance;

    private final AsyncHttpServer server = new AsyncHttpServer();

    public static HttpServer getInstance() {
        if (mInstance == null) {
            // 增加类锁,保证只初始化一次
            synchronized (HttpServer.class) {
                if (mInstance == null) {
                    mInstance = new HttpServer();
                }
            }
        }
        return mInstance;
    }

    public void addRoute(AbstractServlet servlet) {
        server.addAction(servlet.getMethod().name(),servlet.getPath(),servlet);
    }



    /**
     * 开启本地服务
     */
    public void startServer(int port) {
        try {
            server.listen(port);
            Log.i(HttpServer.class.getName(),"Arcaea server started on port:" + port);
        } catch (Exception e) {
        }
    }
}
