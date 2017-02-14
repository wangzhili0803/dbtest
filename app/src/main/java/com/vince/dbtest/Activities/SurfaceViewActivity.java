package com.vince.dbtest.Activities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.vince.dbtest.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SurfaceViewActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private Button btn_play;
    private Button btn_stop;
    private Button btn_pause;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_play.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);//声音流
        mp.setDisplay(holder);//设置显示
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)//getExternalStorageDirectory()
                + "/a.mp4";//daomengkongjian01
        try {
            File file = new File(path);
            FileInputStream in = new FileInputStream(file);
            mp.setDataSource(in.getFD());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                mp.start();
                break;
            case R.id.btn_stop:
                mp.stop();
                break;
            case R.id.btn_pause:
                mp.pause();
                break;
        }
    }
}
