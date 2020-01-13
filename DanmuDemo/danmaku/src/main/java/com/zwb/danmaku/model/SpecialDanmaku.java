package com.zwb.danmaku.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.zwb.danmaku.DanmakuView;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/26 14:36.
 * @ desc: 特殊弹幕--定点
 * 显示固定位置 逐渐变淡直至消失
 * 此类弹幕的速度只与显示时间有关，不要通过外部设置速度
 **/
public class SpecialDanmaku extends BaseDanmaku {
    private long firstShowTime;      //第一次显示的时间

    @Override
    public DanmakuType getType() {
        return DanmakuType.TYPE_SPECIAL;
    }

    @Override
    public void updatePosition() {
        // 已经显示过的不需要更新位置
        if (getShowState() != ShowState.STATE_GONE) {
            if (firstShowTime == 0) {
                firstShowTime = System.currentTimeMillis();
            }
            checkSpeed();
            setAlpha((int) (getAlpha() - getSpeed()));
        }
    }

    @Override
    public void updateShowType(int canvasWidth, int canvasHeight) {
        if (getScrollX() + getWidth() <= 0
                || getScrollX() >= canvasWidth
                || getScrollY() + getHeight() <= 0
                || getScrollY() >= canvasHeight
                || getAlpha() <= AlphaValue.TRANSPARENT) {
            setShowState(ShowState.STATE_GONE);
            firstShowTime = 0;
        } else {
            setShowState(ShowState.STATE_SHOWING);
        }
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, @NonNull Paint shadowPaint, int canvasWidth, int canvasHeight) {
//        canvas.drawRect(getScrollX(),getScrollY(),getScrollX()+ getWidth(),getScrollY() + getHeight(),shadowPaint);
        canvas.drawText(getText(), getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop() + getTextHeight(), shadowPaint);
        canvas.drawText(getText(), getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop() + getTextHeight(), textPaint);
    }

    private void checkSpeed() {
        if (firstShowTime != 0 && System.currentTimeMillis() - firstShowTime >= getDuration() && getSpeed() == 0) {
            if (getDuration() > 0 && getDisappearDuration() > 0) {
                // 显示时间一过，设置600毫秒之内消失
                setSpeed((AlphaValue.MAX * 1.0f * DanmakuView.REFRESH_TIME / getDisappearDuration()));
            } else {
                setSpeed(AlphaValue.MAX);
            }
        }
    }
}
