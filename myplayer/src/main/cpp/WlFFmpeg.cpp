//
// Created by Administrator on 2020/4/17.
//


#include "WlFFmpeg.h"

WlFFmpeg::WlFFmpeg(WlPlaystatus *playstatus, WlCallJava *callJava, const char *url) {
    this->playstatus = playstatus;
    this->callJava = callJava;
    this->url = url;
    exit = false;
	pthread_mutex_init(&init_mutex, NULL);
	pthread_mutex_init(&seek_mutex, NULL);
}

void *decodeFFmpeg(void *data){
    WlFFmpeg *wlFFmpeg = (WlFFmpeg *) data;
    wlFFmpeg->decodeFFmpegThread();
    pthread_exit(&wlFFmpeg->decodeThread);
}


void WlFFmpeg::parpared() {
    pthread_create(&decodeThread, NULL, decodeFFmpeg, this);
}

int avformat_callback(void *ctx){
    WlFFmpeg *fFmpeg = (WlFFmpeg*)ctx;
    if(fFmpeg->playstatus == NULL || fFmpeg->playstatus->exit){
        return AVERROR_EOF;
    }
    return 0;
}

void WlFFmpeg::decodeFFmpegThread() {
    pthread_mutex_lock(&init_mutex);
    av_register_all();
    avformat_network_init();
    pFormatCtx = avformat_alloc_context();

    pFormatCtx->interrupt_callback.callback = avformat_callback;
    pFormatCtx->interrupt_callback.opaque = this;

    if(avformat_open_input(&pFormatCtx, url, NULL, NULL) != 0){
        if(LOG_DEBUG){
            LOGE("open url [%s] failed: %s", url, strerror(errno));
        }
        callJava->onCallError(CHILD_THREAD, 1001, "can not open url");
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return;
    }
    if(avformat_find_stream_info(pFormatCtx, NULL) < 0){
        if(LOG_DEBUG){
            LOGE("can not find streams from %s", url);
        }
        callJava->onCallError(CHILD_THREAD, 1002, "can not find streams from url");
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return;
    }
    for(int i = 0; i < pFormatCtx->nb_streams; i++){
        if(pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO){//得到音频流
            if(audio == NULL){
                audio = new WlAudio(playstatus, pFormatCtx->streams[i]->codecpar->sample_rate, callJava);
                audio->streamIndex = i;
                audio->codecpar = pFormatCtx->streams[i]->codecpar;
                audio->duration = pFormatCtx->duration / AV_TIME_BASE;
                audio->time_base = pFormatCtx->streams[i]->time_base;
                duration = audio->duration;
            }
        }
    }
    AVCodec *dec = avcodec_find_decoder(audio->codecpar->codec_id);
    if(!dec){
        if(LOG_DEBUG){
            LOGE("can not find decoder");
        }
        callJava->onCallError(CHILD_THREAD, 1003, "can not find decoder");
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return;
    }

    audio->avCodecContext = avcodec_alloc_context3(dec);
    if(!audio->avCodecContext){
        if(LOG_DEBUG){
            LOGE("can not alloc new decodectx");
        }
        callJava->onCallError(CHILD_THREAD, 1004, "can not alloc new decodectx");
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return;
    }

    if(avcodec_parameters_to_context(audio->avCodecContext, audio->codecpar) < 0){
        if(LOG_DEBUG){
            LOGE("can not fill decodecctx");
        }
        callJava->onCallError(CHILD_THREAD, 1005, "can not fill decodecct");
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return;
    }

    if(avcodec_open2(audio->avCodecContext, dec, 0) != 0){
        if(LOG_DEBUG){
            LOGE("can not open audio streams");
        }
        callJava->onCallError(CHILD_THREAD, 1006, "can not open audio streams");
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return;
    }
    if(callJava != NULL){
        if(playstatus != NULL && !playstatus->exit){
            callJava->onCallParpared(CHILD_THREAD);
        } else {
            exit = true;
        }
    }
    pthread_mutex_unlock(&init_mutex);
}


void WlFFmpeg::start() {

    if(audio == NULL)
    {
        return;
    }
    audio->play();


    while (playstatus != NULL && !playstatus->exit) {
        if(playstatus->seek){
            continue;
        }

        if(audio->queue->getQueueSize() > 40){
            continue;
        }

        AVPacket *avPacket = av_packet_alloc();
        if (av_read_frame(pFormatCtx, avPacket) == 0) {
            if (avPacket->stream_index == audio->streamIndex) {
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
			break;
        }
    }
    if(callJava != NULL){
        callJava->onCallComplete(CHILD_THREAD);
    }
    exit = true;

}

void WlFFmpeg::pause() {
    if(audio != NULL){
        audio->pause();
    }
}

void WlFFmpeg::resume() {
    if(audio != NULL){
        audio->resume();
    }
}

void WlFFmpeg::release() {
    if(LOG_DEBUG){
        LOGE("开始释放Ffmpeg")
    }
    
    playstatus->exit = true;

    pthread_mutex_lock(&init_mutex);
    int sleepCount = 0;
    while(!exit){
        if(sleepCount > 10){
            exit = true;
        }
        if(LOG_DEBUG){
            LOGE("wait ffmpeg exit %d", sleepCount);
        }
        sleepCount ++;
        av_usleep(1000 * 1000); //暂停10毫秒
    }
    if(LOG_DEBUG){
        LOGE("释放Audio");
    }

    if(audio != NULL){
        audio->release();
        delete(audio);
        audio = NULL;
    }

    if(LOG_DEBUG){
        LOGE("释放封装格式上下文");
    }
    if(pFormatCtx != NULL){
        avformat_close_input(&pFormatCtx);
        avformat_free_context(pFormatCtx);
        pFormatCtx = NULL;
    }
    if(LOG_DEBUG){
        LOGE("释放callJava");
    }
    if(callJava != NULL){
        callJava = NULL;
    }
    if(LOG_DEBUG){
        LOGE("释放playstatus");
    }
    if(playstatus != NULL){
        playstatus = NULL;
    }
    pthread_mutex_unlock(&init_mutex);
}

WlFFmpeg::~WlFFmpeg() {

    pthread_mutex_destroy(&init_mutex);
}

void WlFFmpeg::seek(int64_t secds) {

    if(duration <= 0){
        return;
    }
    if(secds >= 0 && secds <= duration){
        if(audio != NULL){
            playstatus->seek = true;
            audio->queue->clearAvpacket();
            audio->clock = 0;
            audio->last_time = 0;
            pthread_mutex_lock(&seek_mutex);
            int64_t rel = secds * AV_TIME_BASE;
            avformat_seek_file(pFormatCtx, -1, INT64_MIN, rel, INT64_MAX, 0);
            pthread_mutex_unlock(&seek_mutex);
            playstatus->seek = false;
        }
    }
}

void WlFFmpeg::setVolume(int percent) {
    if(audio != NULL){
        audio->setVolume(percent);
    }
}
