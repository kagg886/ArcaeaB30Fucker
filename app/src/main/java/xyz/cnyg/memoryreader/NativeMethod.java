package xyz.cnyg.memoryreader;

public class NativeMethod {
    static {
        System.loadLibrary("nativeapp");
    }
    public static native int StringIndexOf(byte[] jbr_string,byte[] jbr_search);

    public static native byte[] ReadMemory(long address,int size);
}
