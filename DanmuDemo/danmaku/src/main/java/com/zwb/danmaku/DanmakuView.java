package com.zwb.danmaku;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zwb.danmaku.helper.BaseConfig;
import com.zwb.danmaku.helper.DrawHelper;
import com.zwb.danmaku.model.AlphaValue;
import com.zwb.danmaku.model.BaseDanmaku;

import java.util.ArrayList;
import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/24 10:09.
 * @ desc: 弹幕
 **/
public class DanmakuView extends View {

    private Paint textPaint;                                           // 文字画笔
    private Paint textShadowPaint;                                     // 文字阴影画笔
    private int textSize = 24;                                         // 默认文字大小
    private int textColor = Color.WHITE;                               // 默认文字颜色
    private int textShadowColor = Color.TRANSPARENT;                   // 默认阴影颜色
    private int textShadowWidth = 0;                                   // 默认阴影宽度
    private int shadowStyle = BaseDanmaku.SHADOW_STYLE_LAYER;          // 阴影类型

    private DanmakuState mPendingState = DanmakuState.STOP;             // 弹幕预状态
    private DanmakuState mCurrentState = DanmakuState.STOP;             // 弹幕当前状态
    private float speed = 2;                                            // 弹幕变化速度
    private int maxTrajectoryCount = Integer.MAX_VALUE;                 // 最大弹道数量
    private int maxRepeatCount = -1;                                    // 最大重试次数 -1 表示无限循环
    private int repeatCount = 0;                                        // 最大重试次数 -1 表示无限循环
    private float trajectoryMargin = 20;                               // 轨道直接的间距
    private static final long REFRESH_TIME = 14;                        // 每14毫秒刷新一次布局 接近16.6; 太小了增加处理负担，无意义
    private float den;                                                  // 像素密度
    private List<BaseDanmaku> danmukus = new ArrayList<>();            // 数据源

    // 特殊弹幕
    private long interval = 1000;                                       // 显示的时间间隔
    private int countLimit = 10;                                        // 显示的弹幕数量

    private boolean isAttach;                                           // 是否关联窗口
    private boolean isVisible;                                          // 是否可见

    private DrawHelper drawHelper;
    private HandlerThread mHandlerThread;
    private Handler drawHandler;
    private final static int HANDLER_WHAT_START_DRAW = 0X11;
    private final static int HANDLER_WHAT_STOP_DRAW = 0X12;

    public DrawHelper getDrawHelper() {
        if (drawHelper == null) {
            drawHelper = new DrawHelper();
        }
        return drawHelper;
    }

    public DanmakuView(Context context) {
        super(context);
        initParams(context, null, 0);
    }

