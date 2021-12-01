#include <jni.h>

#include <string>

extern "C" JNIEXPORT jstring JNICALL

Java_com_parthdesai1208_jarassignment_Keys_apiKey1(JNIEnv* env, jobject /* this */) {

    std::string api_key = "IKFXGuXn67Fk_ybdVWCXjaKDQatFSd4qGPA9wtdmumQ";

    return env->NewStringUTF(api_key.c_str());

}