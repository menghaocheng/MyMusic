//
// Created by Administrator on 2020/4/25.
//

#include "WlVideo.h"

WlVideo::WlVideo(WlPlaystatus *playstatus, WlCallJava *wlCallJava) {

    this->playstatus = playstatus;
    this->wlCallJava = wlCallJava;
    queue = new WlQueue(playstatus);
}


void * playVideo(void *data){
    WlVideo *video = static_cast<WlVideo *>(data);

    while(video->playstatus != NULL && !video->playstatus->exit){
        AVPacket *avPacket = av_packet_alloc();
        if(video->queue->getAvpacket(avPacket) == 0){
            //解码渲染
            LOGE("线程中获取视频AVpacket");
        }
        av_packet_free(&avPacket);
        av_free(avPacket);
        avPacket = NULL;

    }

    pthread_exit(&video->thread_play);
}

void WlVideo::play() {

    pthread_create(&thread_play, NULL, playVideo, this);

}


