package com.tony.exoplayersample.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * Created by tony on 2017/12/8.
 * 系统圆圈展开动画
 */
public class CircularRevealAnimation {

    public interface OnRevealAnimationListener {
        void onRevealHide();

        void onRevealShow();
    }

    // 圆圈爆炸效果显示
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void animateRevealShow(
            final Context context, final View view,
            final int startRadius, @ColorRes final int color,
            final OnRevealAnimationListener listener) {
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        // 设置圆形显示动画
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, finalRadius);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.VISIBLE);
                listener.onRevealShow();
            }

            @Override public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setBackgroundColor(ContextCompat.getColor(context, color));
            }
        });

        anim.start();
    }

    // 圆圈凝聚效果
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void animateRevealHide(
            final Context context, final View view,
            final int finalRadius, @ColorRes final int color,
            final OnRevealAnimationListener listener
    ) {
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        int initialRadius = view.getWidth();
        // 与入场动画的区别就是圆圈起始和终止的半径相反
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, finalRadius);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setBackgroundColor(ContextCompat.getColor(context, color));
            }

            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                listener.onRevealHide();
                view.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    // 圆圈爆炸效果显示
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void animateRevealShow(
            final Context context, final View view,
            final View transitionView, @ColorRes final int color,
            final OnRevealAnimationListener listener) {

        int startRadius = transitionView.getWidth() / 2;
        int cx = (transitionView.getLeft() + transitionView.getRight()) / 2;
        int cy = (transitionView.getTop() + transitionView.getBottom()) / 2;

        // 获取斜边
        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        // 设置圆形显示动画
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, finalRadius);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.VISIBLE);
                listener.onRevealShow();
            }

            @Override public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setBackgroundColor(ContextCompat.getColor(context, color));
            }
        });

        anim.start();
    }

    // 圆圈凝聚效果
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void animateRevealHide(
            final Context context, final View view,
            final View transitionView, @ColorRes final int color,
            final OnRevealAnimationListener listener
    ) {

        int finalRadius = transitionView.getWidth() / 2;
        int cx = (transitionView.getLeft() + transitionView.getRight()) / 2;
        int cy = (transitionView.getTop() + transitionView.getBottom()) / 2;

        int initialRadius = view.getWidth();
        // 与入场动画的区别就是圆圈起始和终止的半径相反
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, finalRadius);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setBackgroundColor(ContextCompat.getColor(context, color));
            }

            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                listener.onRevealHide();
                view.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    /**
     * 控件由下往上渐入
     *
     * @param view     渐渐显示的控件
     * @param duration 动画持续时间(单位毫秒)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void showPopupWindow(final View view,
                                final View view_back,
                                final int duration) {
        if (view_back == null) {
            return;
        }

        // 背景渐变动画
        final ObjectAnimator anim = ObjectAnimator.ofFloat(view_back, "Alpha", 0, 1)
                .setDuration(duration);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // 处理动画前操作
            view_back.setAlpha(0);
            view_back.setVisibility(View.VISIBLE);
            view.setAlpha(1);
            view.setVisibility(View.INVISIBLE);

            view.post(new Runnable() {
                @Override
                public void run() {
                    // 结束时半径
                    float endRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
                    // 动画执行时长
                    int duration_view = duration;

                    view.setVisibility(View.VISIBLE);
                    // 弹窗圆形动画
                    ViewAnimationUtils.createCircularReveal(view,
                            view.getWidth() / 2,
                            (int) (view.getHeight() * 0.85), 0, endRadius).setDuration(duration_view).start();

                }
            });
            // 开启背景渐变动画
            anim.start();

        } else {
            // 5.0以下执行另一套动画

            anim.setDuration(duration);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float alpha = (Float) animation.getAnimatedValue();
                    if (view != null) {
                        view.setAlpha(alpha);
                        view.setTranslationY((1 - alpha) * 100);
                    }
                }
            });

            // 动画执行前操作
            view_back.setAlpha(0);
            view.setAlpha(0);
            view_back.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);

            // 动画开始
            anim.start();
        }
    }

    /**
     * 控件由下往上渐入
     *
     * @param view      渐渐显示的控件
     * @param view_back 背景遮罩
     */
    public void showPopupWindow(View view, View view_back) {
        showPopupWindow(view, view_back, 300);
    }

    /**
     * 控件由上往下渐出
     *
     * @param view      渐渐隐藏的控件
     * @param view_back 背景遮罩
     * @param duration  动画持续时间(单位毫秒)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void hidePopupWindow(final View view,
                                final View view_back,
                                int duration) {
        if (view_back == null || view_back.getAlpha() != 1) {
            return;
        }

        // 背景渐变动画
        ObjectAnimator anim = ObjectAnimator.ofFloat(view_back, "Alpha", 1, 0)
                .setDuration(duration);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 弹窗圆形动画
            Animator animator = ViewAnimationUtils.createCircularReveal(view,
                    view.getWidth() / 2,
                    (int) (view.getHeight() * 0.85),
                    (float) Math.hypot(view.getWidth(), view.getHeight()),
                    0);
            animator.setDuration(duration);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                    view_back.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            anim.start();
            animator.start();
        } else {
            // 5.0以下执行另一套动画
            anim.setDuration(duration);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float alpha = (Float) animation.getAnimatedValue();
                    if (view != null) {
                        view.setAlpha(alpha);
                        view.setTranslationY((1 - alpha) * 100);
                    }
                    if (alpha < 0.06) {
                        if (view_back != null) {
                            view_back.setVisibility(View.GONE);
                        }
                        if (view != null) {
                            view.setVisibility(View.GONE);
                        }
                    }
                }
            });

            // 动画开始
            anim.start();
        }
    }

    /**
     * 控件由上往下渐出
     *
     * @param view      渐渐隐藏的控件
     * @param view_back 背景遮罩
     */
    public void hidePopupWindow(View view, View view_back) {
        hidePopupWindow(view, view_back, 300);
    }

    /**
     * 展开介绍
     *
     * @param view 需要展开的介绍容器
     */
    public void showIntroduce(final View view, final int introduce_text_id) {
        showIntroduce(view, introduce_text_id, 350);
    }

    /**
     * 展开介绍
     *
     * @param view     需要展开的介绍容器
     * @param duration 动画时长
     */
    @SuppressLint("WrongViewCast")
    public void showIntroduce(final View view, final int introduce_text_id, final int duration) {
        view.setAlpha(0);
        view.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(duration).start();

        try {
            LinearLayout ll_text = (LinearLayout) view.findViewById(introduce_text_id);
            ll_text.setAlpha(0f);
            Animator anim_alpha = ObjectAnimator.ofFloat(ll_text, "alpha", 0, 1);
            anim_alpha.setStartDelay(duration * 2 / 3);
            anim_alpha.setDuration(duration);
            anim_alpha.start();

            Animator anim_y = ObjectAnimator.ofFloat(ll_text, "Y", 80, 0);
            anim_y.setStartDelay(duration * 2 / 3);
            anim_y.setDuration(duration);
            anim_y.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏介绍
     *
     * @param view 需要隐藏的介绍容器
     */
    public void hideIntroduce(final View view, final int introduce_text_id) {
        hideIntroduce(view, introduce_text_id, 350);
    }

    /**
     * 隐藏介绍
     *
     * @param view     需要隐藏的介绍容器
     * @param duration 动画时长
     */
    public void hideIntroduce(final View view, final int introduce_text_id, final int duration) {

        try {
            LinearLayout ll_text = (LinearLayout) view.findViewById(introduce_text_id);
            if (ll_text == null || ll_text.getAlpha() != 1) {
                return;
            }

            ll_text.setAlpha(0f);
            Animator anim_alpha = ObjectAnimator.ofFloat(ll_text, "alpha", 1, 0);
            anim_alpha.setDuration(duration);
            anim_alpha.start();

            float tvY = ll_text.getY();
            Animator anim_y = ObjectAnimator.ofFloat(ll_text, "Y", tvY, tvY + 80);
            anim_y.setDuration(duration);
            anim_y.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 容器的渐出动画
        Animator anim = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        anim.setStartDelay(duration * 2 / 3);
        anim.setDuration(duration);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim.start();
    }

}
