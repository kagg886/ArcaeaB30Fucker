package com.kagg886.fuck_arc_b30.app.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.kagg886.fuck_arc_b30.BuildConfig;
import com.kagg886.fuck_arc_b30.R;
import com.kagg886.fuck_arc_b30.app.MainActivity;
import com.kagg886.fuck_arc_b30.app.ui.dashboard.js.JSAPI;
import com.kagg886.fuck_arc_b30.app.ui.dashboard.js.JSPacket;
import com.kagg886.fuck_arc_b30.app.ui.dashboard.js.ServiceManager;
import com.kagg886.fuck_arc_b30.databinding.FragmentBest30Binding;
import com.kagg886.fuck_arc_b30.server.model.UserProfile;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Profile;
import com.kagg886.fuck_arc_b30.util.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
                api.postMessage(IOUtil.fetch(Profile.INSTANCE, UserProfile.class));
            } catch (IOException e) {
                api.postError(e.getMessage());
            }
        }

        if (packet.getType().equals("assets")) {
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Bitmap b = IOUtil.loadArcaeaResource(packet.getData());
                b.compress(Bitmap.CompressFormat.PNG, 90, output);

                api.postMessage(new String(Base64.getEncoder().encode(output.toByteArray()), StandardCharsets.UTF_8));
                b.recycle();
            } catch (Exception e) {
                api.postError(e.getMessage());
            }
        }

        if (packet.getType().equals("rollback")) {
            ((MainActivity) requireActivity()).navigateTo(R.id.navigation_home);
        }
    }
}

