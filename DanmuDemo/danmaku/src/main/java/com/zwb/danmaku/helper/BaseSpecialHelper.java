package com.zwb.danmaku.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.zwb.danmaku.model.AlphaValue;
import com.zwb.danmaku.model.BaseConfig;
import com.zwb.danmaku.model.BaseDanmaku;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2020/1/13 14:59.
 * @ desc: 特殊弹幕
 **/
public class BaseSpecialHelper implements IDrawHelper, ISpecialDrawHelper {
    private List<BaseDanmaku> originDanmakus = new ArrayList<>();       // 原始数据
    private List<BaseDanmaku> penddingDanmakus = new ArrayList<>();     // 待处理的弹幕
    private List<BaseDanmaku> showingDanmakus = new ArrayList<>();      // 正在显示或即将显示的弹幕
    private List<BaseDanmaku> goneDanmakus = new ArrayList<>();         // 已经显示过的弹幕（不再显示）
    private long mInterval = 1000;                                      // 显示的时间间隔
    private int mCountLimit = 10;                                       // 显示的弹幕数量

    private long lastAddTime = 0;                                       // 上一次添加弹幕的时间
    private BaseConfig baseConfig;                                              // 弹幕基本配置

    @Override
    public synchronized void onDrawPrepared(Context context, @NonNull Paint textPaint, @NonNull Paint mTextShadowPaint, int canvasWidth, int canvasHeight) {
        // 符合间隔时间且待处理的弹幕不为空且显示的弹幕未达到上限
        if (System.currentTimeMillis() - lastAddTime > mInterval && !penddingDanmakus.isEmpty() && showingDanmakus.size() < mCountLimit) {
            BaseDanmaku danmaku = penddingDanmakus.remove(0);
            if (!danmaku.isInit()) {
                if (baseConfig != null) {
                    baseConfig.checkDanmakuConfig(danmaku, true);
                }
                danmaku.initSize(textPaint);
            }
            danmaku.preparedBg(context);
            danmaku.setInit(true);
            showingDanmakus.add(danmaku);
            lastAddTime = System.currentTimeMillis();
        }
    }

    @Override
    public synchronized void onDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, @NonNull Paint mTextShadowPaint, @NonNull Paint mBgPaint, int canvasWidth, int canvasHeight) {
        for (BaseDanmaku info : showingDanmakus) {
            info.startDraw(canvas, textPaint, mTextShadowPaint, mBgPaint, canvasWidth, canvasHeight);
        }
        // 这行是为了确保剔除getShowingDanmakus不可显示的弹幕
        checkedGoneDanmaku();
    }

    @Override
    public BaseSpecialHelper setSpeed(float speed) {
        return this;
    }

    @Override
    public BaseSpecialHelper setBaseConfig(BaseConfig baseConfig) {
        this.baseConfig = baseConfig;
        return this;
    }

    @Override
    public BaseSpecialHelper setDen(float den) {
        return this;
    }

    @Override
    public BaseSpecialHelper setOffScreenLimit(int limit) {
        return this;
    }

    @Override
    public BaseSpecialHelper setInterval(long interval) {
        mInterval = interval;
        return this;
    }

    @Override
    public BaseSpecialHelper setCountLimit(int countLimit) {
        mCountLimit = countLimit;
        return this;
    }

    @Override
    public synchronized void setDanmakus(@NonNull List<BaseDanmaku> danmakus) {
        clear();
        originDanmakus.addAll(danmakus);
        penddingDanmakus.addAll(danmakus);
    }

    @Override
    public synchronized void addDanmaku(@NonNull BaseDanmaku danmaku) {
        originDanmakus.add(danmaku);
        penddingDanmakus.add(danmaku);
    }

    @Override
    public synchronized void addDanmakus(@NonNull List<BaseDanmaku> danmakus) {
        originDanmakus.addAll(danmakus);
        penddingDanmakus.addAll(danmakus);
    }

    @Override
    public synchronized void clear() {
        originDanmakus.clear();
        penddingDanmakus.clear();
        showingDanmakus.clear();
        goneDanmakus.clear();
    }

    /**
     * 把不可见的弹幕加入列表
     */
    private void checkedGoneDanmaku() {
        if (showingDanmakus.isEmpty()) {
            return;
        }
        Iterator<BaseDanmaku> iterator = showingDanmakus.iterator();
        while (iterator.hasNext()) {
            BaseDanmaku danmaku = iterator.next();
            if (danmaku.isGone()) {
                iterator.remove();
                danmaku.release();
                goneDanmakus.add(danmaku);
            } else {
                // 第一个弹幕不是隐藏状态，后面的更不可能是了
                break;
            }
        }
    }

    @Override
    public int getState() {
        if (penddingDanmakus.isEmpty() && showingDanmakus.isEmpty() && !goneDanmakus.isEmpty()) {
            return DrawState.STATE_GONE;
        } else if (penddingDanmakus.isEmpty() && showingDanmakus.isEmpty()) {
            return DrawState.STATE_EMPTY;
        }
        return DrawState.STATE_SHOWING;
    }

    @Override
    public synchronized void rePlay() {
        penddingDanmakus.clear();
        showingDanmakus.clear();
        goneDanmakus.clear();
        for (BaseDanmaku danmaku : originDanmakus) {
            danmaku.setShowState(BaseDanmaku.ShowState.STATE_NEVER_SHOWED);
            danmaku.setAlpha(AlphaValue.MAX);
            danmaku.setSpeed(0);
        }
        penddingDanmakus.addAll(originDanmakus);
    }
}
