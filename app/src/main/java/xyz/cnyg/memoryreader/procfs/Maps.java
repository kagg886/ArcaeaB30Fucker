package xyz.cnyg.memoryreader.procfs;

import android.util.Log;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Maps{
    public static String ProcFsMapPath = Path.FsPath+"/maps";
    private static MemoryMap[] Cached;
    public static void UpdateMemoryMap(){
        Cached = getMemoryMap();
    }
    private static MemoryMap[] getMemoryMap(){
        try {
            var lines = Files.readAllLines(Paths.get(ProcFsMapPath));
            String[] input = new String[lines.size()];
            lines.toArray(input);
            MemoryMap[] a = MemoryMap.ParseContent(input);
            return a;
        }
        catch(Exception ex){
            Log.i("ReadErr","Thrown ex when reading /proc/self/maps file:"+ex.getMessage());
            return null;
        }
    }
    public static MemoryMap[] GetMemoryMap(){
        if(Cached == null) UpdateMemoryMap();
        return Cached;
    }

    public static MemoryMap GetCurrentModuleWithMatch(String ModuleName){
        MemoryMap[] maps = GetMemoryMap();
        for(int i=0;i<maps.length;i++){
            MemoryMap selected = maps[i];
            if(selected.Module.contains(ModuleName)) return selected;
        }
        return null;
    }
    public static MemoryMap GetCurrentMMapWithAddress(long Address){
        MemoryMap[] maps = GetMemoryMap();
        for(int i=0;i<maps.length;i++){
            MemoryMap selected = maps[i];
            if(selected.AddressInRange(Address)) return selected;
        }
        return null;
    }
    public static Boolean AddressReadable(long Address){
        MemoryMap[] maps = GetMemoryMap();
        for(int i=0;i<maps.length;i++) {
            MemoryMap current = maps[i];
            if (current.Permission.Read) {
                if(current.AddressInRange(Address)) return true;
            }
        }
        return false;
    }
    //返回地址范围的可读字节
    // < 1 = 完全不可读
    // > 0 = 从 AddressStart 正数的可读字节数
    // AddressEnd 必须大于 AddressStart
    public static long AddressReadableSize(long AddressStart, long AddressEnd){
        long RangeSize = AddressEnd - AddressStart;
        MemoryMap startPtrMap = GetCurrentMMapWithAddress(AddressStart);
        MemoryMap endPtrMap = GetCurrentMMapWithAddress(AddressEnd);

        if(startPtrMap != null && startPtrMap.Permission.Read == true){
            if(endPtrMap != null && endPtrMap.Permission.Read == true){ //两个模块之间的内存确认可读
                if(startPtrMap.StartAddress == endPtrMap.StartAddress){ //其实是一个模块
                    return RangeSize;
                }
                else if(startPtrMap.EndAddress == endPtrMap.StartAddress){ //两个模块是黏在一起的
                    return RangeSize;
                }
                else{ //不是黏在一起的
                    return startPtrMap.EndAddress - AddressStart;
                }
            }
            else{ //尾末地址不可读时
                return startPtrMap.EndAddress - AddressStart;
            }
        }
        else{ //起始地址始终不可读时
            return 0;
        }

    }
    public static Boolean AddressWriteable(long Address){
        MemoryMap[] maps = GetMemoryMap();
        for(int i=0;i<maps.length;i++) {
            MemoryMap current = maps[i];
            if (current.Permission.Write) {
                if(current.AddressInRange(Address)) return true;
            }
        }
        return false;
    }
    public static Boolean AddressExecutable(long Address){
        MemoryMap[] maps = GetMemoryMap();
        for(int i=0;i<maps.length;i++) {
            MemoryMap current = maps[i];
            if (current.Permission.Execute) {
                if(current.AddressInRange(Address)) return true;
            }
        }
        return false;
    }
    public static long GetStackEndAddress(){
        return GetCurrentModuleWithMatch("[stack]").EndAddress;
    }
}
