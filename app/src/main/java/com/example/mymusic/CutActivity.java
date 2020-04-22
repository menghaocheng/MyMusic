package com.example.mymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.myplayer.MlPlayer.WlPlayer;
import com.example.myplayer.WlTimeInfoBean;
import com.example.myplayer.listener.WlOnParparedListener;
import com.example.myplayer.listener.WlOnPcmInfoListener;
import com.example.myplayer.listener.WlOnTimeInfoListener;
import com.example.myplayer.log.MyLog;

public class CutActivity extends AppCompatActivity {

    private WlPlayer wlPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutaudio);

        wlPlayer = new WlPlayer();

        wlPlayer.setWlOnParparedListener(new WlOnParparedListener() {
            @Override
            public void onParpared() {
                wlPlayer.cutAudioPlay(20, 40, true);
            }
        });

        wlPlayer.setWlOnTimeInfoListener(new WlOnTimeInfoListener() {
            @Override
            public void onTimeInfo(WlTimeInfoBean timeInfoBean) {
                MyLog.d(timeInfoBean.toString());
            }
        });

        wlPlayer.setWlOnPcmInfoListener(new WlOnPcmInfoListener() {
            @Override
            public void onPcmInfo(byte[] buffer, int buffersize) {
                MyLog.d("buffsize: " + buffersize);
            }

            @Override
            public void onPcmRate(int samplerate, int bit, int channels) {
                MyLog.d("samplerate: " +samplerate);
            }
        });

    }


    public void cutaudio(View view) {
        wlPlayer.setSource("/mnt/sdcard/Music/xianggelila.mp3");
        wlPlayer.parpared();
    }
}
