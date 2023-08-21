package xyz.cnyg.memoryreader;

import android.util.Log;

import com.kagg886.fuck_arc_b30.util.NativeMethod;
import com.kagg886.fuck_arc_b30.util.Utils;

import java.lang.annotation.Native;
import java.nio.ByteBuffer;

import kotlin.internal.IntrinsicConstEvaluation;
import xyz.cnyg.memoryreader.procfs.Maps;
import xyz.cnyg.memoryreader.procfs.MemoryMap;

public class MemoryReader {
    public static final String ProcFsPath = "/proc/"+String.valueOf(android.os.Process.myPid());

    public static byte ReadByte(long Address){
        byte[] data = ReadBytes(Address,1);
        if(data == null) return 0;
        return data[0];

    }
    public static short ReadInt16(long Address){
        byte[] data = ReadBytes(Address,2);
        if(data == null) return 0;
        return BitConverter.toShort(data);
    }
    public static int ReadInt32(long Address){
        byte[] data = ReadBytes(Address,4);
        if(data == null) return 0;
        return BitConverter.toInt(data);
    }
    public static long ReadInt64(long Address){
        byte[] data = ReadBytes(Address,8);
        if(data == null) return 0;
        return BitConverter.toLong(data);
    }
    public static float ReadFloat(long Address){
        byte[] data = ReadBytes(Address,4);
        if(data == null) return 0.0f;
        return BitConverter.toFloat(data);
    }
    public static double ReadDouble(long Address){
        byte[] data = ReadBytes(Address,8);
        if(data == null) return 0.0f;
        return BitConverter.toDouble(data);
    }
    public static byte[] ReadBytes(long Address,int size){
        return NativeMethod.ReadMemory(Address,size);
    }
    public static long GetByteDataStartAddress(long StartPos,byte[] search){
        return GetByteDataStartAddress(StartPos,search, Maps.GetStackEndAddress());
    }

    // 返回搜索数据的起始地址
    // 即使做了大量处理，但搜索时还是可能会发生崩溃
    public static long GetByteDataStartAddress(long StartPos,byte[] search,long EndPos){
        int buffSize = 2048;

        long outputDepends = 0x0;
        Boolean hitOutput = false;
        for(long ptr = StartPos;ptr < EndPos;ptr += buffSize){
            hitOutput = false;
            int StartAddressReadableBytes = (int)Maps.AddressReadableSize(ptr,ptr+buffSize);
            if(StartAddressReadableBytes < 1) continue; //如果区域不可读，直接跳过即可

            /*outputDepends = ptr/0x100000;
            if(outputDepends == ptr/0x100000){
                Log.d("ADDRSE","Aim normal to:"+Long.toHexString(ptr));
                Log.d("ADDRSE","Read len:"+ Integer.toString(StartAddressReadableBytes));
                hitOutput = true;
            }*/

            byte[] data = NativeMethod.ReadMemory(ptr,StartAddressReadableBytes);
            int Pos = NativeMethod.StringIndexOf(data,search);

            /*if(hitOutput){
                Log.d("ADDRSE","Finish data search in normal,Pos="+Integer.toString(Pos));
            }*/

            if(Pos < 0){//标准块找不到, 从截断块找
                long nextPos = ptr + StartAddressReadableBytes - search.length;
                long nextReadEndPos = nextPos + (search.length * 2);
                int nextReadSize = (int)Maps.AddressReadableSize(nextPos,nextReadEndPos);
                if(nextReadSize < 1) continue;

                /*if(hitOutput){
                    Log.d("ADDRSE","Aim midblock to:"+Long.toHexString(nextPos));
                    Log.d("ADDRSE","ReadLen:"+Integer.toString(nextReadSize));
                }*/

                byte[] nextData = NativeMethod.ReadMemory(nextPos,nextReadSize);
                int nextSPos = NativeMethod.StringIndexOf(nextData,search);

                /*if(hitOutput){
                    Log.d("ADDRSE","Finish data search in normal,Pos="+Integer.toString(nextSPos));
                }*/

                if(nextSPos < 0){ //还是找不到
                    continue;
                }
                else return nextPos + nextSPos; //截断块找到的
            }
            else return ptr + Pos; //直接找到的
        }
        return 0x0; //一直找不到的话
    }
}
