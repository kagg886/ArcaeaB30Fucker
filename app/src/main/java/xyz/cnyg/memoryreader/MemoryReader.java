package xyz.cnyg.memoryreader.procfs;

import android.util.Log;
import android.view.MotionEvent;

import com.kagg886.fuck_arc_b30.util.NativeMethod;
import com.kagg886.fuck_arc_b30.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProcfsReader {
    public static final String ProcFsPath = "/proc/"+String.valueOf(android.os.Process.myPid());

    public static class Mem{
        public static byte ReadByte(long Address){
            byte[] data = ReadBytes(Address,1);
            if(data == null) return 0;
            return data[0];

        }
        public static short ReadInt16(long Address){
            byte[] data = ReadBytes(Address,2);
            if(data == null) return 0;
            return ByteBuffer.wrap(data).getShort();
        }
        public static int ReadInt32(long Address){
            byte[] data = ReadBytes(Address,4);
            if(data == null) return 0;
            return ByteBuffer.wrap(data).getInt();
        }
        public static long ReadInt64(long Address){
            byte[] data = ReadBytes(Address,8);
            if(data == null) return 0;
            return ByteBuffer.wrap(data).getLong();
        }
        public static float ReadFloat(long Address){
            byte[] data = ReadBytes(Address,4);
            if(data == null) return 0.0f;
            return ByteBuffer.wrap(data).getFloat();
        }
        public static double ReadDouble(long Address){
            byte[] data = ReadBytes(Address,8);
            if(data == null) return 0.0f;
            return ByteBuffer.wrap(data).getDouble();
        }
        public static byte[] ReadBytes(long Address,int size){
            String memPath = ProcFsPath + "/mem";
            File memEntry = new File(memPath);
            memEntry.setReadOnly();
            try{
                return NativeMethod.ReadMemory(Address,size);
            }
            catch(Exception ex){
                Utils.runAsync(()->{ Log.e("Read","Reading mem is thrown" + ex.getMessage()); });
                return null;
            }
            //return null;
        }
        public static long GetByteDataStartAddress(long StartPos,byte[] search){
            return GetByteDataStartAddress(StartPos,search,Maps.StackEnd());
        }
        // TODO: 处理野指针问题
        // 还是根据 Maps 方案，找到所有可以进行读的内存块，然后避免读取不可访问的指针位置即可
        // 需要解析一下 maps 文件以知道哪片区域可读
        public static long GetByteDataStartAddress(long StartPos,byte[] search,long EndPos){
            String TAG = "GetHitCodeBase";
            int bufferSize = 1024;
            int searchTargetLen = search.length;
            int outputSet = 0x0;
            for(long pointer=StartPos;pointer<EndPos;pointer+=bufferSize){
                long outputDep = pointer / 0x100000;
                if(outputSet != (int)outputDep){
                    Log.i("ADDRSE","Search Stage="+Long.toHexString(outputDep));
                    outputSet = (int)outputDep;
                }
                byte[] Read = ReadBytes(StartPos,bufferSize);
                int searchPos = NativeMethod.StringIndexOf(Read,search);
                if(searchPos > -1) Log.i("ADDRSE","FindMatchPos:"+Integer.toHexString(searchPos));
                if(searchPos < 0){
                    if(pointer - searchTargetLen < StartPos){
                        continue; //开始就回去?不可能！
                    }
                    //截取块中断，防止刚好中断就找不到
                    byte[] MidCutBlocks = ReadBytes( pointer + bufferSize - searchTargetLen,searchTargetLen * 2);
                    int nextSearchPos = NativeMethod.StringIndexOf(MidCutBlocks,search);
                    if(nextSearchPos < 0){
                        continue; //截断块没有,继续!
                    }
                    return pointer + bufferSize - searchTargetLen + nextSearchPos;//找到了
                }
                else return pointer + searchPos; //在块内读到时直接返回

            }
            return 0x00;
        }
    }
}
