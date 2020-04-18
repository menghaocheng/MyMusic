//
// Created by Administrator on 2020/4/17.
//

#ifndef MYMUSIC_WLCALLJAVA_H
#define MYMUSIC_WLCALLJAVA_H


#include "jni.h"
#include <linux/stddef.h>
#include "AndroidLog.h"

#define MAIN_THREAD 0
#define CHILD_THREAD 1


class WlCallJava {

public:
    _JavaVM *javaVM = NULL;
    JNIEnv *jniEnv = NULL;
    jobject jobj;

    jmethodID jmid_parpared;
    jmethodID jmid_load;
    jmethodID jmid_timeinfo;

public:
    WlCallJava(_JavaVM *javaVM, JNIEnv *env, jobject *obj);
    ~WlCallJava();

    void onCallParpared(int type);

    void onCallLoad(int type, bool load);

    void onCallTimeInfo(int type, int curr, int total);

};


#endif //MYMUSIC_WLCALLJAVA_H