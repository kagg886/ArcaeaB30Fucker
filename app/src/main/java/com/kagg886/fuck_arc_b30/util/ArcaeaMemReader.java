package com.kagg886.fuck_arc_b30.util;

import android.util.Log;

import xyz.cnyg.memoryreader.MemoryReader;
import xyz.cnyg.memoryreader.NativeMethod;
import xyz.cnyg.memoryreader.procfs.Maps;
import xyz.cnyg.memoryreader.procfs.MemoryMap;

public class ArcaeaMemReader {
    public static Boolean IsGooglePlay = true;

    public static class Profile{
        private static long baseAddr = 0x0;
        private static class HitCode {
            public static int GPOffset = -0x20;
            public static byte[] GooglePlay = {
                    0x28,0x61,0x72,0x63,0x61,0x65,0x61,0x5F,
                    0x63,0x6C,0x65,0x61,0x72,0x5F,0x67,0x72,
                    0x61,0x64,0x65,0x5F,0x64,0x00,0x00,0x00
            };

            public static int ApkOffset = -0x2F8;
            public static byte[] Apk = {
                    0x28,0x61,0x72,0x63,0x61,0x65,0x61,0x5F,
                    0x66,0x72,0x61,0x67,0x6D,0x65,0x6E,0x74,
                    0x5F,0x33,0x30,0x30,0x30,0x00,0x00,0x00
            };

        }
        private static class MemOffset{
            public static int PrimaryPtr = 0x80;
            public static int SecondaryPtr = 0xC8;
            public static int PttOffset = 0x44;
        }

        public static double GetUserPtt() {
            long HitPointer = 0x0;
            Maps.UpdateMemoryMap();

            MemoryMap libcocosAddr = Maps.GetCurrentModuleWithMatch("libcocos2dcpp");
            Log.i("ADDRSE","libcocos2dcpp address="+Long.toHexString(libcocosAddr.StartAddress));
            if(Profile.baseAddr > 0x00 == false) { //将找到的地址做个缓存，可以加快请求
                if (IsGooglePlay)
                    HitPointer = MemoryReader.GetByteDataStartAddress(libcocosAddr.StartAddress, HitCode.GooglePlay) + HitCode.GPOffset;
                else
                    HitPointer = MemoryReader.GetByteDataStartAddress(libcocosAddr.StartAddress, HitCode.Apk) + HitCode.ApkOffset;
                Profile.baseAddr = HitPointer;
            }
            else HitPointer = Profile.baseAddr;

            Log.i("ADDRSE","Finish Search,HitPointer="+Long.toHexString(HitPointer));

            /*if(HitPointer > 0x0){
                long PPtr = MemoryReader.ReadInt64(HitPointer);
                Log.i("ADDRSE","TryReadPrimaryPtr:"+Long.toHexString(PPtr));
            }*/

            if (HitPointer > 0x0) {

                /*int ptt = MemoryReader.ReadInt32(
                        MemoryReader.ReadInt64(
                                MemoryReader.ReadInt64(
                                        MemoryReader.ReadInt64(
                                                HitPointer
                                        ) + MemOffset.PrimaryPtr
                                ) + MemOffset.SecondaryPtr
                        ) + MemOffset.PttOffset
                );*/

                long PrimaryPtr = MemoryReader.ReadInt64(HitPointer);
                if(PrimaryPtr > 0x00 == false) return 0.0f;
                long SecondaryPtr = MemoryReader.ReadInt64(PrimaryPtr + MemOffset.PrimaryPtr);
                if(SecondaryPtr > 0x00 == false) return 0.0f;
                long ProfilePtr = MemoryReader.ReadInt64(SecondaryPtr + MemOffset.SecondaryPtr);
                if(ProfilePtr > 0x00 == false) return 0.0f;
                int ptt = MemoryReader.ReadInt32(ProfilePtr + MemOffset.PttOffset);

                Log.i("READPTT","PTT="+Integer.toString(ptt));
                return (double)ptt / 100.0f;
            }
            else return 0.0f;
            //return 0.0f;
        }
    }
    public static String Test(){
        MemoryMap a = Maps.GetCurrentModuleWithMatch("libcocos2dcpp");

        byte[] data = NativeMethod.ReadMemory(a.StartAddress,2048);
        String out = "";
        for(int i=0;i<data.length;i++){
            out += "0x" +Integer.toHexString(data[i])+ ",";
        }
        return out;

    }
}
