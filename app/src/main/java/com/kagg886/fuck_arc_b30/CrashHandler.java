package com.kagg886.fuck_arc_b30;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.kagg886.fuck_arc_b30.util.LogCatcher;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886
 * @className: GlobalApplication
 * @author: kagg886
 * @description: 应用实例
 * @date: 2023/4/13 21:17
 * @version: 1.0
 */
public class CrashHandler extends Application implements Thread.UncaughtExceptionHandler, Runnable {

    private LogCatcher catcher; //日志抓取器线程

    public LogCatcher getCatcher() {
        return catcher;
    }


    public File getLoggerBase() {
        return new File(getCacheDir(), "log");
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate() {
        super.onCreate();

        //设置日志记录器
        File logRoot = getLoggerBase();
        logRoot.mkdirs();
        int i = 0;
        File log;
        do {
            LocalDate date = LocalDate.now();
            log = new File(logRoot, String.format("%d-%d-%d_%d.log", date.getYear(), date.getMonth().getValue(), date.getDayOfMonth(), i));
            i++;
        } while (log.exists());
        try {
            log.createNewFile();

            catcher = new LogCatcher(log);
            catcher.start();

        } catch (IOException e) {
            System.out.println(log.getAbsolutePath());
            throw new RuntimeException(e);
        }

        Thread.setDefaultUncaughtExceptionHandler(this);
        new Handler(Looper.getMainLooper()).post(this);
    }

    @Override
    public void uncaughtException(@NonNull @NotNull Thread t, @NonNull @NotNull Throwable e) {
        goCrash(e);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Looper.loop();
            } catch (Throwable th) {
                goCrash(th);
            }
        }
    }

    private void goCrash(Throwable th) {
        Log.i(getClass().getName(),"App has crashed!");
        Log.e(getClass().getName(),"ErrorInfo:",th);
        Toast.makeText(this, "发生了一个致命错误，三秒后强制重启APP...", Toast.LENGTH_LONG).show();
        new Thread(() -> {
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
            i.putExtra("error", th);
            startActivity(i);
            android.os.Process.killProcess(android.os.Process.myPid());
        }).start();
    }
}
