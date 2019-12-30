package com.zwb.danmaku.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.zwb.danmaku.model.BaseDanmaku;

import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/29 17:48.
 * @ desc:
 **/
public interface IDrawHelper {

    /**
     * 绘制之前准备
     *
     * @param textPaint    文字画笔
     * @param shadowPaint  阴影画笔
     * @param canvasWidth  画布宽度
     * @param canvasHeight 画布高度
     */
    void onDrawPrepared(@NonNull Paint textPaint, @NonNull Paint shadowPaint, int canvasWidth, int canvasHeight);

    /**
     * @param canvas       画布
     * @param textPaint    文字画笔
     * @param shadowPaint  阴影画笔
     * @param canvasWidth  画布宽度
     * @param canvasHeight 画布高度
     */
    void onDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, @NonNull Paint shadowPaint, int canvasWidth, int canvasHeight);

    /**
     * 设置变化速度
     * @param speed 2 * den   按照弹幕的刷新时间变化
     * @return this
     */
    IDrawHelper setSpeed(float speed);

    /**
     * 设置像素密度
     * @param den 像素密度
     * @return this
     */
    IDrawHelper setDen(float den);

    /**
     * 设置离屏缓存数量
     * @param limit 2
     * @return this
     */
    IDrawHelper setOffScreenLimit(int limit);

    /**
     * 设置需要显示的弹幕
     * @param danmakus 弹幕列表
     */
    void setDanmakus(@NonNull List<BaseDanmaku> danmakus);

    /**
     * 添加一个新的的弹幕
     * @param danmaku 弹幕
     */
    void addDanmaku(@NonNull BaseDanmaku danmaku);

    /**
     * 添加一个新的的弹幕列表
     * @param danmakus 弹幕列表
     */
    void addDanmakus(@NonNull List<BaseDanmaku> danmakus);

    /**
     * 清除画布
     */
    void clear();
}