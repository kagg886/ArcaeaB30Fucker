package com.kagg886.fuck_arc_b30.server;

import android.util.Log;
import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.util.LogCatcher;
import com.koushikdutta.async.http.server.AsyncHttpServer;

import java.io.File;
import java.io.IOException;

/**
 * 微型服务器
 *
 * @author kagg886
 * @date 2023/8/13 11:30
 **/
public class HttpServer {

    private static volatile HttpServer mInstance;

    //远程日志捕获器
    private LogCatcher catcher;

    private final AsyncHttpServer server = new AsyncHttpServer();

    public static HttpServer getInstance() throws IOException {
        if (mInstance == null) {
            // 增加类锁,保证只初始化一次
            synchronized (HttpServer.class) {
                if (mInstance == null) {
                    mInstance = new HttpServer();
                    File f = Hooker.logBase;
                    if (f.exists()) {
                        f.delete();
                        f.createNewFile();
                    }
                    mInstance.catcher = new LogCatcher(f);
                    mInstance.catcher.start();
                    Log.i(HttpServer.class.getName(),"HttpServer instance created");
                }
            }
        }
        return mInstance;
    }

    public void addRoute(AbstractServlet servlet) {
        server.addAction(servlet.getMethod().name(), servlet.getPath(), servlet);
    }


    /**
     * 开启本地服务
     */
    public void startServer(int port) {
        try {
            server.listen(port);
            Log.i(HttpServer.class.getName(), "Arcaea server started on port:" + port);
        } catch (Exception e) {
        }
    }
}
