package com.tony.exoplayersample.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chengkaizone.numberkeyboard.KeyboardUtil;
import com.sevenheaven.segmentcontrol.SegmentControl;
import com.tony.exoplayersample.R;

/**
 * Created by tony on 2017/11/22.
 */
public class SegmentControlActivity extends AppCompatActivity {

    private SegmentControl horizontalSegmentControl;
    private SegmentControl verticalSegmentControl;

    EditText et_number;

    private String[] genders = new String[]{"男", "女"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_segmentcontrol);

        horizontalSegmentControl = findViewById(R.id.segment_control);
        verticalSegmentControl = findViewById(R.id.segment_control2);
        et_number = findViewById(R.id.et_number);

        horizontalSegmentControl.setText(genders);

        horizontalSegmentControl.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {

                String gender = genders[index];

                Toast.makeText(SegmentControlActivity.this, gender, Toast.LENGTH_SHORT).show();

            }
        });

    }
}
