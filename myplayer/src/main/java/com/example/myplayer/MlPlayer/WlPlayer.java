package com.example.myplayer.MlPlayer;

import android.text.TextUtils;

import com.example.myplayer.listener.WlOnParparedListener;
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
    private WlOnParparedListener wlOnParparedListener;

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

    public void parpared(){
        if(TextUtils.isEmpty(source)){
            MyLog.d("source not be empty");
            return;
        }
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

    /**
     * c++回调java的方法
     */
    public void onCallParpared(){
        if(wlOnParparedListener != null){
            wlOnParparedListener.onParpared();
        }
    }

    public native void n_parpared(String source);
    public native void n_start();
}
