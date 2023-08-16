package com.kagg886.fuck_arc_b30;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kagg886.fuck_arc_b30.databinding.ActivityMain2Binding;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Version;
import com.kagg886.fuck_arc_b30.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity {
    private NavController navController;

    public void navigateTo(@IdRes int id) {
        navController.navigate(id);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMain2Binding binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_setting
        ).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main2);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("error")) {
            Throwable throwable = ((Throwable) getIntent().getExtras().getSerializable("error"));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("错误捕获");
            builder.setMessage("捕获了一个错误，是否写出崩溃报告?");
            builder.setNegativeButton("否", null);
            builder.setPositiveButton("是", (dialogInterface, i) -> new Thread(() -> {
                Version.AppVersionInfo info;
                try {
                    info = IOUtil.fetch(Version.INSTANCE, Version.AppVersionInfo.class);
                } catch (IOException ignored) {
                    info = new Version.AppVersionInfo("-1",-1);
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
                        out.write(summaryByte, 0, summaryByte.length);

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

                    quickShare(target,"*/*");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start());
            builder.show();
        }
    }


    public void quickShare(File p, String type) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, "com.kagg886.fuck_arc_b30.fileprovider", p));
        intent.setType(type);
        startActivity(intent);
    }

}