    public DanmakuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams(context, attrs, 0);
    }

    public DanmakuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams(context, attrs, defStyleAttr);
    }

    /**
     * 初始化参数
     */
    private void initParams(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        den = getContext().getResources().getDisplayMetrics().density;

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DanmakuView, defStyleAttr, 0);
        try {
            textColor = a.getColor(R.styleable.DanmakuView_danmaku_textColor, textColor);
            textShadowColor = a.getColor(R.styleable.DanmakuView_danmaku_textShadowColor, textShadowColor);
            textSize = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_textSize, (int) (den * textSize));
            textShadowWidth = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_textShadowWidth, (int) (textShadowWidth * den));
            trajectoryMargin = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_trajectoryMargin, (int) (trajectoryMargin * den));
            maxTrajectoryCount = a.getInteger(R.styleable.DanmakuView_danmaku_trajectoryCount, 2);// 先默认只有两个弹道
            maxRepeatCount = a.getInteger(R.styleable.DanmakuView_danmaku_repeatCount, -1);// -1表示无限循环
            interval = a.getInteger(R.styleable.DanmakuView_danmaku_interval, 1000);// 下一个特殊弹幕显示的间隔时间
            countLimit = a.getInteger(R.styleable.DanmakuView_danmaku_countLimit, 10);// 特殊弹幕允许在屏幕上显示的总数量
            speed = a.getFloat(R.styleable.DanmakuView_danmaku_speed, speed * den);
            shadowStyle = a.getInt(R.styleable.DanmakuView_danmaku_shadowStyle, shadowStyle);
        } finally {
            a.recycle();
        }
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);

        textShadowPaint = new Paint(textPaint);
        textShadowPaint.setStyle(Paint.Style.STROKE);

        BaseConfig baseConfig = new BaseConfig();
        baseConfig.setSpeed(speed)
                .setTextColor(textColor)
                .setTextShadowColor(textShadowColor)
                .setTextShadowWidth(textShadowWidth)
                .setTextSize(textSize)
                .setShadowStyle(shadowStyle);

        getDrawHelper().setDen(den)
                .setBaseConfig(baseConfig)
                .setTrajectoryMargin(trajectoryMargin)
                .setMaxTrajectoryCount(maxTrajectoryCount)
                .setInterval(interval)
                .setCountLimit(countLimit);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getMeasuredHeight() != 0 && getMeasuredWidth() != 0) {
            getDrawHelper().onDraw(canvas, textPaint, textShadowPaint, getMeasuredWidth(), getMeasuredHeight());
        }
    }


    /**
     * 设置弹道的数量
     *
     * @param maxTrajectoryCount 弹道的数量
     */
    public DanmakuView setMaxTrajectoryCount(int maxTrajectoryCount) {
        if (maxTrajectoryCount > 0) {
            this.maxTrajectoryCount = maxTrajectoryCount;
            getDrawHelper().setMaxTrajectoryCount(maxTrajectoryCount);
        }
        return this;
    }

    /**
     * 设置最大重试次数
     *
     * @param maxRepeatCount
     */
    public DanmakuView setMaxRepeatCount(int maxRepeatCount) {
        this.maxRepeatCount = maxRepeatCount;
        return this;
    }

    public DanmakuView setInterval(long interval) {
        if (interval > 0) {
            this.interval = interval;
            getDrawHelper().setInterval(interval);
        }
        return this;
    }

    public DanmakuView setCountLimit(int countLimit) {
        if (countLimit > 0) {
            this.countLimit = countLimit;
            getDrawHelper().setCountLimit(countLimit);
        }
        return this;
    }

    public void setShadowStyle(int shadowStyle) {
        this.shadowStyle = this.shadowStyle;
    }

    /**
     * 设置需要展示的弹幕
     *
     * @param danmukus 弹幕列表
     */
    public synchronized void setDanmukus(List<BaseDanmaku> danmukus) {
        if (danmukus != null) {
            this.danmukus.clear();
            repeatCount = 0;
            this.danmukus.addAll(danmukus);
            getDrawHelper().setDanmakus(danmukus);
        }
    }

    public synchronized void addDanmuku(BaseDanmaku info) {
        if (info != null) {
            danmukus.add(info);
            getDrawHelper().addDanmaku(info);
        }
    }

    public synchronized void addDanmukus(List<BaseDanmaku> danmukus) {
        if (danmukus != null && !danmukus.isEmpty()) {
            this.danmukus.addAll(danmukus);
            getDrawHelper().addDanmakus(danmukus);
        }
    }

    public void stop() {
        mPendingState = DanmakuState.STOP;
        pause();
    }


    public void start() {
        mPendingState = DanmakuState.START;
        resume();
    }

    public void pause() {
        mCurrentState = DanmakuState.STOP;
        sendStop();
        quitHandlerThread();
    }

    public void resume() {
        if (!isAttach || !isVisible) {
            return;
        }
        if (mPendingState == DanmakuState.START) {
            mCurrentState = DanmakuState.START;
            sendStop();
            prepare();
            sendStart();
        }
    }

    public synchronized void clear() {
        repeatCount = 0;
        mPendingState = DanmakuState.STOP;
        mCurrentState = DanmakuState.STOP;
        stop();
        danmukus.clear();
        getDrawHelper().clear();
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttach = true;
        resume();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        isVisible = visibility == View.VISIBLE;
        if (isVisible) {
            resume();
        } else {
            pause();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttach = false;
        pause();
    }


    private Looper getLooper() {
        quitHandlerThread();
        mHandlerThread = new HandlerThread("Danmaku");
        mHandlerThread.start();
        return mHandlerThread.getLooper();
    }

    private void quitHandlerThread() {
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
            mHandlerThread = null;
        }
    }

    private void prepare() {
        if (drawHandler != null) {
            drawHandler.removeMessages(HANDLER_WHAT_START_DRAW);
            drawHandler.removeMessages(HANDLER_WHAT_STOP_DRAW);
        }
        drawHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HANDLER_WHAT_START_DRAW:
                        if (getContext() == null) {
                            return;
                        }
                        if (getContext() instanceof Activity) {
                            Activity activity = (Activity) getContext();
                            if (activity.isFinishing()) {
                                return;
                            }
                        }
                        sendStart();
                        if (mCurrentState == DanmakuState.START && textPaint != null
                                && getMeasuredHeight() != 0 && getMeasuredWidth() != 0) {
                            if (!isAllGone()) {
                                if (!danmukus.isEmpty()) {
                                    getDrawHelper().onDrawPrepared(textPaint, textShadowPaint, getMeasuredWidth(), getMeasuredHeight());
                                    postInvalidate();
                                }
                            } else {
                                if (maxRepeatCount < 0) {
                                    // 无限循环 重置显示状态
                                    rePlay();
                                } else {
                                    repeatCount++;
                                    if (repeatCount <= maxRepeatCount) {
                                        rePlay();
                                    }
                                }
                            }
                        }
                        break;
                    case HANDLER_WHAT_STOP_DRAW:
                        break;
                }
            }
        };
    }

    private synchronized boolean isAllGone() {
        boolean flag = true;
        if (danmukus.isEmpty()) {
            return false;
        }
        for (BaseDanmaku danmaku : danmukus) {
            if (danmaku.getShowState() != BaseDanmaku.ShowState.STATE_GONE) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private synchronized void rePlay() {
        // todo 待优化 重播任务放入helper当中
        getDrawHelper().clear();
        for (BaseDanmaku danmaku : danmukus) {
            danmaku.setShowState(BaseDanmaku.ShowState.STATE_NEVER_SHOWED);
            danmaku.setAlpha(AlphaValue.MAX);
            if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SPECIAL) {
                danmaku.setSpeed(0);
            }
        }
        getDrawHelper().setDanmakus(danmukus);
    }

    private synchronized void sendStart() {
        if (drawHandler != null && mHandlerThread != null) {
            drawHandler.removeMessages(HANDLER_WHAT_START_DRAW);
            drawHandler.removeMessages(HANDLER_WHAT_STOP_DRAW);
            drawHandler.sendEmptyMessageDelayed(HANDLER_WHAT_START_DRAW, REFRESH_TIME);
        }
    }

    private void sendStop() {
        if (drawHandler != null && mHandlerThread != null) {
            drawHandler.removeMessages(HANDLER_WHAT_START_DRAW);
            drawHandler.removeMessages(HANDLER_WHAT_STOP_DRAW);
            drawHandler.sendEmptyMessage(HANDLER_WHAT_STOP_DRAW);
        }
    }
}
