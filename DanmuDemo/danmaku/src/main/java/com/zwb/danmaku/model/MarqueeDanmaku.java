package com.zwb.danmaku.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/26 14:36.
 * @ desc: 跑马灯弹幕，默认从右向左 并且屏幕居中 只有一条轨道
 **/
public class MarqueeDanmaku extends BaseDanmaku {

    @Override
    public DanmakuType getType() {
        return DanmakuType.TYPE_MARQUEE;
    }

    @Override
    public void updatePosition() {
        // 已经显示过的不需要更新位置
        if (getShowState() != ShowState.STATE_GONE) {
            setScrollX(getScrollX() - getSpeed());
        }
    }

    @Override
    public void updateShowType(int canvasWidth, int canvasHeight) {
        if (getScrollX() + getWidth() <= 0
                || getScrollY() + getHeight() <= 0
                || getScrollY() >= canvasHeight) {
            setShowState(ShowState.STATE_GONE);
        } else if (getScrollX() >= canvasWidth) {
            setShowState(ShowState.STATE_NEVER_SHOWED);
        } else {
            setShowState(ShowState.STATE_SHOWING);
        }
    }

    @Override
    public void onDrawContent(@NonNull Canvas canvas, @NonNull Paint textPaint, int canvasWidth, int canvasHeight) {
        textPaint.setTextAlign(Paint.Align.CENTER);
        // 上下居中显示
        float centerX = getScrollX() + getPaddingLeft() + getTextWidth() / 2.0f;
        float centerY = canvasHeight / 2.0f + (getPaddingTop() - getPaddingBottom()) / 2;
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

        int baseLineY = (int) (centerY - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        // 单行显示
        canvas.drawText(getText(), centerX, baseLineY, textPaint);

    }

    @Override
    public void onDrawShadow(@NonNull Canvas canvas, @NonNull Paint mTextShadowPaint, int canvasWidth, int canvasHeight) {
        mTextShadowPaint.setTextAlign(Paint.Align.CENTER);
        // 上下居中显示
        float centerX = getScrollX() + getPaddingLeft() + getTextWidth() / 2.0f;
        float centerY = canvasHeight / 2.0f + (getPaddingTop() - getPaddingBottom()) / 2;
        Paint.FontMetrics fontMetrics = mTextShadowPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

        int baseLineY = (int) (centerY - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        // 单行显示
        canvas.drawText(getText(), centerX, baseLineY, mTextShadowPaint);
    }


    @Override
    public void onDrawBackGround(@NonNull Canvas canvas, @NonNull Paint mBackGroundPaint, int canvasWidth, int canvasHeight) {
        // 上下居中显示
        float scrollY = (canvasHeight - getHeight()) / 2.0f;
        if (getBackgroundBitmap() != null) {
            bgDestRect.set(getScrollX() - getShadowWidth(), scrollY, getScrollX() + getWidth() - getShadowWidth(), scrollY + getHeight());
            canvas.drawBitmap(getBackgroundBitmap(), bgSrcRect, bgDestRect, mBackGroundPaint);
        }
    }
}
