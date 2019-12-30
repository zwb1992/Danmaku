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
    private Paint mTextShadowPaint;                                     // 文字阴影画笔
    private int mTextSize = 24;                                         // 默认文字大小
    private int mTextColor = Color.WHITE;                               // 默认文字颜色
    private int mTextShadowColor = Color.BLACK;                         // 默认阴影颜色
    private int mTextShadowWidth = 1;                                   // 默认阴影宽度

    private DanmakuState mPendingState = DanmakuState.STOP;             // 弹幕预状态
    private DanmakuState mCurrentState = DanmakuState.STOP;             // 弹幕当前状态
    private float speed = 2;                                            // 弹幕变化速度
    private int maxTrajectoryCount = Integer.MAX_VALUE;                 // 最大弹道数量
    private float mTrajectoryMargin = 20;                               // 轨道直接的间距
    private static final long REFRESH_TIME = 20;                        // 每20毫秒刷新一次布局
    private float den;                                                  // 像素密度
    private List<BaseDanmaku> mDanmukus = new ArrayList<>();            // 数据源

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

        mTextShadowPaint = new Paint(mTextPaint);
        mTextShadowPaint.setColor(mTextShadowColor);//默认颜色白色
        mTextShadowPaint.setStyle(Paint.Style.STROKE);
        mTextShadowPaint.setStrokeWidth(mTextShadowWidth);

        getDrawHelper().setSpeed(speed).setTrajectoryMargin(mTrajectoryMargin).setMaxTrajectoryCount(maxTrajectoryCount).setDen(den);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getMeasuredHeight() != 0 && getMeasuredWidth() != 0) {
            getDrawHelper().onDrawPrepared(mTextPaint, mTextShadowPaint, getMeasuredWidth(), getMeasuredHeight());
            getDrawHelper().onDraw(canvas, mTextPaint, mTextShadowPaint, getMeasuredWidth(), getMeasuredHeight());
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
     * 设置需要展示的弹幕
     *
     * @param danmukus 弹幕列表
     */
    public void setDanmukus(List<BaseDanmaku> danmukus) {
        if (danmukus != null) {
            mDanmukus.clear();
            mDanmukus.addAll(danmukus);
            getDrawHelper().setDanmakus(danmukus);
        }
    }

    public void addDanmuku(BaseDanmaku info) {
        if (info != null) {
            mDanmukus.add(info);
            getDrawHelper().addDanmaku(info);
        }
    }

    public void addDanmukus(List<BaseDanmaku> danmukus) {
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

    public void clear() {
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
                        if (mCurrentState == DanmakuState.START
                                && mTextPaint != null && mTextShadowPaint != null
                                && getMeasuredHeight() != 0 && getMeasuredWidth() != 0) {
                            getDrawHelper().onDrawPrepared(mTextPaint, mTextShadowPaint, getMeasuredWidth(), getMeasuredHeight());
                            postInvalidate();
                        }
                        sendStart();
                        break;
                    case HANDLER_WHAT_STOP_DRAW:
                        break;
                }
            }
        };
    }

    private void sendStart() {
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
