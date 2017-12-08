package com.tony.exoplayersample.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tony.exoplayersample.R;
import com.tony.exoplayersample.util.CircularRevealAnimation;

/**
 * Created by tony on 2017/12/8.
 */

public class OtherActivity extends AppCompatActivity {

    private FloatingActionButton other_fab_circle;
    private TextView other_tv_container;
    private ImageView other_iv_close;
    private RelativeLayout other_rl_container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_other);

        other_fab_circle = findViewById(R.id.other_fab_circle);
        other_tv_container = findViewById(R.id.other_tv_container);
        other_iv_close = findViewById(R.id.other_iv_close);
        other_rl_container = findViewById(R.id.other_rl_container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimation(); // 入场动画
            setupExitAnimation(); // 退场动画
        } else {
            initViews();
        }

    }

    // 入场动画
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.arc_motion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override public void onTransitionStart(Transition transition) {

            }

            @Override public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override public void onTransitionCancel(Transition transition) {

            }

            @Override public void onTransitionPause(Transition transition) {

            }

            @Override public void onTransitionResume(Transition transition) {

            }
        });
    }

    // 动画展示
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealShow() {
        CircularRevealAnimation.animateRevealShow(
                this, other_rl_container,
                other_fab_circle, R.color.colorAccent,
                new CircularRevealAnimation.OnRevealAnimationListener() {
                    @Override public void onRevealHide() {

                    }

                    @Override public void onRevealShow() {
                        initViews();
                    }
                });
    }

    // 退出动画
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupExitAnimation() {
        Fade fade = new Fade();
        getWindow().setReturnTransition(fade);
        fade.setDuration(300);
    }

    // 初始化视图
    private void initViews() {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(OtherActivity.this, android.R.anim.fade_in);
                animation.setDuration(300);
                other_tv_container.startAnimation(animation);
                other_iv_close.setAnimation(animation);
                other_tv_container.setVisibility(View.VISIBLE);
                other_iv_close.setVisibility(View.VISIBLE);

            }
        });
    }

    // 退出按钮
    public void backActivity(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    // 退出事件
    @Override public void onBackPressed() {

        CircularRevealAnimation.animateRevealHide(
                this, other_rl_container,
                other_fab_circle, R.color.colorAccent,
                new CircularRevealAnimation.OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {
                        OtherActivity.super.onBackPressed();
                    }

                    @Override
                    public void onRevealShow() {

                    }
                });
    }

}
