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

import com.zwb.danmaku.helper.DrawHelper;
import com.zwb.danmaku.model.BaseDanmaku;

import java.util.ArrayList;
import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/24 10:09.
 * @ desc: 弹幕
 **/
public class DanmakuView extends View {

    private Paint mTextPaint;                                           // 文字画笔
    private int mTextSize = 24;                                         // 默认文字大小
    private int mTextColor = Color.WHITE;                               // 默认文字颜色
    private int mTextShadowColor = Color.TRANSPARENT;                   // 默认阴影颜色
    private int mTextShadowWidth = 0;                                   // 默认阴影宽度

    private DanmakuState mPendingState = DanmakuState.STOP;             // 弹幕预状态
    private DanmakuState mCurrentState = DanmakuState.STOP;             // 弹幕当前状态
    private float speed = 2;                                            // 弹幕变化速度
    private int maxTrajectoryCount = Integer.MAX_VALUE;                 // 最大弹道数量
    private int maxRepeatCount = -1;                                    // 最大重试次数 -1 表示无限循环
    private int repeatCount = 0;                                        // 最大重试次数 -1 表示无限循环
    private float mTrajectoryMargin = 20;                               // 轨道直接的间距
    public static final long REFRESH_TIME = 17;                         // 每17毫秒刷新一次布局 必须大于16.6 小于这个数无意义，且需要时间累加的弹幕会显示异常
    private float den;                                                  // 像素密度
    private List<BaseDanmaku> mDanmukus = new ArrayList<>();            // 数据源

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
            mTextColor = a.getColor(R.styleable.DanmakuView_danmaku_textColor, mTextColor);
            mTextShadowColor = a.getColor(R.styleable.DanmakuView_danmaku_textShadowColor, mTextShadowColor);
            mTextSize = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_textSize, (int) (den * mTextSize));
            mTextShadowWidth = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_textShadowWidth, (int) (mTextShadowWidth * den));
            mTrajectoryMargin = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_trajectoryMargin, (int) (mTrajectoryMargin * den));
            maxTrajectoryCount = a.getInteger(R.styleable.DanmakuView_danmaku_trajectoryCount, 2);// 先默认只有两个弹道
            maxRepeatCount = a.getInteger(R.styleable.DanmakuView_danmaku_repeatCount, -1);// -1表示无限循环
            interval = a.getInteger(R.styleable.DanmakuView_danmaku_interval, 1000);// 下一个特殊弹幕显示的间隔时间
            countLimit = a.getInteger(R.styleable.DanmakuView_danmaku_countLimit, 10);// 特殊弹幕允许在屏幕上显示的总数量
            speed = a.getFloat(R.styleable.DanmakuView_danmaku_speed, speed * den);
        } finally {
            a.recycle();
        }
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setDither(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);//默认颜色白色
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setShadowLayer(mTextShadowWidth, 0, 0, mTextShadowColor);

        getDrawHelper().setSpeed(speed).setTrajectoryMargin(mTrajectoryMargin)
                .setMaxTrajectoryCount(maxTrajectoryCount).setDen(den)
                .setInterval(interval).setCountLimit(countLimit);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getMeasuredHeight() != 0 && getMeasuredWidth() != 0) {
            getDrawHelper().onDraw(canvas, mTextPaint, getMeasuredWidth(), getMeasuredHeight());
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

    /**
     * 设置需要展示的弹幕
     *
     * @param danmukus 弹幕列表
     */
    public synchronized void setDanmukus(List<BaseDanmaku> danmukus) {
        if (danmukus != null) {
            mDanmukus.clear();
            repeatCount = 0;
            mDanmukus.addAll(danmukus);
            getDrawHelper().setDanmakus(danmukus);
        }
    }

    public synchronized void addDanmuku(BaseDanmaku info) {
        if (info != null) {
            mDanmukus.add(info);
            getDrawHelper().addDanmaku(info);
        }
    }

    public synchronized void addDanmukus(List<BaseDanmaku> danmukus) {
        if (danmukus != null && !danmukus.isEmpty()) {
            mDanmukus.addAll(danmukus);
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
        mDanmukus.clear();
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
                        if (mCurrentState == DanmakuState.START && mTextPaint != null
                                && getMeasuredHeight() != 0 && getMeasuredWidth() != 0) {
                            if (!isAllGone()) {
                                if (!mDanmukus.isEmpty()) {
                                    getDrawHelper().onDrawPrepared(mTextPaint, getMeasuredWidth(), getMeasuredHeight());
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
        if (mDanmukus.isEmpty()) {
            return false;
        }
        for (BaseDanmaku danmaku : mDanmukus) {
            if (danmaku.getShowState() != BaseDanmaku.ShowState.STATE_GONE) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private synchronized void rePlay() {
        getDrawHelper().clear();
        for (BaseDanmaku danmaku : mDanmukus) {
            danmaku.setShowState(BaseDanmaku.ShowState.STATE_NEVER_SHOWED);
        }
        getDrawHelper().setDanmakus(mDanmukus);
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
