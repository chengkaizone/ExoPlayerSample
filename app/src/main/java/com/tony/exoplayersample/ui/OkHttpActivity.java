package com.tony.exoplayersample.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.exoplayersample.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tony on 2017/9/28.
 */

public class OkHttpActivity extends AppCompatActivity {

    private TextView tv_result;

    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp);

        tv_result = (TextView) findViewById(R.id.tv_result);

        findViewById(R.id.btn_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestGet("http://openexchangerates.org/api/currencies.json");
            }
        });

        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestPost("http://openexchangerates.org/api/currencies.json");
            }
        });

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.INTERNET)
//                != PackageManager.PERMISSION_GRANTED)
//        {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.INTERNET},
//                    1000);
//        } else {
//        }
    }

    private void requestGet(final String url) {


        OkHttpClient mOkHttpClient = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(url)
                //.header("key", "value") // 头部参数
                .build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_result.setText("结果失败~");
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                try {
                    final String bodyString = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_result.setText("结果成功: " + bodyString);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private  void requestPost(String url) {

        OkHttpClient mOkHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                //.add("key", "value")
                //.add("key", "value")
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_result.setText("结果失败~");
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                try {
                    final String bodyString = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_result.setText("结果成功: " + bodyString);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == 1000)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            } else
            {
                // Permission Denied
                Toast.makeText(OkHttpActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
