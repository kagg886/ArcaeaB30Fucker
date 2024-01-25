package com.kagg886.fuck_arc_b30.util;

import android.util.Log;

import java.io.*;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.util
 * @className: LogCatcher
 * @author: kagg886
 * @description: log抓取器
 * @date: 2023/4/24 18:21
 * @version: 1.0
 */
public class LogCatcher extends Thread {

    private final BufferedWriter writer;

    private final BufferedReader reader;

    private final File file;

    public File getFile() {
        return file;
    }

    public LogCatcher(File write) throws IOException {
        this.file = write;
        this.writer = new BufferedWriter(new FileWriter(write));
        Process process = Runtime.getRuntime().exec(new String[]{"logcat", "-v", "threadtime", "TAG:*"});
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                BufferedReader reader1 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//
//                try {
//                    String s;
//                    while ((s = reader1.readLine()) != null) {
//                        Log.e(getClass().getName(), s);
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
        Log.d(getClass().getName(),"Global Log catcher started");
    }

    @Override
    public void run() {
        try {
            String s;
            //Vivo手机for内不加try一直白屏
            while ((s = reader.readLine()) != null) {
                try {
                    writer.write(s);
                    writer.write("\n");
                    writer.flush();
                } catch (Exception e) {
                    Log.e(getClass().getName(), "Logcat记录错误!", e);
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
