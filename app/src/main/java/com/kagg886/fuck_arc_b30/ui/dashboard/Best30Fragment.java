package com.kagg886.fuck_arc_b30.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.MainActivity;
import com.kagg886.fuck_arc_b30.R;
import com.kagg886.fuck_arc_b30.databinding.FragmentBest30Binding;
import com.kagg886.fuck_arc_b30.server.model.Best30Model;
import com.kagg886.fuck_arc_b30.server.model.SingleSongData;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Best30;
import com.kagg886.fuck_arc_b30.util.IOUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Best30Fragment extends Fragment {

    private FragmentBest30Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBest30Binding.inflate(inflater, container, false);

        new Thread(() -> {
            try {
                String body = Jsoup.connect(IOUtil.base + Best30.INSTANCE.getPath())
                        .ignoreContentType(true)
                        .method(Best30.INSTANCE.getMethod() == AbstractServlet.Method.GET ? Connection.Method.GET : Connection.Method.POST)
                        .timeout(5000)
                        .execute().body();

                JSONArray arr = JSON.parseObject(body).getJSONArray("data");

                if (arr == null) {
                    Log.e(getClass().getName(),"B30 fetch failed!,response:" + body);
                    ((MainActivity) getActivity()).navigateTo(R.id.navigation_home);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("b30拉取失败");
                    builder.setMessage("拉取body如下:\n" + body + "\n截图前往github提交issue！");
                    getActivity().runOnUiThread(builder::show);
                    ((MainActivity) getActivity()).navigateTo(R.id.navigation_home);
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
                for (Best30Model model : models) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            addB30Model(model);
                        } catch (Exception e) {
                            Log.e(getClass().getName(), "load b30 status Error!" + model, e);
                        }
                    });
//                    break; //只运行一个的测试
                }

                models.stream().forEach((v) -> {
                    Log.d("B30i",String.format("%.2f",v.getPtt()));
                });
                double b30Avt = models.stream().mapToDouble(Best30Model::getPtt).sum() / 30; //b30的ptt
                int ratingType;

                if (b30Avt > 13.00) {
                    ratingType = 7;
                } else if (b30Avt > 12.50) {
                    ratingType = 6;
                } else if (b30Avt > 12.00) {
                    ratingType = 5;
                } else if (b30Avt > 11.00) {
                    ratingType = 4;
                } else if (b30Avt > 10.00) {
                    ratingType = 3;
                } else if (b30Avt > 7.00) {
                    ratingType = 2;
                } else if (b30Avt > 3.50) {
                    ratingType = 1;
                } else {
                    ratingType = 0;
                }

                Bitmap ratingImg = IOUtil.loadArcaeaResource("img/rating_" + ratingType + ".png");
                getActivity().runOnUiThread(() -> {
                    binding.fragmentB30Ptt.setText(String.format("%.2f",b30Avt));
                    binding.fragmentB30Bg.setBackground(new BitmapDrawable(getResources(),ratingImg));
                });
            } catch (IOException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "B30拉取失败，请检查服务端存活状态", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).navigateTo(R.id.navigation_home);
                });

            }
        }).start();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("DefaultLocale")
    private void addB30Model(Best30Model best30Model) {
        SingleSongData data = best30Model.getData();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.card_b30, null);
        view.setId(View.generateViewId());

        view.setOnClickListener((v) -> {
            new AlertDialog.Builder(getActivity()).setTitle("DebugWindow")
                    .setMessage(JSON.toJSONString(best30Model)).show();
        });

        TextView b30Name, b30Score, b30Ptt, b30CountP, b30CountF, b30CountL;
        b30Name = view.findViewById(R.id.b30_name);
        b30Score = view.findViewById(R.id.b30_score);
        b30Ptt = view.findViewById(R.id.b30_ptt);
        b30CountP = view.findViewById(R.id.b30_count_p);
        b30CountF = view.findViewById(R.id.b30_count_f);
        b30CountL = view.findViewById(R.id.b30_count_l);


        ImageView b30ScoreType = view.findViewById(R.id.b30_scoreType);
        ImageView b30ClearType = view.findViewById(R.id.b30_clearType);
        ImageView b30SongImg = view.findViewById(R.id.b30_song_img);


        String name = best30Model.getName();
        if (name.length() > 18) {
            name = name.substring(0,16) + "...";
        }
        b30Name.setText(name);
        b30Score.setText(String.valueOf(data.getScore()));
        b30Ptt.setText(String.format("%.2f(%.2f)", best30Model.getPtt(), best30Model.getEx_diff()));
        b30CountP.setText(String.format("Pure:%d(%d)", data.getPerfectCount(), data.getShinyPerfectCount()));
        b30CountF.setText(String.format("Far:%d", data.getFarCount()));
        b30CountL.setText(String.format("Lost:%d", data.getLostCount()));

        new Thread(() -> {
            try {
                String clearType = switch (best30Model.getData().getClearStatus()) {
                    case 0 -> "fail";
                    case 1 -> "normal";
                    case 2 -> "full";
                    case 3 -> "pure";
                    case 4 -> "easy";
                    case 5 -> "hard";
                    default ->
                            throw new IllegalStateException("Unexpected value: " + best30Model.getData().getClearStatus());
                };

                //EX+：9900000及以上（含PM）
                //EX：9800000-9900000
                //AA：9500000-9800000
                //A：9200000-9500000
                //B：8900000-9200000
                //C：8600000-8900000
                //D：8600000以下
                String scoreType;
                if (data.getScore() > 9900000) {
                    scoreType = "explus";
                } else if (data.getScore() > 9800000) {
                    scoreType = "ex";
                } else if (data.getScore() > 9500000) {
                    scoreType = "aa";
                } else if (data.getScore() > 9200000) {
                    scoreType = "a";
                } else if (data.getScore() > 8900000) {
                    scoreType = "b";
                } else if (data.getScore() > 8600000) {
                    scoreType = "c";
                } else {
                    scoreType = "d";
                }

                Bitmap songImg, clearTypeImg, scoreTypeImg;
                songImg = IOUtil.loadArcaeaSongImage(data);
                clearTypeImg = IOUtil.loadArcaeaResource("img/clear_type/" + clearType + ".png");
                scoreTypeImg = IOUtil.loadArcaeaResource("img/grade/mini/" + scoreType + ".png");

                getActivity().runOnUiThread(() -> {
                    b30SongImg.setImageBitmap(songImg);
                    b30ClearType.setImageBitmap(clearTypeImg);
                    b30ScoreType.setImageBitmap(scoreTypeImg);
                });
            } catch (IOException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "B30图片加载失败，请检查服务端存活状态", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).navigateTo(R.id.navigation_home);
                });

            }
        }).start();


        binding.b30Container.addView(view);
        binding.b30Flow.addView(view);
    }
}