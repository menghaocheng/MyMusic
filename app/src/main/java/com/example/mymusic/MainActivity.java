package com.example.mymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myplayer.Demo;

public class MainActivity extends AppCompatActivity {

    private Demo demo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        demo = new Demo();
        demo.testFfmpeg();
    }

}
