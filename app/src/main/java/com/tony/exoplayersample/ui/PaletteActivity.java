package com.tony.exoplayersample.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tony.exoplayersample.R;

import java.security.MessageDigest;

/**
 * Created by tony on 2017/9/28.
 */

public class PaletteActivity extends AppCompatActivity {
    private static final String TAG = "PaletteActivity";

    private ImageView img_icon;
    private ImageView img_icon_2;
    private LinearLayout ln_palette;
    private LinearLayout ln_palette_1;
    private LinearLayout ln_palette_2;
    private LinearLayout ln_palette_3;
    private LinearLayout ln_palette_4;
    private LinearLayout ln_palette_5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        img_icon = (ImageView) findViewById(R.id.img_icon);
        img_icon_2 = (ImageView) findViewById(R.id.img_icon_2);
        ln_palette = (LinearLayout) findViewById(R.id.ln_palette);
        ln_palette_1 = (LinearLayout) findViewById(R.id.ln_palette_1);
        ln_palette_2 = (LinearLayout) findViewById(R.id.ln_palette_2);
        ln_palette_3 = (LinearLayout) findViewById(R.id.ln_palette_3);
        ln_palette_4 = (LinearLayout) findViewById(R.id.ln_palette_4);
        ln_palette_5 = (LinearLayout) findViewById(R.id.ln_palette_5);

        img_icon.setImageResource(R.mipmap.ic_launcher);

        RequestBuilder<Bitmap> bitmapRequestBuilder = GlideApp.with(this)
                .asBitmap()
                .load(R.mipmap.icon_test)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_foreground)
                //.transform(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                });

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.transform(new GlideCircleTransform(this));
        bitmapRequestBuilder.apply(requestOptions).into(img_icon);

        Picasso.with(this)
                .load(R.mipmap.ic_launcher)
                //.resize(60, 60)
                .transform(new CircleTransform())
                //.centerInside() // 此处调用必须调用resize方法
                .placeholder(R.mipmap.ic_launcher_round)
                .into(img_icon_2);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {

                // 活力色
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                // 活力的 暗色
                Palette.Swatch vibrantDark = palette.getDarkVibrantSwatch();
                // 活力的 亮色
                Palette.Swatch vibrantLight = palette.getLightVibrantSwatch();
                // 柔和的
                Palette.Swatch muted = palette.getMutedSwatch();
                // 柔和的 暗色
                Palette.Swatch mutedDark = palette.getDarkMutedSwatch();
                // 柔和的 暗色
                Palette.Swatch mutedLight = palette.getLightMutedSwatch();

                Log.i(TAG, "--->" + vibrant + " | " + vibrantDark + " | " + vibrantLight);
                ln_palette.setBackground(shapeDrawable(vibrant.getRgb(), vibrantLight.getRgb()));
                ln_palette_1.setBackgroundColor(vibrantDark.getRgb());
                ln_palette_2.setBackgroundColor(vibrantLight.getRgb());
//                ln_palette_3.setBackgroundColor(muted.getRgb());
//                ln_palette_4.setBackgroundColor(mutedDark.getRgb());
//                ln_palette_5.setBackgroundColor(mutedLight.getRgb());

            }
        });

    }

    /**
     * 创建Drawable对象
     * @param RGBValues
     * @param color2
     * @return
     */
    private Drawable shapeDrawable(int RGBValues, int color2){
        GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.TL_BR
                ,new int[]{RGBValues,color2});
        shape.setShape(GradientDrawable.RECTANGLE);
        //设置渐变方式
        shape.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        //圆角
        shape.setCornerRadius(8);
        return shape;
    }

    /**
     * 圆角显示图片-Picasso
     */
    class RoundTransform implements Transformation {
        private int radius;//圆角值

        public RoundTransform(int radius) {
            this.radius = radius;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int width = source.getWidth();
            int height = source.getHeight();
            //画板
            Bitmap bitmap = Bitmap.createBitmap(width, height, source.getConfig());
            Paint paint = new Paint();
            Canvas canvas = new Canvas(bitmap);//创建同尺寸的画布
            paint.setAntiAlias(true);//画笔抗锯齿
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            //画圆角背景
            RectF rectF = new RectF(new Rect(0, 0, width, height));//赋值
            canvas.drawRoundRect(rectF, radius, radius, paint);//画圆角矩形
            //
            paint.setFilterBitmap(true);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(source, 0, 0, paint);
            source.recycle();//释放

            return bitmap;
        }

        @Override
        public String key() {
            return "round : radius = " + radius;
        }
    }

}

/**
 * Created by 刘楠 on 2016/8/31 0031.23:09
 */
class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        /**
         * 求出宽和高的哪个小
         */
        int  size = Math.min(source.getWidth(), source.getHeight());

        /**
         * 求中心点
         */
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        /**
         * 生成BitMap
         */
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            //释放
            source.recycle();
        }

        /**
         * 建立新的Bitmap
         */
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        /**
         * 画布画笔
         */
        Canvas canvas = new Canvas(bitmap);
        Paint  paint  = new Paint();

        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        /**
         * 画圆
         */
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}

class GlideCircleTransform extends BitmapTransformation {

    public GlideCircleTransform(Context context){
        super(context);
    }
    /**
     *  重写 生成圆角图片
     * @param pool
     * @param toTransform
     * @param outWidth
     * @param outHeight
     * @return
     */
    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool,toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        //画布中背景图片与绘制图片交集部分
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return result;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
    }
}