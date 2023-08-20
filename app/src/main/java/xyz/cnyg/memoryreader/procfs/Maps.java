package xyz.cnyg.memoryreader.maps;

import android.util.Log;

import java.nio.file.Files;
import java.nio.file.Paths;

public static class Maps{
    public static long StackEnd(){
        return ReadModuleEndAddress("[stack]");
    }
    public static long ReadModuleStartAddress(String module){
        String Readed = ReadModuleAddressRange(module);
        if(Readed == null) return 0x00;
        return Long.parseLong(Readed.split("-")[0],16);
    }
    public static long ReadModuleEndAddress(String module){
        String Readed = ReadModuleAddressRange(module);
        if(Readed == null) return 0x00;
        return Long.parseLong(Readed.split("-")[1],16);
    }
    public static String ReadModuleAddressRange(String module){
        byte[] filebyte;
        try {
            filebyte = Files.readAllBytes(Paths.get(ProcFsPath + "/maps"));
        }
        catch(Exception e){
            Log.i("ProcfsIO","Just Thrown ioerror");
            return null;
        }
        String mapFileContent = new String(filebyte);
        String[] rols = mapFileContent.split("\n");
        for(int i=0;i<rols.length;i++){
            if(rols[i].contains(module)){
                return rols[i].split(" ")[0];
            }
        }
        return null;
    }
}
