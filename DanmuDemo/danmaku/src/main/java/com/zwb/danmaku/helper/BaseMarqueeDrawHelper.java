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
 * @ time: 2019/12/31 15:42.
 * @ desc: 移动的弹幕--跑马灯 居中显示
 **/
public abstract class BaseMarqueeDrawHelper implements IDrawHelper {

    int canvasWidth;                                                    // 画布高度
    int canvasHeight;                                                   // 画布高度
    float den;                                                          // 默认速度
    private int offScreenLimit = Integer.MAX_VALUE;                     // 允许离屏初始化的弹幕数量

    private List<BaseDanmaku> originDanmakus = new ArrayList<>();       // 原始数据
    private List<BaseDanmaku> penddingDanmakus = new ArrayList<>();     // 待处理的弹幕
    private List<BaseDanmaku> showingDanmakus = new ArrayList<>();      // 正在显示或即将显示的弹幕
    private List<BaseDanmaku> goneDanmakus = new ArrayList<>();         // 已经显示过的弹幕（不再显示）

    private BaseConfig baseConfig;                                      // 弹幕基本配置

    @Override
    public synchronized void onDrawPrepared(Context context, @NonNull Paint textPaint, @NonNull Paint mTextShadowPaint, int canvasWidth, int canvasHeight) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
        // 还有待处理的弹幕，准备加入轨道
        if (!penddingDanmakus.isEmpty()) {
            Iterator<BaseDanmaku> iterator = penddingDanmakus.iterator();
            while (iterator.hasNext()) {
                // 当前预处理的弹幕已经超过了缓存的最大数量
                if (getNeverShowedNum() >= offScreenLimit) {
                    break;
                }
                BaseDanmaku danmaku = iterator.next();
                iterator.remove();
                // 循环显示的时候可不再执行
                if (!danmaku.isInit()) {
                    if (baseConfig != null) {
                        baseConfig.checkDanmakuConfig(danmaku, false);
                    }
                    danmaku.initSize(textPaint);
                }
                initPosition(danmaku, getLastNeedShowingDanmaku(), canvasWidth, canvasHeight);
                danmaku.preparedBg(context);
                danmaku.setInit(true);
                danmaku.setShowState(BaseDanmaku.ShowState.STATE_NEVER_SHOWED);
                showingDanmakus.add(danmaku);
            }
        }
    }

    /**
     * 获取从未显示过的弹幕数量
     *
     * @return 0
     */
    public int getNeverShowedNum() {
        int size = 0;
        for (BaseDanmaku danmaku : showingDanmakus) {
            if (danmaku.isNeverShowed()) {
                size++;
            }
        }
        return size;
    }


    /**
     * 初始化弹幕的位置
     *
     * @param danmaku                弹幕
     * @param lastNeedShowingDanmaku 最后一个需要显示的弹幕--已经处理完毕的
     * @param canvasWidth            画布宽度
     * @param canvasHeight           画布高度
     */
    protected abstract void initPosition(BaseDanmaku danmaku, BaseDanmaku lastNeedShowingDanmaku, int canvasWidth, int canvasHeight);

    private BaseDanmaku getLastNeedShowingDanmaku() {
        if (showingDanmakus.isEmpty()) {
            return null;
        }
        return showingDanmakus.get(showingDanmakus.size() - 1);
    }

    @Override
    public synchronized void onDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, @NonNull Paint mTextShadowPaint, @NonNull Paint mBgPaint, int canvasWidth, int canvasHeight) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
        for (BaseDanmaku info : showingDanmakus) {
            info.startDraw(canvas, textPaint, mTextShadowPaint, mBgPaint, canvasWidth, canvasHeight);
        }
        // 这行是为了确保剔除getShowingDanmakus不可显示的弹幕
        checkedGoneDanmaku();
    }

    /**
     * 把不可见的弹幕加入列表
     */
    public void checkedGoneDanmaku() {
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
    public BaseMarqueeDrawHelper setSpeed(float speed) {
        if (baseConfig != null) {
            baseConfig.setSpeed(speed);
        }
        return this;
    }

    @Override
    public BaseMarqueeDrawHelper setBaseConfig(BaseConfig baseConfig) {
        this.baseConfig = baseConfig;
        return this;
    }

    @Override
    public BaseMarqueeDrawHelper setOffScreenLimit(int offScreenLimit) {
        this.offScreenLimit = offScreenLimit;
        return this;
    }

    @Override
    public BaseMarqueeDrawHelper setDen(float den) {
        this.den = den;
        return this;
    }

    @Override
    public synchronized void setDanmakus(@NonNull List<BaseDanmaku> danmakuList) {
        clear();
        this.originDanmakus.addAll(danmakuList);
        this.penddingDanmakus.addAll(danmakuList);
    }

    @Override
    public synchronized void addDanmaku(@NonNull BaseDanmaku danmaku) {
        addDanmaku(danmaku, false);
    }

    @Override
    public void addDanmaku(@NonNull BaseDanmaku danmaku, boolean addFirst) {
        if (addFirst) {
            // 当前弹幕全部显示完了，添加在最后面
            if (penddingDanmakus.isEmpty()) {
                addDanmaku(danmaku);
            } else {
                // 将弹幕插入到指定的位置
                BaseDanmaku bean = penddingDanmakus.get(0);
                penddingDanmakus.add(0, danmaku);
                int index = originDanmakus.indexOf(bean);
                if (index >= 0) {
                    originDanmakus.add(index, danmaku);
                }
            }
        } else {
            this.originDanmakus.add(danmaku);
            this.penddingDanmakus.add(danmaku);
        }
    }

    @Override
    public synchronized void addDanmakus(@NonNull List<BaseDanmaku> danmakus) {
        this.originDanmakus.addAll(danmakus);
        this.penddingDanmakus.addAll(danmakus);
    }

    @Override
    public synchronized void clear() {
        originDanmakus.clear();
        penddingDanmakus.clear();
        showingDanmakus.clear();
        goneDanmakus.clear();
    }


    @Override
    public int getState() {
        if (penddingDanmakus.isEmpty()) {
            int goneCount = 0;
            int showingCount = 0;
            goneCount += goneDanmakus.size();
            showingCount += showingDanmakus.size();
            if (goneCount == 0 && showingCount == 0) {
                return DrawState.STATE_EMPTY;
            } else if (goneCount > 0 && showingCount == 0) {
                // 消失的列表大于0 要显示的列表等于0 说明已经显示完了
                return DrawState.STATE_GONE;
            } else {
                return DrawState.STATE_SHOWING;
            }
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
        }
        penddingDanmakus.addAll(originDanmakus);
    }

    @Override
    public BaseDanmaku getMatchedDamaku(float x, float y) {
        for (BaseDanmaku danmaku : showingDanmakus) {
            if (danmaku.getShowState() == BaseDanmaku.ShowState.STATE_SHOWING) {
                if (x >= danmaku.getScrollX() && x <= danmaku.getScrollX() + danmaku.getWidth()
                        && y >= danmaku.getScrollY() && y <= danmaku.getScrollY() + danmaku.getHeight()) {
                    return danmaku;
                }
            }
        }

        return null;
    }
}

