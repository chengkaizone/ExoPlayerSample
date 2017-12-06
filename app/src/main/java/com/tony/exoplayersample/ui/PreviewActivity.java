package com.tony.exoplayersample.ui;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.tony.exoplayersample.R;
import com.tony.exoplayersample.view.AVPlayerView;

import java.io.File;

/**
 * Created by tony on 2017/9/28.
 */

public class PreviewActivity extends AppCompatActivity {

    private final static String TAG = "PreviewActivity";

    private AVPlayerView videoPlayer;

    private SeekBar seekBar;
    private boolean isTrackingTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        videoPlayer = (AVPlayerView) findViewById(R.id.video_player);


        seekBar = (SeekBar) findViewById(R.id.seekbar);

        videoPlayer.setLooping(true);
        videoPlayer.play(R.raw.test_video);

        findViewById(R.id.btn_play_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String path = Environment.getExternalStorageDirectory() + File.separator + "video.mp4";

                Log.i(TAG, path);
                videoPlayer.play(path);
            }
        });

        videoPlayer.setOnPlayerListener(new AVPlayerView.OnAVPlayerViewListener() {
            @Override
            public void onProgress(int progress) {

                Log.i(TAG, "progress: " + progress);
                seekBar.setProgress(progress);
            }
        });

        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {


                Log.i(TAG, "onProgressChanged");

                if (isTrackingTouch) {
                    //videoPlayer.seekTo(seekBar.getProgress());
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                Log.i(TAG, "onStartTrackingTouch");

                isTrackingTouch = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Log.i(TAG, "onStopTrackingTouch");

                isTrackingTouch = false;

                videoPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        videoPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        videoPlayer.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}