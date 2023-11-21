package com.kagg886.fuck_arc_b30.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.fuck_arc_b30.Hooker;
import org.jsoup.Jsoup;
import xyz.cnyg.memoryreader.MemoryReader;
import xyz.cnyg.memoryreader.NativeMethod;
import xyz.cnyg.memoryreader.procfs.Maps;
import xyz.cnyg.memoryreader.procfs.MemoryMap;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import static com.kagg886.fuck_arc_b30.server.res.UserManager.pkgInfo;

public class ArcaeaMemReader {
    public static Boolean IsGooglePlay = true;

    public static void init() throws Exception {
        //google play版本的arc版本号不带 'c'
        IsGooglePlay = !pkgInfo.versionName.contains("c");


        //获取缓存的数据版本号
        SharedPreferences exactlyData = Hooker.activity.getSharedPreferences("arc_b30_fucker_exactly_data", Context.MODE_PRIVATE);
        String native_version = exactlyData.getString("native_version", "");

        JSONObject o;
        if (native_version.equals(pkgInfo.versionName)) {
            o = JSON.parseObject(exactlyData.getString("native_details", null));
        } else {
            JSONArray list = JSON.parseArray(Jsoup.connect("https://raw.githubusercontent.com/OllyDoge/ArcMemOffsets/main/offsets.json").execute().body());
            //光是抓取详细数据还不够，还需要比对出合适的版本号
            o = list.stream()
                    .map((v) -> ((JSONObject) v))
                    .filter((a) -> pkgInfo.versionName.equals(a.getString("gamever")))
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException("从线上配置中找不到当前版本的偏移量"));
            //保存
            exactlyData.edit()
                    .putString("native_version", pkgInfo.versionName)
                    .putString("native_details", o.toString())
                    .apply();
        }
        o = o.getJSONObject("offsets").getJSONObject("userinfo");
        ArcaeaMemReader.Profile.LocateValue = o.getString("locateValue");


        List<Long> l = Arrays.stream(o.getString("memOffset").split(",")).map((a) -> Long.parseLong(a, 16)).collect(Collectors.toList());
        long[] a = new long[l.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = l.get(i);
        }

        ArcaeaMemReader.Profile.LocateMemOffset = a;

        ArcaeaMemReader.Profile.PTT = Long.parseLong(o.getJSONObject("struct").getString("ptt").split(",")[0],16);

        Log.v(ArcaeaMemReader.class.getName(), String.format("locate:%s\noffset:%s\nptt:%s",
                        Profile.LocateValue,
                        Arrays.stream(ArcaeaMemReader.Profile.LocateMemOffset, 0, ArcaeaMemReader.Profile.LocateMemOffset.length).mapToObj(Long::toHexString).collect(Collectors.joining(",")),
                        Long.toHexString(Profile.PTT)
                )
        );
        /*{
	"gamever":"5.1.1c",
	"offsets":{
		"userinfo":{
			"locateMode":0,
			"locateValue":"libcocos2dcpp.so.1",
			"memOffset":"CF028,0,80,C8",
			"lookMode":0,
			"lookValue":"",
			"struct":{
				"uid":"C,int",
				"friendcode":"11,string",
				"username":"29,string",
				"character":"40,int",
				"ptt":"44,int"
			},
			"innerStructs":{}
		}
	}
}*/


        Log.i(ArcaeaMemReader.class.getName(), "Memory parameters get successful");
    }

    public static class Profile {
        public static String LocateValue;
        public static long[] LocateMemOffset;
        public static long PTT;
//        public static class MemOffsets_GP {
//            public static String LocateValue = "libcocos2dcpp.so.1";
//            public static long[] LocateMemOffset;
////            public static long[] LocateMemOffset = {0xCBB38, 0x0, 0x80, 0xC8};
//        }
//
//        public static class MemOffsets_APK {
//            public static String LocateValue = "libcocos2dcpp.so.1";
//
//            public static long[] LocateMemOffset = {0xCBB38, 0x0, 0x80, 0xC8};
////            public static long[] LocateMemOffset = {0xCBE20, 0x0, 0x98, 0x0, 0x0, 0x2E0, 0x4A8};
//        }
//
//        public static class ProfileStructOffsets {
//            public static long PTT = 0x44;
//        }

        public static double GetUserPtt() {
            MemoryMap libBase = Maps.GetCurrentModuleWithMatch(LocateValue);

            long Ptr = libBase.StartAddress;
            for (int i = 0; i < LocateMemOffset.length; i++) {
                Ptr += LocateMemOffset[i];
                if (Maps.AddressReadable(Ptr)) {
                    Ptr = MemoryReader.ReadInt64(Ptr);
                } else return 0.0f;
            }
            if (Maps.AddressReadable(Ptr + PTT)) {
                return (double) MemoryReader.ReadInt32(Ptr + PTT) / 100.0;
            } else return 0.0f;
        }
    }

    public static String Test() {
        MemoryMap a = Maps.GetCurrentModuleWithMatch("libcocos2dcpp");

        byte[] data = NativeMethod.ReadMemory(a.StartAddress, 2048);
        String out = "";
        for (int i = 0; i < data.length; i++) {
            out += "0x" + Integer.toHexString(data[i]) + ",";
        }
        return out;

    }
}
