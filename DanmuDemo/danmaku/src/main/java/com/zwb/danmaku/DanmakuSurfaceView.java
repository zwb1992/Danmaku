package com.zwb.danmaku;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.zwb.danmaku.helper.DanmakuTouchHelper;
import com.zwb.danmaku.helper.DrawHelper;
import com.zwb.danmaku.helper.DrawState;
import com.zwb.danmaku.model.BaseConfig;
import com.zwb.danmaku.model.BaseDanmaku;

import java.util.ArrayList;
import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/24 10:09.
 * @ desc: 弹幕 使用surfaceview实现
 **/
public class DanmakuSurfaceView extends SurfaceView implements IDanmakuView, SurfaceHolder.Callback {

    private Paint textPaint;                                           // 文字画笔
    private Paint textShadowPaint;                                     // 文字阴影画笔
    private Paint bgPaint;                                             // 背景画笔
    private int textSize = 24;                                         // 默认文字大小
    private int textColor = Color.WHITE;                               // 默认文字颜色
    private int textShadowColor = Color.TRANSPARENT;                   // 默认阴影颜色
    private int canvasColor = Color.WHITE;                             // 默认画布颜色
    private int textShadowWidth = 0;                                   // 默认阴影宽度
    private int lineSpacingExtra = 0;                                  // 文字之间的上下距离（多行的情况下）
    private int maxWidth = -1;                                         // 弹幕的最大宽度 -1代表不限制大小
    private int shadowStyle = BaseDanmaku.SHADOW_STYLE_LAYER;          // 阴影类型

    private DanmakuState mPendingState = DanmakuState.STOP;             // 弹幕预状态
    private DanmakuState mCurrentState = DanmakuState.STOP;             // 弹幕当前状态
    private float speed = 2;                                            // 弹幕变化速度
    private int maxTrajectoryCount = Integer.MAX_VALUE;                 // 最大弹道数量
    private int maxRepeatCount = -1;                                    // 最大重试次数 -1 表示无限循环
    private int repeatCount = 0;                                        // 最大重试次数 -1 表示无限循环
    private float trajectoryMargin = 20;                                // 轨道直接的间距
    private static final long REFRESH_TIME = 16;                        // 每16毫秒刷新一次布局 接近16.6; 太小了增加处理负担，无意义
    private float den;                                                  // 像素密度
    private List<BaseDanmaku> danmukus = new ArrayList<>();             // 数据源
    private boolean isCanvasTransparent; // 画布是否透明

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
    private OnDanmakuClickListener onDanmakuClickListener;
    private DanmakuTouchHelper mTouchHelper;

    private SurfaceHolder mSurfaceHolder;

    @Override
    public DrawHelper getDrawHelper() {
        if (drawHelper == null) {
            drawHelper = new DrawHelper();
        }
        return drawHelper;
    }

    public DanmakuSurfaceView(Context context) {
        super(context);
        initParams(context, null, 0);
    }

