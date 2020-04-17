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
        wlPlayer.setSource("http://downsc.chinaz.net/Files/DownLoad/sound1/202004/12723.mp3");
        //wlPlayer.setSource("https://ugcws.video.gtimg.com/uwMROfz2r55hIaQXGdGnC2ddDmZewyzlYzQiGREwJsgLwIzc/shg_52074891_50001_8ed24aa6b05939f290a26a53a3b21860.f622.mp4?sdtfrom=v1010&guid=8a83470aa537680b414e8a87e6242f97&vkey=DE4D22BC12177BC2D6C96A5BA76DA6A858CF98311AD87406CD1DDC024B448E92D209CF193CEB5AD22E8BCBA23232961C5EFE589853305B1E7CA0675ECC9948D190F41BD3F9649251BE56404CDA62CEEC5A732C709566BFF049B77F320B67444D8C83C552EDF4195D8C069255B5F0A2965116AABA03941FE6F3AC7E4F96E0E940#t=171");
        wlPlayer.parpared();
    }

}
