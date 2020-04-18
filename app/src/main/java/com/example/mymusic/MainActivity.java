package com.example.mymusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.myplayer.MlPlayer.WlPlayer;
import com.example.myplayer.WlTimeInfoBean;
import com.example.myplayer.listener.WlOnLoadListener;
import com.example.myplayer.listener.WlOnParparedListener;
import com.example.myplayer.listener.WlOnPauseResumeListener;
import com.example.myplayer.listener.WlOnTimeInfoListener;
import com.example.myplayer.log.MyLog;
import com.example.myplayer.util.WlTimeUtil;

public class MainActivity extends AppCompatActivity {

    private WlPlayer wlPlayer;
    private TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTime = findViewById(R.id.tv_time);
        wlPlayer = new WlPlayer();
        wlPlayer.setWlOnParparedListener(new WlOnParparedListener() {
            @Override
            public void onParpared() {
                MyLog.d("准备好了，可以开始播放声音了");
                wlPlayer.start();
            }
        });

        wlPlayer.setWlOnLoadListener(new WlOnLoadListener() {
            @Override
            public void onLoad(boolean load) {
                if(load){
                    MyLog.d("加载中...");
                }else{
                    MyLog.d("播放中...");
                }
            }
        });

        wlPlayer.setWlOnPauseResumeListener(new WlOnPauseResumeListener() {
            @Override
            public void onPause(boolean pause) {
                if(pause){
                    MyLog.d("暂停中...");
                }else{
                    MyLog.d("播放中...");
                }
            }
        });

        wlPlayer.setWlOnTimeInfoListener(new WlOnTimeInfoListener() {
            @Override
            public void onTimeInfo(WlTimeInfoBean timeInfoBean) {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = timeInfoBean;
                handler.sendMessage(message);
            }
        });

    }



    public void begin(View view){
        //wlPlayer.setSource("http://mpge.5nd.com/2015/2015-12-26/69708/1.mp3");
        //wlPlayer.setSource("http://downsc.chinaz.net/Files/DownLoad/sound1/202004/12723.mp3");
        wlPlayer.setSource("/mnt/sdcard/Music/xianggelila.mp3");
        wlPlayer.parpared();
    }

    public void pause(View view) {
        wlPlayer.pause();
    }

    public void resume(View view) {
        wlPlayer.resume();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                WlTimeInfoBean wlTimeInfoBean = (WlTimeInfoBean) msg.obj;
                tvTime.setText(WlTimeUtil.secdsToDateFormat(wlTimeInfoBean.getTotalTime(), wlTimeInfoBean.getTotalTime())
                + "/" + WlTimeUtil.secdsToDateFormat(wlTimeInfoBean.getCurrentTime(), wlTimeInfoBean.getTotalTime()));
            }
        }
    };
}
