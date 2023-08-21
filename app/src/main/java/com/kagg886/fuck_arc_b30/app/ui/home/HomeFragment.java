package com.kagg886.fuck_arc_b30.app.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.kagg886.fuck_arc_b30.app.CrashHandler;
import com.kagg886.fuck_arc_b30.R;
import com.kagg886.fuck_arc_b30.databinding.FragmentHomeBinding;
import com.kagg886.fuck_arc_b30.server.servlet.impl.Version;
import com.kagg886.fuck_arc_b30.util.IOUtil;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (binding == null) {
                return;
            }
            TextView emoji = binding.fragmentHomeLoading;
            TextView message = binding.fragmentHomeStatus;
            switch (msg.what) {
                case STATUS_LOADING -> {
                    emoji.setText(R.string.loading);
                    message.setText(R.string.loading_text);
                }

                case STATUS_LOADING_FAILED -> {
                    emoji.setText(R.string.loading_failed);
                    message.setText(R.string.loading_text_failed);
                }
                case STATUS_LOADING_SUCCESS -> {
                    emoji.setText(R.string.loading_success);
                    message.setText(R.string.loading_text_success);
                }
            }
        }
    };

    private static final int STATUS_LOADING = 0;
    private static final int STATUS_LOADING_SUCCESS = 1;
    private static final int STATUS_LOADING_FAILED = 2;

    private boolean isRefreshing = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.cardView.setOnClickListener((v) -> {
            if (!isRefreshing) reloadServerStatus();
        });
        reloadServerStatus();
        return binding.getRoot();
    }

    @SuppressLint("DefaultLocale")
    private void reloadServerStatus() {
        isRefreshing = true;
        mHandler.sendEmptyMessage(STATUS_LOADING);
        new Thread(() -> {
            try {
                Version.AppVersionInfo info = IOUtil.fetch(Version.INSTANCE, Version.AppVersionInfo.class);
                mHandler.sendEmptyMessage(STATUS_LOADING_SUCCESS);
                CrashHandler.getCurrentActivity().runOnUiThread(() -> binding.fragmentHomeMessage.setText(String.format("Server Version:\nName:%s\nCode:%d", info.getVersionName(), info.getVersionCode())));
            } catch (IOException e) {
                mHandler.sendEmptyMessage(STATUS_LOADING_FAILED);
                CrashHandler.getCurrentActivity().runOnUiThread(() -> binding.fragmentHomeMessage.setText(e.getMessage() + "\n触摸emoji以重试"));
            }
            isRefreshing = false;
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}