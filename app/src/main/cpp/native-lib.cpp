#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>

using namespace std;

extern "C"
JNIEXPORT jint JNICALL
Java_xyz_cnyg_memoryreader_NativeMethod_StringIndexOf(JNIEnv *env, jclass clazz,
                                                                jbyteArray jbr_string,
                                                                jbyteArray jbr_search) {
    // TODO: 完成实际的排序算法
    // 找不到返回 -1
    // 找到就返回基于块的位置
    // 内置 BM 算法,效率比 Java 快
    int targetStrLen = env->GetArrayLength(jbr_string);
    jbyte* jtarget = env->GetByteArrayElements(jbr_string,JNI_FALSE);
    char target[targetStrLen];
    memcpy(target,jtarget,targetStrLen);
    env->ReleaseByteArrayElements(jbr_string,jtarget,0);

    int searchStrLen = env->GetArrayLength(jbr_search);
    jbyte *jsearch = env->GetByteArrayElements(jbr_search,JNI_FALSE);
    char search[searchStrLen];
    memcpy(search,jsearch,searchStrLen);
    env->ReleaseByteArrayElements(jbr_search,jsearch,0);

    //算法实现
    string st = string(target,targetStrLen);
    size_t pos = st.find(string(search,searchStrLen));

    if(pos == string::npos) return (jint)-1;
    return (jint)pos;
}
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_xyz_cnyg_memoryreader_NativeMethod_ReadMemory(JNIEnv *env, jclass clazz,
                                                             jlong address, jint size) {
    char buffer[size];
    jbyteArray ret = env->NewByteArray(size);
    //__android_log_print(ANDROID_LOG_DEBUG,"NATIVEOUT","Reading Address:%x",address);
    memcpy(buffer,(char*)address,size);

    env->SetByteArrayRegion(ret,0,size,(jbyte*)buffer);
    return ret;
}