    public DanmakuSurfaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams(context, attrs, 0);
    }

    public DanmakuSurfaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            canvasColor = a.getColor(R.styleable.DanmakuView_danmaku_canvasColor, canvasColor);
            textSize = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_textSize, (int) (den * textSize));
            textShadowWidth = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_textShadowWidth, (int) (textShadowWidth * den));
            trajectoryMargin = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_trajectoryMargin, (int) (trajectoryMargin * den));
            lineSpacingExtra = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_lineSpacingExtra, (int) (lineSpacingExtra * den));
            maxWidth = a.getDimensionPixelOffset(R.styleable.DanmakuView_danmaku_maxWidth, maxWidth);
            maxTrajectoryCount = a.getInteger(R.styleable.DanmakuView_danmaku_trajectoryCount, 2);// 先默认只有两个弹道
            maxRepeatCount = a.getInteger(R.styleable.DanmakuView_danmaku_repeatCount, -1);// -1表示无限循环
            interval = a.getInteger(R.styleable.DanmakuView_danmaku_interval, 1000);// 下一个特殊弹幕显示的间隔时间
            countLimit = a.getInteger(R.styleable.DanmakuView_danmaku_countLimit, 10);// 特殊弹幕允许在屏幕上显示的总数量
            speed = a.getFloat(R.styleable.DanmakuView_danmaku_speed, speed * den);
            shadowStyle = a.getInt(R.styleable.DanmakuView_danmaku_shadowStyle, shadowStyle);
            isCanvasTransparent = a.getBoolean(R.styleable.DanmakuView_danmaku_isCanvasTransparent, isCanvasTransparent);
        } finally {
            a.recycle();
        }
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);

        bgPaint = new Paint(textPaint);

        textShadowPaint = new Paint(textPaint);
        textShadowPaint.setStyle(Paint.Style.STROKE);

        BaseConfig baseConfig = new BaseConfig();
        baseConfig.setSpeed(speed)
                .setTextColor(textColor)
                .setTextShadowColor(textShadowColor)
                .setTextShadowWidth(textShadowWidth)
                .setTextSize(textSize)
                .setShadowStyle(shadowStyle)
                .setMaxWidth(maxWidth)
                .setLineSpacingExtra(lineSpacingExtra);

        getDrawHelper().setDen(den)
                .setBaseConfig(baseConfig)
                .setTrajectoryMargin(trajectoryMargin)
                .setMaxTrajectoryCount(maxTrajectoryCount)
                .setInterval(interval)
                .setCountLimit(countLimit);

        mTouchHelper = new DanmakuTouchHelper(this);

        setZOrderMediaOverlay(true);
        setWillNotCacheDrawing(true);
        setDrawingCacheEnabled(false);
        setWillNotDraw(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setZOrderOnTop(isCanvasTransparent);
        mSurfaceHolder.setFormat(isCanvasTransparent ? PixelFormat.TRANSLUCENT : PixelFormat.TRANSPARENT);

    }

    private void doDraw() {
        if (mSurfaceHolder == null) {
            return;
        }
        Canvas mCanvas = mSurfaceHolder.lockCanvas();
        if (getMeasuredHeight() != 0 && getMeasuredWidth() != 0 && mCanvas != null) {
            if (!isCanvasTransparent) {
                mCanvas.drawColor(canvasColor);
            } else {
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            getDrawHelper().onDraw(mCanvas, textPaint, textShadowPaint, bgPaint, getMeasuredWidth(), getMeasuredHeight());
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public IDanmakuView setCanvasTransparent(boolean isTransparent) {
        this.isCanvasTransparent = isTransparent;
        // 是否绘制在最顶层 在它上面的view均显示在canvas的绘制的内容下面
        setZOrderOnTop(isTransparent);
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setFormat(isTransparent ? PixelFormat.TRANSLUCENT : PixelFormat.TRANSPARENT);
        }
        doDraw();
        return this;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        doDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public DanmakuSurfaceView setMaxTrajectoryCount(int maxTrajectoryCount) {
        if (maxTrajectoryCount > 0) {
            this.maxTrajectoryCount = maxTrajectoryCount;
            getDrawHelper().setMaxTrajectoryCount(maxTrajectoryCount);
        }
        return this;
    }

    @Override
    public DanmakuSurfaceView setOffScreenLimit(int offScreenLimit) {
        getDrawHelper().setOffScreenLimit(offScreenLimit);
        return this;
    }

    @Override
    public DanmakuSurfaceView setMaxRepeatCount(int maxRepeatCount) {
        this.maxRepeatCount = maxRepeatCount;
        return this;
    }

    @Override
    public DanmakuSurfaceView setInterval(long interval) {
        if (interval > 0) {
            this.interval = interval;
            getDrawHelper().setInterval(interval);
        }
        return this;
    }

    @Override
    public DanmakuSurfaceView setCountLimit(int countLimit) {
        if (countLimit > 0) {
            this.countLimit = countLimit;
            getDrawHelper().setCountLimit(countLimit);
        }
        return this;
    }

    @Override
    public void setShadowStyle(int shadowStyle) {
        this.shadowStyle = this.shadowStyle;
    }

    @Override
    public synchronized void setDanmukus(List<BaseDanmaku> danmukus) {
        if (danmukus != null) {
            this.danmukus.clear();
            repeatCount = 0;
            this.danmukus.addAll(danmukus);
            getDrawHelper().setDanmakus(danmukus);
        }
    }

    @Override
    public synchronized void addDanmuku(BaseDanmaku info) {
        if (info != null) {
            danmukus.add(info);
            getDrawHelper().addDanmaku(info);
        }
    }

    @Override
    public void addDanmuku(BaseDanmaku info, boolean addFirst) {
        if (info != null) {
            danmukus.add(info);
            getDrawHelper().addDanmaku(info, addFirst);
        }
    }

    @Override
    public synchronized void addDanmukus(List<BaseDanmaku> danmukus) {
        if (danmukus != null && !danmukus.isEmpty()) {
            this.danmukus.addAll(danmukus);
            getDrawHelper().addDanmakus(danmukus);
        }
    }

    @Override
    public void stop() {
        mPendingState = DanmakuState.STOP;
        pause();
    }

    @Override
    public void start() {
        mPendingState = DanmakuState.START;
        resume();
    }

    @Override
    public void pause() {
        mCurrentState = DanmakuState.STOP;
        sendStop();
        quitHandlerThread();
    }

    @Override
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

    /**
     * 是否正在播放
     *
     * @return false
     */
    public boolean isPlaying() {
        return mCurrentState == DanmakuState.START;
    }

    /**
     * 获取当前的播放状态
     *
     * @return state
     */
    public DanmakuState getState() {
        return mCurrentState;
    }

    @Override
    public synchronized void clear() {
        repeatCount = 0;
        mPendingState = DanmakuState.STOP;
        mCurrentState = DanmakuState.STOP;
        stop();
        danmukus.clear();
        getDrawHelper().clear();
        doDraw();
    }

    @Override
    public void setOnDanmakuClickListener(OnDanmakuClickListener listener) {
        onDanmakuClickListener = listener;
    }

    @Override
    public OnDanmakuClickListener getOnDanmakuClickListener() {
        return onDanmakuClickListener;
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
                            int state = drawHelper.getState();
                            if (drawHelper.getState() != DrawState.STATE_GONE) {
                                if (state != DrawState.STATE_EMPTY) {
                                    getDrawHelper().onDrawPrepared(getContext(), textPaint, textShadowPaint, getMeasuredWidth(), getMeasuredHeight());
                                    doDraw();
                                }
                            } else {
                                if (maxRepeatCount < 0) {
                                    // 无限循环 重置显示状态
                                    drawHelper.rePlay();
                                } else {
                                    repeatCount++;
                                    if (repeatCount <= maxRepeatCount) {
                                        drawHelper.rePlay();
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

    @Override
    public BaseDanmaku getTouchMatchedDamaku(float x, float y) {
        if (getDrawHelper() != null) {
            return getDrawHelper().getMatchedDamaku(x, y);
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTouchHelper.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }
}
