package com.kagg886.fuck_arc_b30.app.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.BuildConfig;
import com.kagg886.fuck_arc_b30.R;
import com.kagg886.fuck_arc_b30.app.ui.dashboard.js.JSAPI;
import com.kagg886.fuck_arc_b30.app.ui.dashboard.js.JSPacket;
import com.kagg886.fuck_arc_b30.app.ui.dashboard.js.ServiceManager;
import com.kagg886.fuck_arc_b30.databinding.FragmentBest30Binding;
import com.kagg886.fuck_arc_b30.server.model.Best30Model;
import com.kagg886.fuck_arc_b30.server.model.SingleSongData;
import com.kagg886.fuck_arc_b30.server.model.UserProfile;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Best30;
import com.kagg886.fuck_arc_b30.server.servlet.impl.GetSongInfoById;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Profile;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Version;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class Best30Fragment extends Fragment implements BiConsumer<JSPacket, JSAPI> {

    private FragmentBest30Binding binding;

    private WebView view;

    @Override
    public void onViewCreated(@NonNull @NotNull View view0, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view0, savedInstanceState);
        ((MenuHost) requireActivity()).addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.fragment_b30_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull @NotNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menu_share -> {
                        Bitmap bitmap = transfer();

                        File f = requireActivity().getCacheDir().toPath().resolve("share.png").toFile();
                        if (f.exists()) {
                            f.delete();
                        }
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try (FileOutputStream s = new FileOutputStream(f)){
                            bitmap.compress(Bitmap.CompressFormat.PNG,80,s);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        var uri = FileProvider.getUriForFile(requireActivity(), requireActivity().getPackageName()+".fileprovider", f);
                        startActivity(new Intent() {{
                            setAction(ACTION_SEND);
                            putExtra(Intent.EXTRA_STREAM,uri);
                            setType("image/png");
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }});
//                            action = Intent.ACTION_SEND
//                            putExtra(Intent.EXTRA_STREAM, uri)
//                            type = "image/png"
//                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    case R.id.menu_save -> {
                        Bitmap bitmap = transfer();

                        ContentResolver contentResolver = requireActivity().getContentResolver();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, "Pictures/arcaea_b30");
                        contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "b30.png");
                        contentValues.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
                        contentValues.put(MediaStore.Images.ImageColumns.WIDTH, bitmap.getWidth());
                        contentValues.put(MediaStore.Images.ImageColumns.HEIGHT, bitmap.getHeight());
                        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                        try(OutputStream out = contentResolver.openOutputStream(uri)) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bitmap.recycle();
                        Toast.makeText(requireActivity(), "导出成功!请前往相册查看", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private Bitmap transfer() {
        view.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0, view.getMeasuredHeight(), paint);
        view.draw(canvas);
        return bitmap;
    }

    @SuppressLint({"DefaultLocale", "SetJavaScriptEnabled", "RequiresFeature"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        WebView.enableSlowWholeDocumentDraw();
        WebView.setWebContentsDebuggingEnabled(true);

        binding = FragmentBest30Binding.inflate(inflater, container, false);
        view = new WebView(requireActivity().getApplicationContext());
        binding.getRoot().addView(view);


        WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .setDomain("616sb.com")
                .addPathHandler("/", new WebViewAssetLoader.AssetsPathHandler(requireContext()))//对所有资源优先检查离线资源
                .setHttpAllowed(true)
                .build();

        view.setWebViewClient(new WebViewClientCompat() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.i(Best30Fragment.class.getName(), "route access:" + request.getUrl());
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        WebSettings webSetting = view.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);

        ServiceManager.getInstance().injectJSObject(view, () -> {
            Toast.makeText(requireActivity(), "当前应用不支持使用WebView渲染b30图片，请回滚旧版。", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        });

        ServiceManager.getInstance().getReceiver().add(this);

        binding.getRoot().setOnRefreshListener(() -> {
            binding.getRoot().setRefreshing(false);
        });

        AlertDialog dialog = new AlertDialog.Builder(requireActivity()).setTitle("检查后端存活状态...")
                .setMessage("请稍等片刻...")
                .create();

        dialog.show();


        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
                IOUtil.fetch(Version.INSTANCE, Version.AppVersionInfo.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            requireActivity().runOnUiThread(() -> {
                dialog.dismiss();
                dialog.cancel();
                view.loadUrl(BuildConfig.HMR + "/index.html");
                binding.getRoot().setOnRefreshListener(() -> {
                    view.loadUrl(BuildConfig.HMR + "/index.html");
                    binding.getRoot().setRefreshing(false);
                });
            });
        }).exceptionally((e) -> {
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireActivity(), "无法连接到Arcaea后端，请检查后台存活状态", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            });
            return null;
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
                        Toast.makeText(requireActivity(), "无法连接到Arcaea的后端服务，请检查Arcaea后台存活状态", Toast.LENGTH_SHORT).show();
                        requireActivity().finish();
                    });
                } else {
                    api.postMessage(p);
                }

            } catch (IOException ignored) {
            }
        }

        //TODO 等待真机运行测试
        if (packet.getType().equals("songInfo")) {
            String body;
            try {
                body = Jsoup.connect(IOUtil.base + GetSongInfoById.INSTANCE.getPath())
                        .data("id",JSON.parseObject(packet.getData()).getString("id"))
                        .ignoreContentType(true)
                        .method(Best30.INSTANCE.getMethod() == AbstractServlet.Method.GET ? Connection.Method.GET : Connection.Method.POST)
                        .timeout(5000)
                        .execute().body();

                api.postMessage(JSON.parseObject(body).getJSONObject("data"));
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireActivity(), "拉取b30失败，请检查注入到Arcaea的后端是否在线！", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                });
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
                    requireActivity().finish();
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