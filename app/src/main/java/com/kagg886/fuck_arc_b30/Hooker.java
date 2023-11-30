package com.kagg886.fuck_arc_b30;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.kagg886.fuck_arc_b30.server.res.SongManager;
import com.kagg886.fuck_arc_b30.server.HttpServer;
import com.kagg886.fuck_arc_b30.server.res.UserManager;
import com.kagg886.fuck_arc_b30.server.servlet.impl.*;
import com.kagg886.fuck_arc_b30.util.ArcaeaMemReader;
import com.kagg886.fuck_arc_b30.util.Utils;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import javax.net.ssl.SSLHandshakeException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class Hooker implements IXposedHookLoadPackage {
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    public static File logBase;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //兼容共存版arc
        Class<?> AppActivityClass;
        try {

            //如果完全断网的话，Arcaea会崩溃。
            Utils.runAsync(() -> Log.i(Hooker.class.getName(), "Your Local IP is:" + Utils.getLocalIp()));
            AppActivityClass = loadPackageParam.classLoader.loadClass("low.moe.AppActivity");
            Method onCreate = AppActivityClass.getDeclaredMethod("onCreate", Bundle.class);
            XposedBridge.hookMethod(onCreate, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    activity = ((Activity) param.thisObject);
                    Log.i(Hooker.class.getName(), "App Activity is Find!");
                    //init Logger
                    logBase = activity.getCacheDir().toPath().resolve("server.log").toFile();

                    //ignore ssl
                    //自5.2.0c
                    Utils.ignoreALLSSL();

                    //init user
                    UserManager.init();
                    //init Resources

                    //init offset for web
                    CompletableFuture.runAsync(() -> {
                        try {
                            ArcaeaMemReader.init();
                        } catch (Exception e) {
                            Log.e(ArcaeaMemReader.class.getName(), "failed to load native param:", e);
                            activity.runOnUiThread(() -> {
                                if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
                                    Toast.makeText(activity, "无法加载Native偏移量，请尝试开启魔法上网", Toast.LENGTH_SHORT).show();
                                } else if (e instanceof SSLHandshakeException) {
                                    //wtf
                                    Toast.makeText(activity, "HTTPS出现了点问题，请尝试更换代理软件", Toast.LENGTH_SHORT).show();
                                } else  {
                                    Toast.makeText(activity, "无法加载Native偏移量，请前往github issue", Toast.LENGTH_SHORT).show();
                                }
                                activity.finish();
                            });
                            return;
                        }

                        if (SongManager.init(true)) {
                            HttpServer i;
                            try {
                                i = HttpServer.getInstance();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            //start HTTPServer
                            i.addRoute(RefreshResource.INSTANCE);
                            i.addRoute(Version.INSTANCE);
                            i.addRoute(GetSongInfoById.INSTANCE);
                            i.addRoute(GetPlayerDataById.INSTANCE);
                            i.addRoute(Image.INSTANCE);
                            i.addRoute(Best30.INSTANCE);
                            i.addRoute(AssetsGet.INSTANCE);
                            i.addRoute(DumpLog.INSTANCE);
                            i.addRoute(Profile.INSTANCE);

                            activity.runOnUiThread(() -> Toast.makeText(activity, "Arcaea B30 Server Started!", Toast.LENGTH_SHORT).show());
                            i.startServer(61616);

                        }
                    }).get();
                }
            });
        } catch (ClassNotFoundException ignored) {
        }
    }
}
