package com.zwb.danmaku.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/26 14:36.
 * @ desc: 从左向右滑动
 **/
public class L2RDanmaku extends BaseDanmaku {

    @Override
    public DanmakuType getType() {
        return DanmakuType.TYPE_SCROLL_LR;
    }

    @Override
    public void updatePosition() {
        // 已经显示过的不需要更新位置
        if (getShowState() != ShowState.STATE_GONE) {
            setScrollX(getScrollX() + getSpeed());
        }
    }

    @Override
    public void updateShowType(int canvasWidth, int canvasHeight) {
        if (getScrollX() + getWidth() <= 0) {
            setShowState(ShowState.STATE_NEVER_SHOWED);
        } else if (getScrollX() >= canvasWidth
                || getScrollY() + getHeight() <= 0
                || getScrollY() >= canvasHeight) {
            setShowState(ShowState.STATE_GONE);
        } else {
            setShowState(ShowState.STATE_SHOWING);
        }
    }

    @Override
    public void onDrawContent(@NonNull Canvas canvas, @NonNull Paint textPaint, int canvasWidth, int canvasHeight) {
//        canvas.drawRect(getScrollX(),getScrollY(),getScrollX()+ getWidth(),getScrollY() + getHeight(),shadowPaint);
        canvas.drawText(getText(), getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop() + getTextHeight(), textPaint);
    }

    @Override
    public void onDrawShadow(@NonNull Canvas canvas, @NonNull Paint mTextShadowPaint, int canvasWidth, int canvasHeight) {
        canvas.drawText(getText(), getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop() + getTextHeight(), mTextShadowPaint);
    }
}
