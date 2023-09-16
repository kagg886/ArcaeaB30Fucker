package com.kagg886.fuck_arc_b30.util;

import xyz.cnyg.memoryreader.MemoryReader;
import xyz.cnyg.memoryreader.NativeMethod;
import xyz.cnyg.memoryreader.procfs.Maps;
import xyz.cnyg.memoryreader.procfs.MemoryMap;

public class ArcaeaMemReader {
    public static Boolean IsGooglePlay = true;

    public static class Profile {
        public static class MemOffsets_GP {
            public static String LocateValue = "libcocos2dcpp.so.1";
            public static long[] LocateMemOffset = {0xCBB38, 0x0, 0x80, 0xC8};
        }

        public static class MemOffsets_APK {
            public static String LocateValue = "libcocos2dcpp.so.1";
            public static long[] LocateMemOffset = {0xCBE20, 0x0, 0x98, 0x0, 0x0, 0x2E0, 0x4A8};
        }

        public static class ProfileStructOffsets {
            public static long PTT = 0x44;
        }

        public static double GetUserPtt() {
            String LocateVal = IsGooglePlay ? MemOffsets_GP.LocateValue : MemOffsets_APK.LocateValue;
            MemoryMap libBase = Maps.GetCurrentModuleWithMatch(LocateVal);
            long[] Offset = IsGooglePlay ? MemOffsets_GP.LocateMemOffset : MemOffsets_APK.LocateMemOffset;

            long Ptr = libBase.StartAddress;
            for (int i = 0; i < Offset.length; i++) {
                Ptr += Offset[i];
                if (Maps.AddressReadable(Ptr)) {
                    Ptr = MemoryReader.ReadInt64(Ptr);
                } else return 0.0f;
            }
            if (Maps.AddressReadable(Ptr + ProfileStructOffsets.PTT)) {
                return (double) MemoryReader.ReadInt32(Ptr + ProfileStructOffsets.PTT) / 100.0;
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
