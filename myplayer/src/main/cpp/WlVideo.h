//
// Created by yangw on 2018-5-14.
//

#ifndef MYMUSIC_WLVIDEO_H
#define MYMUSIC_WLVIDEO_H


#include "WlQueue.h"
#include "WlCallJava.h"
#include "WlAudio.h"

#define CODEC_YUV 0
#define CODEC_MEDIACODEC 1

extern "C"
{
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
#include <libavutil/time.h>
#include <libavcodec/avcodec.h>
};

class WlVideo {

public:
    int streamIndex = -1;
    AVCodecContext *avCodecContext = NULL;
    AVCodecParameters *codecpar = NULL;
    WlQueue *queue = NULL;
    WlPlaystatus *playstatus = NULL;
    WlCallJava *wlCallJava = NULL;
    AVRational time_base;
    pthread_t thread_play;
    WlAudio *audio = NULL;
    double clock = 0;
    double delayTime = 0;
    double defaultDelayTime = 0.0166;
    pthread_mutex_t codecMutex;

    int codectype = CODEC_YUV;


public:
    WlVideo(WlPlaystatus *playstatus, WlCallJava *wlCallJava);
    ~WlVideo();

    void play();

    void release();

    double getFrameDiffTime(AVFrame *avFrame);

    double getDelayTime(double diff);




};


#endif //MYMUSIC_WLVIDEO_H
