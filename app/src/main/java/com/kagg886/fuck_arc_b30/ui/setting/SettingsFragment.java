package com.kagg886.fuck_arc_b30.ui.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.preference.PreferenceFragmentCompat;
import com.kagg886.fuck_arc_b30.BuildConfig;
import com.kagg886.fuck_arc_b30.CrashHandler;
import com.kagg886.fuck_arc_b30.R;

import java.io.File;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        findPreference("setting_throw_error").setOnPreferenceClickListener((v1) -> {
            throw new RuntimeException("Test Exception,Don't issue it in Github!!!");
        });

        findPreference("setting_clean_cache").setOnPreferenceClickListener((v1) -> {
            File p = ((CrashHandler) getActivity().getApplication()).getCatcher().getFile();

            for (File a : p.getParentFile().listFiles()) {
                if (a.getName().equals(p.getName())) {
                    continue;
                }
                a.delete();
            }

            Toast.makeText(getContext(), "清理完成...!", Toast.LENGTH_SHORT).show();
            return true;
        });

        findPreference("setting_vName").setSummary(BuildConfig.VERSION_NAME);
        findPreference("setting_vCode").setSummary(String.valueOf(BuildConfig.VERSION_CODE));
        findPreference("setting_goGithub").setOnPreferenceClickListener((v) -> {
            Uri uri = Uri.parse("https://github.com/kagg886/ArcaeaB30Fucker");
            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        });
    }
}