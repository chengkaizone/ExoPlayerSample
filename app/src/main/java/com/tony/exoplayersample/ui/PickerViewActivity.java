package com.tony.exoplayersample.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.tony.exoplayersample.R;

import java.util.Arrays;

/**
 * Created by tony on 2017/11/21.
 */

public class PickerViewActivity extends AppCompatActivity {
    public static final String TAG = "PickerViewActivity";

    private WheelView wheel_intl;
    private WheelView wheel_usa;
    private WheelView wheel_cups;

    static String[] intls = new String[]{"60", "65", "70", "75", "80", "85", "90"};
    static String[] usas = new String[]{"28", "30", "32", "34", "36", "38", "40"};
    static String[] cups = new String[]{"AA", "A", "B", "C", "D", "E", "F"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picker_view);

        wheel_intl = findViewById(R.id.wheel_intl);
        wheel_usa = findViewById(R.id.wheel_usa);
        wheel_cups = findViewById(R.id.wheel_cups);

        wheel_intl.setCyclic(false);
        wheel_usa.setCyclic(false);
        wheel_cups.setCyclic(false);

        //wheel_intl.set
        wheel_intl.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {


                wheel_usa.setCurrentItem(index);
                Log.i(TAG, "intl " + index + " | " + intls[index]);
            }
        });
        wheel_usa.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {

                wheel_intl.setCurrentItem(index);
                Log.i(TAG, "usa " + index + " | " + usas[index]);
            }
        });
        wheel_cups.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                Log.i(TAG, "cups " + index + " | " + cups[index]);
            }
        });

        ArrayWheelAdapter adapter = new ArrayWheelAdapter(Arrays.asList(intls));
        wheel_intl.setAdapter(adapter);
        ArrayWheelAdapter adapter2 = new ArrayWheelAdapter(Arrays.asList(usas));
        wheel_usa.setAdapter(adapter2);
        ArrayWheelAdapter adapter3 = new ArrayWheelAdapter(Arrays.asList(cups));
        wheel_cups.setAdapter(adapter3);
    }

}