//package com.kagg886.fuck_arc_b30.app.ui.dashboard;
//
//import android.annotation.SuppressLint;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.Fragment;
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.kagg886.fuck_arc_b30.R;
//import com.kagg886.fuck_arc_b30.app.CrashHandler;
//import com.kagg886.fuck_arc_b30.databinding.FragmentBest30Binding;
//import com.kagg886.fuck_arc_b30.server.model.Best30Model;
//import com.kagg886.fuck_arc_b30.server.model.SingleSongData;
//import com.kagg886.fuck_arc_b30.server.model.UserProfile;
//import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
//import com.kagg886.fuck_arc_b30.server.servlet.impl.Best30;
//import com.kagg886.fuck_arc_b30.server.servlet.impl.Profile;
//import com.kagg886.fuck_arc_b30.util.IOUtil;
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Best30Fragment extends Fragment {
//
//    private FragmentBest30Binding binding;
//
//    @SuppressLint("DefaultLocale")
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//
//        binding = FragmentBest30Binding.inflate(inflater, container, false);
//
//        new Thread(() -> {
//            try {
//                //get b30 body
//                String body = Jsoup.connect(IOUtil.base + Best30.INSTANCE.getPath())
//                        .ignoreContentType(true)
//                        .method(Best30.INSTANCE.getMethod() == AbstractServlet.Method.GET ? Connection.Method.GET : Connection.Method.POST)
//                        .timeout(5000)
//                        .execute().body();
//
//                JSONArray arr = JSON.parseObject(body).getJSONArray("data");
//
//                if (arr == null) {
//                    Log.e(getClass().getName(), "B30 fetch failed!,response:" + body);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(CrashHandler.getCurrentActivity());
//                    builder.setTitle("b30拉取失败");
//                    builder.setMessage("拉取body如下:\n" + body + "\n截图前往github提交issue！");
//                    CrashHandler.getCurrentActivity().runOnUiThread(() -> {
//                        builder.show();
//                        CrashHandler.getCurrentActivity().navigateTo(R.id.navigation_home);
//                    });
//                    return;
//                }
//                List<Best30Model> models = new ArrayList<>();
//                arr.forEach((model) -> {
//                    JSONObject source = (JSONObject) model;
//                    SingleSongData data = JSON.parseObject(source.getJSONObject("data").toString(), SingleSongData.class);
//                    //学艺不精，fastjson嵌套对象解析不会写，什么鸡掰东西.jpg
//                    Best30Model best30Model = new Best30Model(
//                            source.getString("name"),
//                            data,
//                            source.getDouble("ptt"),
//                            source.getDouble("ex_diff")
//                    );
//                    models.add(best30Model);
//                });
//                for (Best30Model model : models) {
//                    CrashHandler.getCurrentActivity().runOnUiThread(() -> {
//                        try {
//                            addB30Model(model);
//                        } catch (Exception e) {
//                            Log.e(getClass().getName(), "load b30 status Error!" + model, e);
//                        }
//                    });
////                    break; //只运行一个的测试
//                }
//
//                //get user body
//                UserProfile profile = IOUtil.fetch(Profile.INSTANCE, UserProfile.class);
//
//                if (profile == null) {
//                    double pttReal = models.stream().mapToDouble(Best30Model::getPtt).sum() / 30;
//                    double pttMax = pttReal + models.subList(0, 10).stream().mapToDouble(Best30Model::getPtt).sum() / 10;
//                    pttMax /= 2;
//                    profile = new UserProfile("RealPtt服务不可用", pttReal, pttReal, 0, pttMax);
//                }
//
//
//                double b30Avt = profile.getPttB30();
//                int ratingType;
//
//                if (b30Avt > 13.00) {
//                    ratingType = 7;
//                } else if (b30Avt > 12.50) {
//                    ratingType = 6;
//                } else if (b30Avt > 12.00) {
//                    ratingType = 5;
//                } else if (b30Avt > 11.00) {
//                    ratingType = 4;
//                } else if (b30Avt > 10.00) {
//                    ratingType = 3;
//                } else if (b30Avt > 7.00) {
//                    ratingType = 2;
//                } else if (b30Avt > 3.50) {
//                    ratingType = 1;
//                } else {
//                    ratingType = 0;
//                }
//
//                Bitmap ratingImg = IOUtil.loadArcaeaResource("img/rating_" + ratingType + ".png");
//                UserProfile finalProfile = profile;
//                CrashHandler.getCurrentActivity().runOnUiThread(() -> {
//                    binding.fragmentB30User.setText(finalProfile.getName());
//                    binding.fragmentB30PttMax.setText(String.format("无推分最高PTT:%.2f", finalProfile.getPttMax()));
//                    binding.fragmentB30PttB30.setText(String.format("B30:%.2f", finalProfile.getPttB30()));
//                    binding.fragmentB30PttR10.setText(String.format("R10:%.2f", finalProfile.getPttR10()));
//                    binding.fragmentB30Ptt.setText(String.format("%.2f", finalProfile.getPttReal()));
//
//                    binding.fragmentB30Bg.setBackground(new BitmapDrawable(getResources(), ratingImg));
//                });
//            } catch (IOException e) {
//                CrashHandler.getCurrentActivity().runOnUiThread(() -> {
//                    Toast.makeText(CrashHandler.getCurrentActivity(), "B30拉取失败，请检查服务端存活状态", Toast.LENGTH_SHORT).show();
//                    CrashHandler.getCurrentActivity().navigateTo(R.id.navigation_home);
//                });
//
//            }
//        }).start();
//
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//    @SuppressLint("DefaultLocale")
//    private void addB30Model(Best30Model best30Model) {
//        SingleSongData data = best30Model.getData();
//        View view = LayoutInflater.from(CrashHandler.getCurrentActivity()).inflate(R.layout.card_b30, null);
//        view.setId(View.generateViewId());
//
//        view.setOnClickListener((v) -> {
//            new AlertDialog.Builder(CrashHandler.getCurrentActivity()).setTitle("DebugWindow")
//                    .setMessage(JSON.toJSONString(best30Model)).show();
//        });
//
//        TextView b30Name, b30Score, b30Ptt, b30CountP, b30CountF, b30CountL;
//        b30Name = view.findViewById(R.id.b30_name);
//        b30Score = view.findViewById(R.id.b30_score);
//        b30Ptt = view.findViewById(R.id.b30_ptt);
//        b30CountP = view.findViewById(R.id.b30_count_p);
//        b30CountF = view.findViewById(R.id.b30_count_f);
//        b30CountL = view.findViewById(R.id.b30_count_l);
//
//
//        ImageView b30ScoreType = view.findViewById(R.id.b30_scoreType);
//        ImageView b30ClearType = view.findViewById(R.id.b30_clearType);
//        ImageView b30SongImg = view.findViewById(R.id.b30_song_img);
//        ImageView b30DiffType = view.findViewById(R.id.b30_diff_type);
//
//
//        String name = best30Model.getName();
//        if (name.length() > 18) {
//            name = name.substring(0, 16) + "...";
//        }
//        b30Name.setText(name);
//        b30Score.setText(String.valueOf(data.getScore()));
//        b30Ptt.setText(String.format("%.2f(%.2f)", best30Model.getPtt(), best30Model.getEx_diff()));
//        b30CountP.setText(String.format("Pure:%d(%d)", data.getPerfectCount(), data.getShinyPerfectCount()));
//        b30CountF.setText(String.format("Far:%d", data.getFarCount()));
//        b30CountL.setText(String.format("Lost:%d", data.getLostCount()));
//
//        new Thread(() -> {
//            try {
//                String clearType = switch (best30Model.getData().getClearStatus()) {
//                    case 0 -> "fail";
//                    case 1 -> "normal";
//                    case 2 -> "full";
//                    case 3 -> "pure";
//                    case 4 -> "easy";
//                    case 5 -> "hard";
//                    default ->
//                            throw new IllegalStateException("Unexpected value: " + best30Model.getData().getClearStatus());
//                };
//
//                //EX+：9900000及以上（含PM）
//                //EX：9800000-9900000
//                //AA：9500000-9800000
//                //A：9200000-9500000
//                //B：8900000-9200000
//                //C：8600000-8900000
//                //D：8600000以下
//                String scoreType;
//                if (data.getScore() > 9900000) {
//                    scoreType = "explus";
//                } else if (data.getScore() > 9800000) {
//                    scoreType = "ex";
//                } else if (data.getScore() > 9500000) {
//                    scoreType = "aa";
//                } else if (data.getScore() > 9200000) {
//                    scoreType = "a";
//                } else if (data.getScore() > 8900000) {
//                    scoreType = "b";
//                } else if (data.getScore() > 8600000) {
//                    scoreType = "c";
//                } else {
//                    scoreType = "d";
//                }
//
//                Bitmap songImg, clearTypeImg, scoreTypeImg, diffTypeImg;
//                songImg = IOUtil.loadArcaeaSongImage(data);
//                clearTypeImg = IOUtil.loadArcaeaResource("img/clear_type/" + clearType + ".png");
//                scoreTypeImg = IOUtil.loadArcaeaResource("img/grade/mini/" + scoreType + ".png");
//                diffTypeImg = IOUtil.loadArcaeaResource("layouts/multiplayer/tag-difficulty-" + best30Model.getData().getDifficulty().name().toLowerCase() + ".png");
//                CrashHandler.getCurrentActivity().runOnUiThread(() -> {
//                    b30SongImg.setImageBitmap(songImg);
//                    b30ClearType.setImageBitmap(clearTypeImg);
//                    b30ScoreType.setImageBitmap(scoreTypeImg);
//                    b30DiffType.setImageBitmap(diffTypeImg);
//                });
//            } catch (IOException e) {
//                CrashHandler.getCurrentActivity().runOnUiThread(() -> {
//                    Toast.makeText(CrashHandler.getCurrentActivity(), "B30图片加载失败，请检查服务端存活状态", Toast.LENGTH_SHORT).show();
//                    CrashHandler.getCurrentActivity().navigateTo(R.id.navigation_home);
//                });
//            }
//        }).start();
//
//
//        binding.b30Container.addView(view);
//        binding.b30Flow.addView(view);
//    }
//}