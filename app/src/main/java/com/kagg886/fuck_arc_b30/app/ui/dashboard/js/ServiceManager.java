package com.kagg886.fuck_arc_b30.app.ui.dashboard.js;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.webkit.JavaScriptReplyProxy;
import androidx.webkit.WebMessageCompat;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;
import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.BuildConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @Description
 * @Author kagg886
 * @Date 2023/11/22 下午2:13
 */

@SuppressLint("RequiresFeature")
public class ServiceManager implements WebViewCompat.WebMessageListener {
    private static final ServiceManager INSTANCE = new ServiceManager();

    private final List<BiConsumer<JSPacket, JSAPI>> receiver = new ArrayList<>();


    private ServiceManager() {

    }

    public static ServiceManager getInstance() {
        return INSTANCE;
    }

    public void injectJSObject(WebView view0, Runnable error) {
        if (!WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
            error.run();
        }
        WebViewCompat.addWebMessageListener(view0, "native", new HashSet<>(List.of(BuildConfig.HMR)), this);
    }

    public List<BiConsumer<JSPacket, JSAPI>> getReceiver() {
        return receiver;
    }

    @Override
    public void onPostMessage(@NonNull @NotNull WebView view, @NonNull @NotNull WebMessageCompat message, @NonNull @NotNull Uri sourceOrigin, boolean isMainFrame, @NonNull @NotNull JavaScriptReplyProxy replyProxy) {
        JSPacket p = JSON.parseObject(message.getData(), JSPacket.class);
        if (p == null) {
            Log.i("JSBridge", "[Server]throw a packet:" + message.getData());
            return;
        }
        Log.i("JSBridge", "[Server]recv->" + JSON.toJSONString(p));
        CompletableFuture.runAsync(() -> receiver.forEach((a) -> a.accept(p, new JSAPI() {
            @Override
            public void postMessage(Object reply) {
                replyProxy.postMessage(JSON.toJSONString(p.asReply(reply)));
            }

            @Override
            public void postError(String cause) {
                replyProxy.postMessage(JSON.toJSONString(p.asError(cause)));
            }
        })));
    }
}
