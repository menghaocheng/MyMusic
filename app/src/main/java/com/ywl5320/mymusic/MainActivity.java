package com.ywl5320.mymusic;

import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ywl5320.myplayer.WlTimeInfoBean;
import com.ywl5320.myplayer.listener.WlOnCompleteListener;
import com.ywl5320.myplayer.listener.WlOnErrorListener;
import com.ywl5320.myplayer.listener.WlOnLoadListener;
import com.ywl5320.myplayer.listener.WlOnParparedListener;
import com.ywl5320.myplayer.listener.WlOnPauseResumeListener;
import com.ywl5320.myplayer.listener.WlOnTimeInfoListener;
import com.ywl5320.myplayer.log.MyLog;
import com.ywl5320.myplayer.opengl.WlGLSurfaceView;
import com.ywl5320.myplayer.player.WlPlayer;
import com.ywl5320.myplayer.util.WlTimeUtil;


public class MainActivity extends AppCompatActivity {
    private WlPlayer wlPlayer;
    private TextView tvTime;
    private WlGLSurfaceView wlGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTime = findViewById(R.id.tv_time);
        wlGLSurfaceView = findViewById(R.id.wlglsurfaceview);
        wlPlayer = new WlPlayer();
        wlPlayer.setWlGLSurfaceView(wlGLSurfaceView);
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
                if(load)
                {
                    MyLog.d("加载中...");
                }
                else
                {
                    MyLog.d("播放中...");
                }
            }
        });

        wlPlayer.setWlOnPauseResumeListener(new WlOnPauseResumeListener() {
            @Override
            public void onPause(boolean pause) {
                if(pause)
                {
                    MyLog.d("暂停中...");
                }
                else
                {
                    MyLog.d("播放中...");
                }
            }
        });

        wlPlayer.setWlOnTimeInfoListener(new WlOnTimeInfoListener() {
            @Override
            public void onTimeInfo(WlTimeInfoBean timeInfoBean) {
//                MyLog.d(timeInfoBean.toString());
                Message message = Message.obtain();
                message.what = 1;
                message.obj = timeInfoBean;
                handler.sendMessage(message);

            }
        });

        wlPlayer.setWlOnErrorListener(new WlOnErrorListener() {
            @Override
            public void onError(int code, String msg) {
                MyLog.d("code:" + code + ", msg:" + msg);
            }
        });

        wlPlayer.setWlOnCompleteListener(new WlOnCompleteListener() {
            @Override
            public void onComplete() {
                MyLog.d("播放完成了");
            }
        });
    }



    public void begin(View view){
        wlPlayer.setSource("/sdcard/Movies/《刺猬索尼克》定档2月28日，童年经典首登银幕，音速出击拯救世界！.mp4");
        //wlPlayer.setSource("/sdcard/Movies/bandicam.mp4");
        //wlPlayer.setSource("/sdcard/Movies/《你好世界》少男为爱穿越 最后一秒故事反转《你好世界》敬请期待.mp4");
        ///wlPlayer.setSource("/sdcard/Movies/《合法伴侣》定档3月13日，李治廷白客上演“假情侣真兄弟”.mp4");
        //wlPlayer.setSource("http://mpge.5nd.com/2015/2015-12-26/69708/1.mp3");
        //wlPlayer.setSource("http://downsc.chinaz.net/Files/DownLoad/sound1/202004/12723.mp3");
        //wlPlayer.setSource("/mnt/sdcard/Music/xianggelila.mp3");
        //wlPlayer.setSource("http://ngcdn004.cnr.cn/live/dszs/index.m3u8");
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
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1)
            {
                WlTimeInfoBean wlTimeInfoBean = (WlTimeInfoBean) msg.obj;
                tvTime.setText(WlTimeUtil.secdsToDateFormat(wlTimeInfoBean.getTotalTime(), wlTimeInfoBean.getTotalTime())
                + "/" + WlTimeUtil.secdsToDateFormat(wlTimeInfoBean.getCurrentTime(), wlTimeInfoBean.getTotalTime()));
            }
        }
    };

    public void stop(View view) {
        wlPlayer.stop();
    }

    public void seek(View view) {
        wlPlayer.seek(260);
    }

    public void next(View view) {
        //wlPlayer.playNext("http://ngcdn004.cnr.cn/live/dszs/index.m3u8");
    }
}
