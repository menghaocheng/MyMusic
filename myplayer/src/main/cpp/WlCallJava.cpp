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
    jmid_timeinfo = env->GetMethodID(jlz, "onCallTimeInfo", "(II)V");
    jmid_error = env->GetMethodID(jlz, "onCallError", "(ILjava/lang/String;)V");
    jmid_complete = env->GetMethodID(jlz, "onCallComplete", "()V");
    jmid_valuedb = env->GetMethodID(jlz, "onCallValueDB", "(I)V");
    jmid_pcmtoaac = env->GetMethodID(jlz, "encodecPcmToAAc", "(I[B)V");
    jmid_pcminfo = env->GetMethodID(jlz, "onCallPcmInfo", "([BI)V");
    jmid_pcmrate = env->GetMethodID(jlz, "onCallPcmRate", "(I)V");

}

void WlCallJava::onCallParpared(int type) {
    if(type == MAIN_THREAD){
        jniEnv->CallVoidMethod(jobj, jmid_parpared);
    }else if(type == CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
            if(LOG_DEBUG){
                LOGE("get child thread jnienv wrong");
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
                LOGE("call onCallLoad wrong");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_load, load);
        javaVM->DetachCurrentThread();
    }
}

void WlCallJava::onCallTimeInfo(int type, int curr, int total) {
    if(type == MAIN_THREAD){
        jniEnv->CallVoidMethod(jobj, jmid_timeinfo, curr, total);
    }else if(type == CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
            if(LOG_DEBUG){
                LOGE("call onCallTimeInfo wrong");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_timeinfo, curr, total);
        javaVM->DetachCurrentThread();
    }
}

WlCallJava::~WlCallJava() {

}

void WlCallJava::onCallError(int type, int code, char *msg) {
    if(type == MAIN_THREAD){
        jstring jmsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jobj, jmid_error, code, jmsg);
        jniEnv->DeleteLocalRef(jmsg);
    }else if(type == CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
            if(LOG_DEBUG){
                LOGE("call onCallError wrong");
            }
            return;
        }
        jstring jmsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jobj, jmid_error, code, jmsg);
        jniEnv->DeleteLocalRef(jmsg);
        javaVM->DetachCurrentThread();
    }
}

void WlCallJava::onCallComplete(int type) {
    if(type == MAIN_THREAD){
        jniEnv->CallVoidMethod(jobj, jmid_complete);
    }else if(type == CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
            if(LOG_DEBUG){
                LOGE("call onCallComplete wrong");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_complete);
        javaVM->DetachCurrentThread();
    }
}

void WlCallJava::onCallValueDB(int type, int db) {
    if(type == MAIN_THREAD){
        jniEnv->CallVoidMethod(jobj, jmid_valuedb, db);
    }else if(type == CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
            if(LOG_DEBUG){
                LOGE("call onCallValueDB wrong");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_valuedb, db);
        javaVM->DetachCurrentThread();
    }
}

void WlCallJava::onCallPcmToAAc(int type, int size, void *buffer) {
    if(type == MAIN_THREAD){
        jbyteArray jbuffer = jniEnv->NewByteArray(size);
        jniEnv->SetByteArrayRegion(jbuffer, 0, size, static_cast<const jbyte *>(buffer));

        jniEnv->CallVoidMethod(jobj, jmid_pcmtoaac, size, jbuffer);

        jniEnv->DeleteLocalRef(jbuffer);
    }else if(type == CHILD_THREAD){
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
            if(LOG_DEBUG){
                LOGE("call onCallPcmToAAc wrong");
            }
            return;
        }
        jbyteArray jbuffer = jniEnv->NewByteArray(size);
        jniEnv->SetByteArrayRegion(jbuffer, 0, size, static_cast<const jbyte *>(buffer));

        jniEnv->CallVoidMethod(jobj, jmid_pcmtoaac, size, jbuffer);

        jniEnv->DeleteLocalRef(jbuffer);

        javaVM->DetachCurrentThread();
    }
}

void WlCallJava::onCallPcmInfo(void *buffer, int size) {
    JNIEnv *jniEnv;
    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
        if(LOG_DEBUG){
            LOGE("call onCallPcmInfo wrong");
        }
        return;
    }
    jbyteArray jbuffer = jniEnv->NewByteArray(size);
    jniEnv->SetByteArrayRegion(jbuffer, 0, size, static_cast<const jbyte *>(buffer));

    jniEnv->CallVoidMethod(jobj, jmid_pcminfo, jbuffer, size);

    jniEnv->DeleteLocalRef(jbuffer);

    javaVM->DetachCurrentThread();
}

void WlCallJava::onCallPcmRate(int samplerate) {
    JNIEnv *jniEnv;
    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
        if(LOG_DEBUG){
            LOGE("call onCallPcmRate wrong");
        }
        return;
    }

    jniEnv->CallVoidMethod(jobj, jmid_pcmrate, samplerate);

    javaVM->DetachCurrentThread();
}
