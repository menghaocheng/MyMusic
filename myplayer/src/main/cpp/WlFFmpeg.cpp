//
// Created by Administrator on 2020/4/17.
//

#include "WlFFmpeg.h"

WlFFmpeg::WlFFmpeg(WlPlaystatus *playstatus, WlCallJava *callJava, const char *url) {
    this->playstatus = playstatus;
    this->callJava = callJava;
    this->url = url;
}

WlFFmpeg::~WlFFmpeg() {

}

void *decodeFFmpeg(void *data){
    WlFFmpeg *wlFFmpeg = (WlFFmpeg *) data;
    wlFFmpeg->decodeFFmpegThread();
    pthread_exit(&wlFFmpeg->decodeThread);
}


void WlFFmpeg::parpared() {
    pthread_create(&decodeThread, NULL, decodeFFmpeg, this);
}


void WlFFmpeg::decodeFFmpegThread() {
    av_register_all();
    avformat_network_init();
    pFormatCtx = avformat_alloc_context();
    if(avformat_open_input(&pFormatCtx, url, NULL, NULL) != 0){
        if(LOG_DEBUG){
            LOGE("open url [%s] failed: %s", url, strerror(errno));
        }
        return;
    }
    if(avformat_find_stream_info(pFormatCtx, NULL) < 0){
        if(LOG_DEBUG){
            LOGE("can not find streams from %s", url);
        }
        return;
    }
    for(int i = 0; i < pFormatCtx->nb_streams; i++){
        if(pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO){//得到音频流
            if(audio == NULL){
                audio = new WlAudio(playstatus, pFormatCtx->streams[i]->codecpar->sample_rate);
                audio->streamIndex = i;
                audio->codecpar = pFormatCtx->streams[i]->codecpar;
            }
        }
    }
    AVCodec *dec = avcodec_find_decoder(audio->codecpar->codec_id);
    if(!dec){
        if(LOG_DEBUG){
            LOGE("can not find decoder");
        }
        return;
    }

    audio->avCodecContext = avcodec_alloc_context3(dec);
    if(!audio->avCodecContext){
        if(LOG_DEBUG){
            LOGE("can not alloc new decodectx");
        }
        return;
    }

    if(avcodec_parameters_to_context(audio->avCodecContext, audio->codecpar) < 0){
        if(LOG_DEBUG){
            LOGE("can not fil decodectx");
        }
        return;
    }

    if(avcodec_open2(audio->avCodecContext, dec, 0) != 0){
        if(LOG_DEBUG){
            LOGE("can not open audio streams");
        }
        return;
    }
    callJava->onCallParpared(CHILD_THREAD);
}


void WlFFmpeg::start() {

    if (audio == NULL) {
        if (LOG_DEBUG) {
            LOGE("audio is null");
        }
        return;
    }
    audio->play();

    int count = 0;

    while (playstatus != NULL && !playstatus->exit) {
        AVPacket *avPacket = av_packet_alloc();
        if (av_read_frame(pFormatCtx, avPacket) == 0) {
            if (avPacket->stream_index == audio->streamIndex) {
                //解码操作
                count++;
                if (LOG_DEBUG) {
                    LOGE("解码第 %d 帧", count);
                }
                audio->queue->putAvpacket(avPacket);
            } else {
                av_packet_free(&avPacket);
                av_free(avPacket);
            }
        } else {
            av_packet_free(&avPacket);
            av_free(avPacket);
            while(playstatus != NULL && !playstatus->exit){
                if(audio->queue->getQueueSize() > 0){
                    continue;
                } else {
                    playstatus->exit = true;
                    break;
                }
            }

        }
    }

    if (LOG_DEBUG) {
        LOGD("解码完成");
    }

}
