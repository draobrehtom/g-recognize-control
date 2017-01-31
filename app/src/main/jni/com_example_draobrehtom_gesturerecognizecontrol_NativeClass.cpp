#include <com_example_draobrehtom_gesturerecognizecontrol_NativeClass.h>

JNIEXPORT jstring JNICALL Java_com_example_draobrehtom_gesturerecognizecontrol_NativeClass_getMessageFromJNI
  (JNIEnv *env, jclass obj) {
    return env->NewStringUTF("This message from JNI");
}
