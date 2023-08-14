package com.kagg886.fuck_arc_b30.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.kagg886.fuck_arc_b30.R;
import com.kagg886.fuck_arc_b30.databinding.FragmentBest30Binding;
import com.kagg886.fuck_arc_b30.server.model.Best30Model;
import com.kagg886.fuck_arc_b30.server.servlet.impl.AssetsGet;
import com.kagg886.fuck_arc_b30.util.IOUtil;

import java.io.IOException;

public class Best30Fragment extends Fragment {

    private FragmentBest30Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBest30Binding.inflate(inflater, container, false);

        new Thread(() -> {
            try {
                 Bitmap a = IOUtil.loadArcaeaResource("img/rating_7.png");
                 getActivity().runOnUiThread(() -> binding.fragmentB30Bg.setBackground(new BitmapDrawable(getResources(),a)));
            } catch (IOException ignored) {
//                getActivity().runOnUiThread(() ->
//                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2).navigate(R.id.navigation_home)
//                );
            }
        }).start();

        addB30Model();
        addB30Model();
        addB30Model();
        addB30Model();
        addB30Model();
        addB30Model();
        addB30Model();
        addB30Model();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addB30Model() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.card_b30,binding.b30Container, false);
        view.setId(View.generateViewId());
        binding.b30Container.addView(view);
        binding.b30Flow.addView(view);
    }
}