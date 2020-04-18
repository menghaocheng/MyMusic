package com.example.mymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.myplayer.Demo;
import com.example.myplayer.MlPlayer.WlPlayer;
import com.example.myplayer.listener.WlOnParparedListener;
import com.example.myplayer.log.MyLog;

public class MainActivity extends AppCompatActivity {

    private WlPlayer wlPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wlPlayer = new WlPlayer();
        wlPlayer.setWlOnParparedListener(new WlOnParparedListener() {
            @Override
            public void onParpared() {
                MyLog.d("准备好了，可以开始播放声音了");
                wlPlayer.start();
            }
        });
    }

    public void begin(View view){
        //wlPlayer.setSource("http://mpge.5nd.com/2015/2015-12-26/69708/1.mp3");
        //wlPlayer.setSource("http://downsc.chinaz.net/Files/DownLoad/sound1/202004/12723.mp3");
        wlPlayer.setSource("/mnt/sdcard/Music/xianggelila.mp3");
        wlPlayer.parpared();
    }

}
