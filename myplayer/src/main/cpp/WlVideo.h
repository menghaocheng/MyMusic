//
// Created by Administrator on 2020/4/25.
//

#ifndef MYMUSIC_WLVIDEO_H
#define MYMUSIC_WLVIDEO_H

#include "WlQueue.h"
#include "WlCallJava.h"

extern "C"
{
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



public:
    WlVideo(WlPlaystatus *playstatus, WlCallJava *wlCallJava);
    ~WlVideo();

    void play();





};


#endif //MYMUSIC_WLVIDEO_H
