package com.zwb.danmaku.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

/**
 * @ author : zhouweibin
 * @ time: 2020/1/17 16:21.
 * @ desc:
 **/
public class BitmapUtils {

    /**
     * 获取弹幕背景
     *
     * @param context     上下文
     * @param drawableRes 图片资源
     * @param targetW     目标宽度
     * @param targetH     目标高度
     * @return null
     * <p>
     * 规则：以目标高度作为参照，保持高度与弹幕高度一致（需要继续拉伸）
     */
    public static Bitmap getBackgroundForDanmaku(Context context, @DrawableRes int drawableRes, int targetW, int targetH) {
        Bitmap bitmap = getNinePatchBitmap(context, drawableRes, targetW, targetH);
        if (bitmap == null) {
            bitmap = getBitmapByResId(context, drawableRes, targetW, targetH);
        }
        return bitmap;
    }

    /**
     * 获取弹幕背景
     *
     * @param context     上下文
     * @param drawableRes 图片资源
     * @param targetW     目标宽度
     * @param targetH     目标高度
     * @return null
     * <p>
     * 规则：以目标高度作为参照，保持高度与弹幕高度一致（需要继续拉伸）
     */
    public static Bitmap getNinePatchBitmap(Context context, @DrawableRes int drawableRes, int targetW, int targetH) {
        Bitmap originBitmap = BitmapFactory.decodeResource(context.getResources(), drawableRes);
        if (originBitmap == null) {
            return null;
        }
        int bitmapH = originBitmap.getHeight();
        float scale = targetH * 1.0f / bitmapH;
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap((int) (targetW / scale + 0.5f), bitmapH, Bitmap.Config.ARGB_4444);
        canvas.setBitmap(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        try {
            // 如果是.9图片，以它特有的方式绘制，使其生成的图片最终与弹幕宽高保持相同的比例
            //获取点9块
            NinePatch np = new NinePatch(originBitmap, originBitmap.getNinePatchChunk(), null);
            //开始绘制
            np.draw(canvas, new RectF(0, 0, targetW / scale, bitmapH));
        } catch (Exception e) {
            e.printStackTrace();
            return originBitmap;
        }
        originBitmap.recycle();
        return bitmap;
    }

    /**
     * @param context     上下文
     * @param drawableRes 图片资源
     * @return bitmap
     */
    public static Bitmap getBitmapByResId(Context context, int drawableRes, int targetW, int targetH) {
        Drawable drawable = context.getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        // 为0的情况是xml配置的shape加载的drawable 直接设置目标大小
        int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : targetW;
        int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : targetH;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        canvas.setBitmap(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
