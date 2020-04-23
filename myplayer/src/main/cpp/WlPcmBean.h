//
// Created by Administrator on 2020/4/24.
//

#ifndef MYMUSIC_WLPCMBEAN_H
#define MYMUSIC_WLPCMBEAN_H

#include <SoundTouch.h>

using namespace soundtouch;

class WlPcmBean {

public:
    char *buffer;
    int buffsize;

public:
    WlPcmBean(SAMPLETYPE *buffer, int size);
    ~WlPcmBean();


};


#endif //MYMUSIC_WLPCMBEAN_H
