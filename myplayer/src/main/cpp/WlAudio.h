//
// Created by Administrator on 2020/4/17.
//

#ifndef MYMUSIC_WLAUDIO_H
#define MYMUSIC_WLAUDIO_H


extern "C"{
#include "include/libavcodec/avcodec.h"
};

class WlAudio {

public:
    int streamIndex = -1;
    AVCodecContext *avCodecContext = NULL;
    AVCodecParameters *codecpar = NULL;

public:
    WlAudio();
    ~WlAudio();
};


#endif //MYMUSIC_WLAUDIO_H
