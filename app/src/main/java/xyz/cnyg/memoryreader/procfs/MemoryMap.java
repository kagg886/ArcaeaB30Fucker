package xyz.cnyg.memoryreader.procfs;

import java.util.ArrayList;
import java.util.List;

public class MemoryMap {
    public long StartAddress;
    public long EndAddress;

    public _Permission Permission = new _Permission();
    public class _Permission{
        public Boolean Read;
        public Boolean Write;
        public Boolean Execute;
        public Boolean Share;
    }
    public long Offset;
    public _Device Device = new _Device();
    public class _Device{
        public int Master;
        public int Sub;
    }
    public long inode;
    public String Module;
    public static MemoryMap[] ParseContent(String MapFileContent){
        String[] EachMemRange = MapFileContent.split("\n");
        return ParseContent(EachMemRange);
    }
    public static MemoryMap[] ParseContent(String[] MapFileContent){
        String[] EachMemRange = MapFileContent;
        List<MemoryMap> mlist = new ArrayList<MemoryMap>();

        for(int i=0;i<EachMemRange.length;i++){
            if(EachMemRange[i].length() < 6) continue;;
            String target = EachMemRange[i];
            String[] current = target.split(" ");
            MemoryMap m = new MemoryMap();

            String[] MemRange = current[0].split("-");
            m.StartAddress = Long.parseLong(MemRange[0],16);
            m.EndAddress = Long.parseLong(MemRange[1],16);

            String permissionstr = current[1];
            m.Permission.Read = permissionstr.contains("r");
            m.Permission.Write = permissionstr.contains("w");
            m.Permission.Execute = permissionstr.contains("x");
            m.Permission.Share = permissionstr.contains("s");

            m.Offset = Long.parseLong(current[2],16);

            String[] devicestr = current[3].split(":");
            m.Device.Master = Integer.parseInt(devicestr[0],16);
            m.Device.Sub = Integer.parseInt(devicestr[1],16);

            m.inode = Long.parseLong(current[4],16);

            int pathIndex = target.indexOf("/");
            int sysIndex = target.indexOf("[");

            m.Module = sysIndex < 0 ? (pathIndex < 0 ? "UNKNOWN_MODULE" : target.substring(pathIndex)) : target.substring(sysIndex);

            long sameCount = mlist.stream().filter((x)->{
                return x.Module.contains(m.Module);
            }).count();

            m.Module = sameCount > 0 ? (m.Module + "." + sameCount) : m.Module;

            mlist.add(m);
        }

        MemoryMap[] a = new MemoryMap[mlist.size()];

        mlist.toArray(a);

        return a;
    }
    public Boolean AddressInRange(long Address){
        return (Address >= StartAddress && Address < EndAddress);
    }
}
