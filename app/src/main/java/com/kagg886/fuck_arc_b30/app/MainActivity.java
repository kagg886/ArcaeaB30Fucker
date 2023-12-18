package com.kagg886.fuck_arc_b30.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kagg886.fuck_arc_b30.R;
import com.kagg886.fuck_arc_b30.databinding.ActivityMain2Binding;
import com.kagg886.fuck_arc_b30.server.servlet.impl.DumpLog;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Version;
import com.kagg886.fuck_arc_b30.util.ArcaeaMemReader;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import com.kagg886.fuck_arc_b30.util.Utils;
import com.koushikdutta.async.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import xyz.cnyg.memoryreader.procfs.Maps;

public class MainActivity extends AppCompatActivity {
    private NavController navController;

    private ActivityResultLauncher<Intent> writeCall;

    private String file_path;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMain2Binding binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        //应该是以 Arcaea 进程身份读取，而不是以 app 进程
//        //Toast.makeText(this, String.valueOf(ArcaeaMemReader.Test()), Toast.LENGTH_SHORT).show();
//        var o = Maps.GetMemoryMap();
//
//        Log.i("LOCAL","MemMapSize="+Integer.toString(o.length));
//        for(int i=0;i<o.length;i++){
//            Log.i("LOCAL",
//                    "Addr="+Long.toHexString(o[i].StartAddress)+"-"+Long.toHexString(o[i].EndAddress)+"" +
//                    " - R="+o[i].Permission.Read +",W="+o[i].Permission.Write+
//                    " -> "+o[i].Module);
//        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_setting
        ).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main2);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        writeCall = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() == null) {
                return;
            }
            try {
                OutputStream stream = getContentResolver().openOutputStream(result.getData().getData());
                stream.write(IOUtil.loadByteFromFile(new File(file_path)));
                stream.close();
            } catch (Exception e) {
                Log.w(getClass().getName(), "export failed!", e);
            }

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("温馨提示")
                    .setMessage("导出文件并找到文件后，您可以：\n1. 前往设置打开Github仓库，新开issue上传此文件\n2. 发送此文件到 'iveour@163.com'")
                    .show();
        });
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("error")) {
            Throwable throwable = ((Throwable) getIntent().getExtras().getSerializable("error"));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("错误捕获");
            builder.setMessage("捕获了一个错误，是否写出崩溃报告?");
            builder.setNegativeButton("否", null);

            builder.setNeutralButton("是(仅写出本地报告)", (a, b) -> {
                Utils.runUntilNoError(() -> writeLocalReportAndShare(throwable, false));
            });
            builder.setPositiveButton("是(写出服务端报告)", (dialogInterface, i) ->{
                runOnUiThread(() -> Toast.makeText(this, "若迟迟不出导出页面，请打开Arcaea后将后台切回到这里", Toast.LENGTH_SHORT).show());
                Utils.runUntilNoError(() -> writeLocalReportAndShare(throwable, true));
            });
            builder.show();
        }
    }


    private void writeLocalReportAndShare(Throwable throwable, boolean writeServerLog) {
        Version.AppVersionInfo info;
        String serverLog = null;
        try {
            info = IOUtil.fetch(Version.INSTANCE, Version.AppVersionInfo.class);
            serverLog = IOUtil.fetch(DumpLog.INSTANCE, String.class);
        } catch (IOException e) {
            if (writeServerLog) {
                Log.e(getClass().getName(), "dump message must connect the server,now reloading...");
                throw new RuntimeException(e);
            } else {
                info = new Version.AppVersionInfo("-1", -1);
            }
        }

        StringBuilder fileWriter = new StringBuilder();
        fileWriter.append("---AppCrashLogSummary---");
        fileWriter.append("\nCrashTime:").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        fileWriter.append("\nBaseLogFile:").append(((CrashHandler) getApplication()).getCatcher().getFile().getName());
        fileWriter.append("\n---DeviceInfo---");
        fileWriter.append("\nAndroid-Version:").append(Build.VERSION.RELEASE);
        fileWriter.append("\nModel:").append(Build.MODEL);
        fileWriter.append("\nLocal-Version:").append(BuildConfig.VERSION_NAME).append("(").append(BuildConfig.VERSION_CODE).append(")");
        fileWriter.append("\nRemote-Version:").append(info.getVersionName()).append("(").append(info.getVersionCode()).append(")");
        fileWriter.append("\n---StackTrace---\n");
        fileWriter.append(IOUtil.getException(throwable));
        fileWriter.append("\n----------Report End----------");


        File base = ((CrashHandler) getApplication()).getLoggerBase();

        File target = new File(getCacheDir(), "Log-" + UUID.randomUUID().toString() + ".zip");
        try {
            target.createNewFile();
            try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(target.toPath()))) {
                //write Summary
                out.putNextEntry(new ZipEntry("Summary.txt"));
                byte[] summaryByte = fileWriter.toString().getBytes();
                out.write(summaryByte);

                if (writeServerLog) {
                    out.putNextEntry(new ZipEntry("Server.log"));
                    byte[] serverByte = serverLog.getBytes();
                    out.write(serverByte);
                }

                //write Log.txt
                for (File log : Objects.requireNonNull(base.listFiles())) {
                    ZipEntry entry = new ZipEntry("log/" + log.getName());
                    out.putNextEntry(entry);

                    try (FileInputStream stream = new FileInputStream(log)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = stream.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
//                                byte[] buffer = new byte[1024];
//                                int readOnly = stream.read(buffer);
//                                //使用缓冲区，效率杠杠的
//                                out.write(buffer, 0, readOnly);
                    }
                }
            }

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            this.file_path = target.getAbsolutePath();
            intent.putExtra(Intent.EXTRA_TITLE, target.getName());
            writeCall.launch(intent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}