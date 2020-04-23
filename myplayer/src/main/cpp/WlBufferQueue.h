//
// Created by Administrator on 2020/4/24.
//

#ifndef MYMUSIC_WLBUFFERQUEUE_H
#define MYMUSIC_WLBUFFERQUEUE_H

#include "deque"
#include "WlPlaystatus.h"
#include "WlPcmBean.h"

extern "C"
{
#include <libavcodec/avcodec.h>
#include "pthread.h"
};

class WlBufferQueue {

public:
    std::deque<WlPcmBean *> queueBuffer;
    pthread_mutex_t mutexBuffer;
    pthread_cond_t condBuffer;
    WlPlaystatus *wlPlayStatus = NULL;

public:
    WlBufferQueue(WlPlaystatus *playStatus);
    ~WlBufferQueue();
    int putBuffer(SAMPLETYPE *buffer, int size);
    int getBuffer(WlPcmBean **pcmBean);
    int clearBuffer();

    void release();
    int getBufferSize();

    int noticeThread();
};


#endif //MYMUSIC_WLBUFFERQUEUE_H
