package com.tony.exoplayersample.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sevenheaven.segmentcontrol.SegmentControl;
import com.tony.exoplayersample.R;
import com.tony.exoplayersample.videoedit.VideoEditActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, PreviewActivity.class));
            }
        });

        findViewById(R.id.btn_palette).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, PaletteActivity.class));
            }
        });

        findViewById(R.id.btn_okhttp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, OkHttpActivity.class));
            }
        });

        findViewById(R.id.btn_bottom_sheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, BottomSheetActivity.class));
            }
        });
        findViewById(R.id.btn_video_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, VideoEditActivity.class));
            }
        });

        findViewById(R.id.btn_segment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, SegmentControlActivity.class));
            }
        });

        final FloatingActionButton other_fab_circle = findViewById(R.id.other_fab_circle);
        findViewById(R.id.other_fab_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, other_fab_circle, other_fab_circle.getTransitionName());
                    startActivity(new Intent(MainActivity.this, OtherActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(MainActivity.this, OtherActivity.class));
                }
            }
        });
    }
}
