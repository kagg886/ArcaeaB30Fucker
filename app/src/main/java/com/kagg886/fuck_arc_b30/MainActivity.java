package com.kagg886.fuck_arc_b30;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;

/**
 * @author kagg886
 * @date 2023/8/13 11:56
 **/
public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView v = new TextView(this);
        v.setText("请打开Arcaea");
        setContentView(v);
    }
}
