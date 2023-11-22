package com.kagg886.fuck_arc_b30.app.ui.dashboard;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.webkit.*;
import com.kagg886.fuck_arc_b30.BuildConfig;
import com.kagg886.fuck_arc_b30.databinding.FragmentBest30Binding;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Best30Fragment extends Fragment {

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

        WebViewCompat.WebMessageListener myListener = (view, message, sourceOrigin, isMainFrame, replyProxy) -> {
            replyProxy.postMessage("Got it!");
        };

        HashSet<String> allowedOriginRules = new HashSet<>(List.of(BuildConfig.HMR));
        WebViewCompat.addWebMessageListener(view, "native", allowedOriginRules, myListener);

        view.loadUrl(BuildConfig.HMR);

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

}