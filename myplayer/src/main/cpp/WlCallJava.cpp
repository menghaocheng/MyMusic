//
// Created by Administrator on 2020/4/17.
//

#include "WlCallJava.h"

WlCallJava::WlCallJava(_JavaVM *javaVM, JNIEnv *env, jobject *obj) {
    this->javaVM = javaVM;
    this->jniEnv = env;
    this->jobj = *obj;
    this->jobj = env->NewGlobalRef(jobj);

    jclass jlz = jniEnv->GetObjectClass(jobj);
    if(!jlz){
        if(LOG_DEBUG){
            LOGE("get jclass wrong");
        }
        return;
    }

    jmid_parpared = env->GetMethodID(jlz, "onCallParpared", "()V");
    jmid_load = env->GetMethodID(jlz, "onCallLoad", "(Z)V");
}

void WlCallJava::onCallParpared(int type) {
    if(type == MAIN_THREAD){
        jniEnv->CallVoidMethod(jobj, jmid_parpared);
    }else if(type == CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
            if(LOG_DEBUG){
                LOGE("get child thread jnienv wrong")
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_parpared);
        javaVM->DetachCurrentThread();
    }
}

void WlCallJava::onCallLoad(int type, bool load) {
    if(type == MAIN_THREAD){
        jniEnv->CallVoidMethod(jobj, jmid_load, load);
    }else if(type == CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
            if(LOG_DEBUG){
                LOGE("call onCallLoad wrong")
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_load, load);
        javaVM->DetachCurrentThread();
    }
}
