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
import com.kagg886.fuck_arc_b30.util.Utils;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.File;
import java.lang.reflect.Method;

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

                    //init user
                    UserManager.init();
                    //init Resources
                    if (SongManager.init(true)) {
                        //start HTTPServer
                        HttpServer.getInstance().addRoute(RefreshResource.INSTANCE);
                        HttpServer.getInstance().addRoute(Version.INSTANCE);
                        HttpServer.getInstance().addRoute(GetSongInfoById.INSTANCE);
                        HttpServer.getInstance().addRoute(GetPlayerDataById.INSTANCE);
                        HttpServer.getInstance().addRoute(Image.INSTANCE);
                        HttpServer.getInstance().addRoute(Best30.INSTANCE);
                        HttpServer.getInstance().addRoute(AssetsGet.INSTANCE);
                        HttpServer.getInstance().addRoute(DumpLog.INSTANCE);
                        HttpServer.getInstance().addRoute(Profile.INSTANCE);

                        HttpServer.getInstance().startServer(61616);

                        activity.runOnUiThread(() -> Toast.makeText(activity, "Arcaea B30 Server Started!", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (ClassNotFoundException ignored) {
        }
    }
}
