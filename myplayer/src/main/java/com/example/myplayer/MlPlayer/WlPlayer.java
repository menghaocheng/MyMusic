package com.example.myplayer.MlPlayer;

import android.text.TextUtils;

import com.example.myplayer.WlTimeInfoBean;
import com.example.myplayer.listener.WlOnCompleteListener;
import com.example.myplayer.listener.WlOnErrorListener;
import com.example.myplayer.listener.WlOnLoadListener;
import com.example.myplayer.listener.WlOnParparedListener;
import com.example.myplayer.listener.WlOnPauseResumeListener;
import com.example.myplayer.listener.WlOnTimeInfoListener;
import com.example.myplayer.log.MyLog;

public class WlPlayer {

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avformat-57");
        System.loadLibrary("swscale-4");
        System.loadLibrary("postproc-54");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avdevice-57");

    }

    private String source; //数据源
    private static WlTimeInfoBean wlTimeInfoBean;
    private WlOnParparedListener wlOnParparedListener;
    private WlOnLoadListener wlOnLoadListener;
    private WlOnPauseResumeListener wlOnPauseResumeListener;
    private WlOnTimeInfoListener wlOnTimeInfoListener;
    private WlOnErrorListener wlOnErrorListener;
    private WlOnCompleteListener wlOnCompleteListener;

    public WlPlayer(){}


    /**
     * 设置数据源
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 设置准备接口回调
     * @param wlOnParparedListener
     */
    public void setWlOnParparedListener(WlOnParparedListener wlOnParparedListener) {
        this.wlOnParparedListener = wlOnParparedListener;
    }

    public void setWlOnLoadListener(WlOnLoadListener wlOnLoadListener) {
        this.wlOnLoadListener = wlOnLoadListener;
    }

    public void setWlOnPauseResumeListener(WlOnPauseResumeListener wlOnPauseResumeListener) {
        this.wlOnPauseResumeListener = wlOnPauseResumeListener;
    }

    public void setWlOnTimeInfoListener(WlOnTimeInfoListener wlOnTimeInfoListener) {
        this.wlOnTimeInfoListener = wlOnTimeInfoListener;
    }

    public void setWlOnErrorListener(WlOnErrorListener wlOnErrorListener) {
        this.wlOnErrorListener = wlOnErrorListener;
    }

    public void setWlOnCompleteListener(WlOnCompleteListener wlOnCompleteListener) {
        this.wlOnCompleteListener = wlOnCompleteListener;
    }

    public void parpared(){
        if(TextUtils.isEmpty(source)){
            MyLog.d("source not be empty");
            return;
        }
        //onCallLoad(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_parpared(source);
            }
        }).start();
    }

    public void start()
    {
        if(TextUtils.isEmpty(source)){
            MyLog.d("source is empty");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_start();
            }
        }).start();
    }

    public void pause(){
        n_pause();
        if(wlOnPauseResumeListener != null){
            wlOnPauseResumeListener.onPause(true);
        }
    }

    public void resume(){
        n_resume();
        if(wlOnPauseResumeListener != null){
            wlOnPauseResumeListener.onPause(false);
        }
    }

    public void stop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_stop();
            }
        }).start();
    }

    public void seek(int secds){
        n_seek(secds);
    }
    /**
     * c++回调java的方法
     */
    public void onCallParpared(){
        if(wlOnParparedListener != null){
            wlOnParparedListener.onParpared();
        }
    }

    public void onCallLoad(boolean load){
        if(wlOnLoadListener != null){
            wlOnLoadListener.onLoad(load);
        }
    }

    public void onCallTimeInfo(int currentTime, int totalTime){
        if(wlOnTimeInfoListener != null){
            if(wlTimeInfoBean == null){
                wlTimeInfoBean = new WlTimeInfoBean();
            }
            wlTimeInfoBean.setCurrentTime(currentTime);
            wlTimeInfoBean.setTotalTime(totalTime);
            wlOnTimeInfoListener.onTimeInfo(wlTimeInfoBean);
        }
    }

    public void onCallError(int code, String msg){
        if(wlOnErrorListener != null){
            stop();
            wlOnErrorListener.onError(code, msg);
        }
    }

    public void onCallComplete(){
        if(wlOnCompleteListener != null){
            stop();
            wlOnCompleteListener.onComplete();
        }
    }

    private native void n_parpared(String source);
    private native void n_start();
    private native void n_pause();
    private native void n_resume();
    private native void n_stop();
    private native void n_seek(int secds);





}
