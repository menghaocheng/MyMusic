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
        if(video->playstatus->seek){
            av_usleep(1000 * 100);
            continue;
        }
        if(video->queue->getQueueSize() == 0){
            if(!video->playstatus->load){
                video->playstatus->load = true;
                video->wlCallJava->onCallLoad(CHILD_THREAD, true);
            }
            av_usleep(1000 * 100);
            continue;
        } else {
            if(video->playstatus->load){
                video->playstatus->load = false;
                video->wlCallJava->onCallLoad(CHILD_THREAD, false);
            }
        }
        AVPacket *avPacket = av_packet_alloc();
        if(video->queue->getAvpacket(avPacket) != 0){
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            continue;
        }
        if(avcodec_send_packet(video->avCodecContext, avPacket) != 0){
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            continue;
        }
        AVFrame *avFrame = av_frame_alloc();
        if(avcodec_receive_frame(video->avCodecContext, avFrame) != 0){
            av_frame_free(&avFrame);
            av_free(avFrame);
            avFrame = NULL;
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            continue;
        }
        LOGE("子线程解码一个AVframe成功");

        av_frame_free(&avFrame);
        av_free(avFrame);
        avFrame = NULL;
        av_packet_free(&avPacket);
        av_free(avPacket);
        avPacket = NULL;
    }
    pthread_exit(&video->thread_play);
}

void WlVideo::play() {

    pthread_create(&thread_play, NULL, playVideo, this);

}

void WlVideo::release() {
    if(queue != NULL){
        delete(queue);
        queue = NULL;
    }
    if(avCodecContext != NULL){
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);
        avCodecContext = NULL;
    }

    if(playstatus != NULL){
        playstatus = NULL;
    }
    if(wlCallJava != NULL){
        wlCallJava = NULL;
    }

}

WlVideo::~WlVideo() {

}


