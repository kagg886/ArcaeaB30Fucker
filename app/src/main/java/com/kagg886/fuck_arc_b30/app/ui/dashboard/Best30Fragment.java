package com.kagg886.fuck_arc_b30.app.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.BuildConfig;
import com.kagg886.fuck_arc_b30.R;
import com.kagg886.fuck_arc_b30.app.CrashHandler;
import com.kagg886.fuck_arc_b30.app.MainActivity;
import com.kagg886.fuck_arc_b30.app.ui.dashboard.js.JSAPI;
import com.kagg886.fuck_arc_b30.app.ui.dashboard.js.JSPacket;
import com.kagg886.fuck_arc_b30.app.ui.dashboard.js.ServiceManager;
import com.kagg886.fuck_arc_b30.databinding.FragmentBest30Binding;
import com.kagg886.fuck_arc_b30.server.model.Best30Model;
import com.kagg886.fuck_arc_b30.server.model.SingleSongData;
import com.kagg886.fuck_arc_b30.server.model.UserProfile;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Best30;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Profile;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class Best30Fragment extends Fragment implements BiConsumer<JSPacket, JSAPI> {

    private FragmentBest30Binding binding;

    private WebView view;

    @SuppressLint({"DefaultLocale", "SetJavaScriptEnabled", "RequiresFeature"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBest30Binding.inflate(inflater, container, false);
        view = new WebView(requireActivity().getApplicationContext());
        binding.getRoot().addView(view);


        WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .setDomain("616sb.com")
                .addPathHandler("/", new WebViewAssetLoader.AssetsPathHandler(requireContext()))//对所有资源优先检查离线资源
                .build();
        WebView.setWebContentsDebuggingEnabled(true);
        view.setWebViewClient(new WebViewClientCompat() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        WebSettings webSetting = view.getSettings();
        webSetting.setJavaScriptEnabled(true);

        ServiceManager.getInstance().injectJSObject(view, () -> {
            Toast.makeText(requireActivity(), "当前应用不支持使用WebView渲染b30图片，请回滚旧版。", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        });

        ServiceManager.getInstance().getReceiver().add(this);

        view.loadUrl(BuildConfig.HMR);

        binding.getRoot().setOnRefreshListener(() -> {
            view.loadUrl(BuildConfig.HMR);
            binding.getRoot().setRefreshing(false);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //回收WebView
        view.loadUrl("about:blank");
        binding.getRoot().removeView(view);
        view.stopLoading();
        view.getSettings().setJavaScriptEnabled(false);
        view.clearHistory();
        view.clearCache(true);
        view.removeAllViewsInLayout();
        view.removeAllViews();
        view.setWebViewClient(null);
        view.setWebChromeClient(null);
        view.destroy();
        view = null;

        binding = null;
    }

    @Override
    public void accept(JSPacket packet, JSAPI api) {

        if (packet.getType().equals("getUserProfile")) {
            try {
                UserProfile p = IOUtil.fetch(Profile.INSTANCE, UserProfile.class);
                if (p == null) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireActivity(), "无法连接到Arcaea的后端服务，请检查Arcaea后台存货状态", Toast.LENGTH_SHORT).show();
                        ((MainActivity) requireActivity()).navigateTo(R.id.navigation_home);
                    });
                } else {
                    api.postMessage(p);
                }

            } catch (IOException ignored) {
            }
        }

        if (packet.getType().equals("b30")) {
            //get b30 body
            String body = null;
            try {
                body = Jsoup.connect(IOUtil.base + Best30.INSTANCE.getPath())
                        .ignoreContentType(true)
                        .method(Best30.INSTANCE.getMethod() == AbstractServlet.Method.GET ? Connection.Method.GET : Connection.Method.POST)
                        .timeout(5000)
                        .execute().body();
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireActivity(), "拉取b30失败，请检查注入到Arcaea的后端是否在线！", Toast.LENGTH_SHORT).show();
                    ((MainActivity) requireActivity()).navigateTo(R.id.navigation_home);
                });
            }

            JSONArray arr = Objects.requireNonNull(JSON.parseObject(body)).getJSONArray("data");

            if (arr == null) {
                api.postError("b30拉取失败!,body如下:" + body);
                return;
            }
            List<Best30Model> models = new ArrayList<>();
            arr.forEach((model) -> {
                JSONObject source = (JSONObject) model;
                SingleSongData data = JSON.parseObject(source.getJSONObject("data").toString(), SingleSongData.class);
                //学艺不精，fastjson嵌套对象解析不会写，什么鸡掰东西.jpg
                Best30Model best30Model = new Best30Model(
                        source.getString("name"),
                        data,
                        source.getDouble("ptt"),
                        source.getDouble("ex_diff")
                );
                models.add(best30Model);
            });
            api.postMessage(models);
        }
    }
}