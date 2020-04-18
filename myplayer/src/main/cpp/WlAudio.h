//
// Created by Administrator on 2020/4/17.
//

#ifndef MYMUSIC_WLAUDIO_H
#define MYMUSIC_WLAUDIO_H


#include "WlQueue.h"
#include "WlPlaystatus.h"

extern "C"
{
#include "libavcodec/avcodec.h"
#include <libswresample/swresample.h>
};

class WlAudio {

public:
    int streamIndex = -1;
    AVCodecContext *avCodecContext = NULL;
    AVCodecParameters *codecpar = NULL;
    WlQueue *queue = NULL;
    WlPlaystatus *playstatus = NULL;

    pthread_t thread_play;
    AVPacket *avPacket = NULL;
    AVFrame *avFrame = NULL;
    int ret = 0;
    uint8_t *buffer = NULL;
    int data_size = 0;



public:
    WlAudio(WlPlaystatus *playstatus);
    ~WlAudio();

    void play();
    int resampleAudio();


};


#endif //MYMUSIC_WLAUDIO_H
