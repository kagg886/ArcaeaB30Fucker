package com.kagg886.fuck_arc_b30.util;

/**
 * @author kagg886
 * @date 2023/8/18 10:52
 **/
public class ArcaeaNativeTool {
    static {
        System.loadLibrary("nativeapp");
    }
    public static native double nativeGetPtt();
}
