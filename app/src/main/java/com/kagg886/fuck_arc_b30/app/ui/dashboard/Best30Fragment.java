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

        if (packet.getType().equals("assets")) {
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Bitmap b = IOUtil.loadArcaeaResource(packet.getData());

                if (b == null) {
                    throw new IOException("failed to create bitmap:" + packet.getData());
                }
                b.compress(Bitmap.CompressFormat.PNG, 90, output);

                api.postMessage(new String(Base64.getEncoder().encode(output.toByteArray()), StandardCharsets.UTF_8));
                b.recycle();
            } catch (Exception e) {
                api.postError(e.getMessage());
            }
        }

        if (packet.getType().startsWith("loadArcaeaSong")) {
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Bitmap origin = IOUtil.loadArcaeaSongImage(JSON.parseObject(packet.getData(), SingleSongData.class));
                if (origin == null) {
                    throw new IOException("not find the resource:" + packet.getData());
                }

                Bitmap bmCopy = Bitmap.createBitmap(origin.getWidth(), origin.getHeight(), origin.getConfig());

                Paint paint = new Paint();
                Canvas canvas = new Canvas(bmCopy);
                canvas.drawBitmap(origin, new Matrix(), paint);
                origin.recycle();

                bmCopy = doBlur(bmCopy, 5, false);
                bmCopy.compress(Bitmap.CompressFormat.PNG, 90, output);
                api.postMessage(new String(Base64.getEncoder().encode(output.toByteArray()), StandardCharsets.UTF_8));


                bmCopy.recycle();
            } catch (Exception e) {
                api.postError(e.getMessage());
            }
        }

        if (packet.getType().equals("rollback")) {
            Toast.makeText(requireActivity(), packet.getData(), Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).navigateTo(R.id.navigation_home);
        }
    }


    private static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}