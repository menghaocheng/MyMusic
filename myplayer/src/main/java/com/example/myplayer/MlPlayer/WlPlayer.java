package com.example.myplayer.MlPlayer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.text.TextUtils;

import com.example.myplayer.WlTimeInfoBean;
import com.example.myplayer.listener.WlOnCompleteListener;
import com.example.myplayer.listener.WlOnErrorListener;
import com.example.myplayer.listener.WlOnLoadListener;
import com.example.myplayer.listener.WlOnParparedListener;
import com.example.myplayer.listener.WlOnPauseResumeListener;
import com.example.myplayer.listener.WlOnPcmInfoListener;
import com.example.myplayer.listener.WlOnRecordTimeListener;
import com.example.myplayer.listener.WlOnTimeInfoListener;
import com.example.myplayer.listener.WlOnValumeDBListener;
import com.example.myplayer.log.MyLog;
import com.example.myplayer.muteenum.MuteEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class WlPlayer {

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("avcodec-58");
        System.loadLibrary("avdevice-58");
        System.loadLibrary("avfilter-7");
        System.loadLibrary("avformat-58");
        System.loadLibrary("avutil-56");
        System.loadLibrary("postproc-55");
        System.loadLibrary("swresample-3");
        System.loadLibrary("swscale-5");
    }

    private static String source;//数据源
    private static WlTimeInfoBean wlTimeInfoBean;
    private static boolean playNext = false;
    private static int duration = -1;
    private static int volumePercent = 100;
    private static float speed = 1.0f;
    private static float pitch = 1.0f;
    private static boolean initmediacodec = false;
    private static Object mediacodecLock = new Object();
    private static MuteEnum muteEnum = MuteEnum.MUTE_CENTER;
    private WlOnParparedListener wlOnParparedListener;
    private WlOnLoadListener wlOnLoadListener;
    private WlOnPauseResumeListener wlOnPauseResumeListener;
    private WlOnTimeInfoListener wlOnTimeInfoListener;
    private WlOnErrorListener wlOnErrorListener;
    private WlOnCompleteListener wlOnCompleteListener;
    private WlOnValumeDBListener wlOnValumeDBListener;
    private WlOnRecordTimeListener wlOnRecordTimeListener;
    private WlOnPcmInfoListener wlOnPcmInfoListener;

    public WlPlayer(){
	}

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

    public void setWlOnValumeDBListener(WlOnValumeDBListener wlOnValumeDBListener) {
        this.wlOnValumeDBListener = wlOnValumeDBListener;
    }

    public void setWlOnRecordTimeListener(WlOnRecordTimeListener wlOnRecordTimeListener) {
        this.wlOnRecordTimeListener = wlOnRecordTimeListener;
    }

    public void setWlOnPcmInfoListener(WlOnPcmInfoListener wlOnPcmInfoListener) {
        this.wlOnPcmInfoListener = wlOnPcmInfoListener;
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
                setVolume(volumePercent);
                setMute(muteEnum);
                setPitch(pitch);
                setSpeed(speed);
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
        wlTimeInfoBean = null;
        duration = -1;
        stopRecord();
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

    public void playNext(String url){
        source = url;
        playNext = true;
        stop();
    }

    public int getDuration() {
        if(duration < 0){
            duration = n_duration();
        }
        return duration;
    }

    public void setVolume(int percent) {
        if(percent >= 0 && percent <= 100){
            volumePercent = percent;
            n_volume(percent);
        }
    }

    public int getVolumePercent() {
        return volumePercent;
    }

    public void setMute(MuteEnum mute){
        muteEnum = mute;
        n_mute(mute.getValue());
    }

    public void setPitch(float p){
        pitch = p;
        n_pitch(pitch);
    }

    public void setSpeed(float s){
        speed = s;
        n_speed(speed);
    }

    public void startRecord(File outfile){
        synchronized (mediacodecLock) {
            if (!initmediacodec) {
                audioSamplerate = n_samplerate();
                if (audioSamplerate > 0) {
                    initmediacodec = true;
                    initMediacodec(audioSamplerate, outfile);
                    n_startstoprecord(true);
                    MyLog.e("开始录制");
                }
            }
        }
    }

    public void stopRecord(){
        synchronized (mediacodecLock) {
            if (initmediacodec) {
                n_startstoprecord(false);
                releaseMediacodec();
                MyLog.e("完成录制");
            }
        }
    }

    public void pauseRecord(){
        n_startstoprecord(false);
        MyLog.d("暂停录制");
    }

    public void resumeRecord(){
        n_startstoprecord(true);
        MyLog.d("继续录制");
    }

    public void cutAudioPlay(int start_time, int end_time, boolean showPcm){
        if(n_cutaudioplay(start_time, end_time, showPcm)){
            start();
        } else {
            stop();
            onCallError(2001, "cutaudio params is wrong");
        }
    }

    /**
     * c++回调java的方法
     */
    public void onCallParpared()
    {
        if(wlOnParparedListener != null)
        {
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
	    stop();
        if(wlOnCompleteListener != null){
            wlOnCompleteListener.onComplete();
        }
    }

    public void onCallNext(){
        if(playNext){
            playNext = false;
            parpared();
        }
    }

    public void onCallValueDB(int db){
        if(wlOnValumeDBListener != null){
            wlOnValumeDBListener.onDbValue(db);
        }
    }

    public void onCallPcmInfo(byte[] buffer, int buffersize){
        if(wlOnPcmInfoListener != null){
            wlOnPcmInfoListener.onPcmInfo(buffer, buffersize);
        }
    }

    public void onCallPcmRate(int samplerate){
        if(wlOnPcmInfoListener != null){
            wlOnPcmInfoListener.onPcmRate(samplerate, 16, 2);
        }
    }


    private native void n_parpared(String source);
    private native void n_start();
    private native void n_pause();
    private native void n_resume();
    private native void n_stop();
    private native void n_seek(int secds);
    private native int n_duration();
    private native void n_volume(int percent);
    private native void n_mute(int mute);
    private native void n_pitch(float pitch);
    private native void n_speed(float speed);
    private native int n_samplerate();
    private native void n_startstoprecord(boolean start);

    private native boolean n_cutaudioplay(int start_time, int end_time, boolean showPcm);


    //mediacodec

    private MediaFormat encoderFormat = null;
    private MediaCodec encoder = null;
    private FileOutputStream outputStream = null;
    private MediaCodec.BufferInfo info = null;
    private int perpcmsize = 0;
    private byte[] outByteBuffer = null;
    private int aacsamplerate = 4;
    private double recordTime = 0;
    private int audioSamplerate = 0;


    private void initMediacodec(int samperate, File outfile){
        try{
            aacsamplerate = getADTSsamplerate(samperate);
            encoderFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, samperate, 2);
            encoderFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            encoderFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            encoderFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096);
            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            info = new MediaCodec.BufferInfo();
            if(encoder == null){
                MyLog.d("create encoder wrong");
                return;
            }
            recordTime = 0;
            encoder.configure(encoderFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            outputStream = new FileOutputStream(outfile);
            encoder.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    private void encodecPcmToAAc(int size, byte[] buffer){
        synchronized (mediacodecLock) {
            if (initmediacodec == false) {
                return;
            }
            if (buffer != null && encoder != null) {
                recordTime += size * 1.0 / (audioSamplerate * 2 * (16 / 8));

                MyLog.d("recordTime = " + recordTime);
                if (wlOnRecordTimeListener != null) {
                    wlOnRecordTimeListener.onRecordTime((int) recordTime);
                }

                int inputBufferindex = encoder.dequeueInputBuffer(0);
                if (inputBufferindex >= 0) {
                    ByteBuffer byteBuffer = encoder.getInputBuffer(inputBufferindex);
                    byteBuffer.clear();
                    byteBuffer.put(buffer);
                    encoder.queueInputBuffer(inputBufferindex, 0, size, 0, 0);
                }

                int index = encoder.dequeueOutputBuffer(info, 0);
                while (index >= 0 && initmediacodec) {
                    try {
                        perpcmsize = info.size + 7;
                        outByteBuffer = new byte[perpcmsize];

                        ByteBuffer byteBuffer = encoder.getOutputBuffer(index);
                        byteBuffer.position(info.offset);
                        byteBuffer.limit(info.offset + info.size);

                        addADtsHeader(outByteBuffer, perpcmsize, aacsamplerate);

                        byteBuffer.get(outByteBuffer, 7, info.size);
                        byteBuffer.position(info.offset);
                        outputStream.write(outByteBuffer, 0, perpcmsize);

                        encoder.releaseOutputBuffer(index, false);
                        index = encoder.dequeueOutputBuffer(info, 0);
                        outByteBuffer = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void addADtsHeader(byte[] packet, int packetLen, int samplerate) {
        int profile = 2; //AAC LC
        int freqIdx = samplerate; // samplerate
        int chanCfg = 2; // CPE

        packet[0] = (byte) 0xFF; // 0xFFF(12bit) 这里只取了8位，所以还差4位放到下一个里面
        packet[1] = (byte) 0xF9; // 第一个t位放F
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    private int getADTSsamplerate(int samplerate) {
        int rate = 4;
        switch (samplerate){
            case 96000:
                rate = 0;
                break;
            case 88200:
                rate = 1;
                break;
            case 64000:
                rate = 2;
                break;
            case 48000:
                rate = 3;
                break;
            case 44100:
                rate = 4;
                break;
            case 32000:
                rate = 5;
                break;
            case 24000:
                rate = 6;
                break;
            case 22050:
                rate = 7;
                break;
            case 16000:
                rate = 8;
                break;
            case 12000:
                rate = 9;
                break;
            case 11025:
                rate = 10;
                break;
            case 8000:
                rate = 11;
                break;
            case 7350:
                rate = 12;
                break;
        }
        return rate;
    }

    private void releaseMediacodec(){
        if(encoder == null){
            return;
        }
        try {
            recordTime = 0;
            outputStream.close();
            outputStream = null;
            encoder.stop();
            encoder.release();
            encoder = null;
            encoderFormat = null;
            info = null;
            initmediacodec = false;

            MyLog.d("录制完成...");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null){
                try{
                    outputStream.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
                outputStream = null;
            }
        }
    }



}
