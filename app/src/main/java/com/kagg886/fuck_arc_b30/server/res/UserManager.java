package com.kagg886.fuck_arc_b30.server.res;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.kagg886.fuck_arc_b30.Hooker;
import com.kagg886.fuck_arc_b30.util.ArcaeaMemReader;

/**
 * @author kagg886
 * @date 2023/8/21 16:38
 **/
public class UserManager {
    public static PackageInfo pkgInfo;
    public static String userName;

    public static void init() {
        try {
            pkgInfo = Hooker.activity.getPackageManager().getPackageInfo(Hooker.activity.getPackageName(), 0);
            SharedPreferences sp = Hooker.activity.getSharedPreferences("Cocos2dxPrefsFile", Context.MODE_PRIVATE);
            userName = sp.getString("lastLoggedInUsername", "离线");
        } catch (Exception e) {
            Log.e(UserManager.class.getName(),"user init failed!",e);
            throw new RuntimeException(e);
        }
        Log.i(UserManager.class.getName(), "user init success");
    }

}
