package com.kagg886.fuck_arc_b30.app.ui.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceFragmentCompat;
import com.kagg886.fuck_arc_b30.BuildConfig;
import com.kagg886.fuck_arc_b30.app.CrashHandler;
import com.kagg886.fuck_arc_b30.R;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.server.servlet.impl.RefreshResource;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        findPreference("setting_throw_error").setOnPreferenceClickListener((v1) -> {
            throw new RuntimeException("Test Exception,Don't issue it in Github!!!");
        });

        Consumer<Boolean> uploadRequest = (b) -> {
            new Thread(() -> {
                try {
                    String body = Jsoup.connect(IOUtil.base + RefreshResource.INSTANCE.getPath())
                            .ignoreContentType(true)
                            .method(RefreshResource.INSTANCE.getMethod() == AbstractServlet.Method.GET ? Connection.Method.GET : Connection.Method.POST)
                            .data("forceRefreshEx_diff",String.valueOf(b))
                            .timeout(5000)
                            .execute().body();
                    CrashHandler.getCurrentActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), body, Toast.LENGTH_LONG).show();
                    });
                } catch (IOException e) {
                    CrashHandler.getCurrentActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "无法连接到后端，请检查后端启用状态", Toast.LENGTH_LONG).show();
                    });
                }

            }).start();
        };
        findPreference("setting_remote_resource_refresh").setOnPreferenceClickListener((v) -> {
            new AlertDialog.Builder(getContext()).setTitle("强制刷新定数资源?")
                    .setMessage("若Arcaea启动时一直白屏,请禁用该模块并等待Arcaea中文维基更新定数详单")
                    .setPositiveButton("是", (dialogInterface, i) -> {
                        uploadRequest.accept(true);
                    })
                    .setNegativeButton("否", (a, b) -> {
                        uploadRequest.accept(false);
                    })
                    .show();
            return true;
        });

        findPreference("setting_clean_cache").setOnPreferenceClickListener((v1) -> {
            File p = ((CrashHandler) CrashHandler.getCurrentActivity().getApplication()).getCatcher().getFile();

            for (File a : p.getParentFile().listFiles()) {
                if (a.getName().equals(p.getName())) {
                    continue;
                }
                a.delete();
            }

            Toast.makeText(getContext(), "清理完成...!", Toast.LENGTH_SHORT).show();
            return true;
        });

        findPreference("setting_vName").setSummary(BuildConfig.VERSION_NAME);
        findPreference("setting_vCode").setSummary(String.valueOf(BuildConfig.VERSION_CODE));
        findPreference("setting_goGithub").setOnPreferenceClickListener((v) -> {
            Uri uri = Uri.parse("https://github.com/kagg886/ArcaeaB30Fucker");
            CrashHandler.getCurrentActivity().startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        });
    }
}