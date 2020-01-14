package com.zwb.danmaku.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/26 14:08.
 * @ desc:
 **/
public abstract class BaseDanmaku {

    /**
     * 弹幕类型
     */
    public enum DanmakuType {
        TYPE_SCROLL_RL,         // 从右向左
        TYPE_SCROLL_LR,         // 从左向右
        TYPE_SCROLL_TB,         // 从上向下
        TYPE_SCROLL_BT,         // 从下向上
        TYPE_SPECIAL            // 特殊弹幕
    }

    /**
     * 显示类型
     */
    public enum ShowState {
        STATE_NEVER_SHOWED,  // 从未显示过
        STATE_SHOWING,       // 正在显示
        STATE_GONE           // 已经显示过
    }

    public abstract DanmakuType getType();

    public void startDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, int canvasWidth, int canvasHeight) {
        if (!isInit()) {
            return;
        }
        updatePosition();
        updateShowType(canvasWidth, canvasHeight);
        // 只绘制可见的
        if (isVisible()) {
            updatePaint(textPaint);
            onDraw(canvas, textPaint, canvasWidth, canvasHeight);
        }
    }

    /**
     * @param canvas       画布
     * @param textPaint    文字画笔
     * @param canvasWidth  画布宽度
     * @param canvasHeight 画布高度
     */
    public abstract void onDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, int canvasWidth, int canvasHeight);

    /**
     * 更新位置状态信息
     */
    public abstract void updatePosition();

    /**
     * 更新显示状态
     *
     * @param canvasWidth  画布宽度
     * @param canvasHeight 画布高度
     */
    public abstract void updateShowType(int canvasWidth, int canvasHeight);

    private void updatePaint(@NonNull Paint textPaint) {
        try {
            if (getTextSize() > 0) {
                textPaint.setTextSize(getTextSize());
            }
            if (getTextColor() != 0) {
                textPaint.setColor(getTextColor());
            }

            if (getShadowWidth() > 0 && getShadowColor() != 0) {
                textPaint.setShadowLayer(getShadowWidth(), 0, 0, getShadowColor());
            } else {
                textPaint.clearShadowLayer();
            }
            textPaint.setAlpha(getAlpha());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String text;                        // 弹幕内容

    private float textSize;                     // 弹幕字体大小

    private int textColor;                       // 弹幕字体颜色

    private float scrollX = 0;                  // x轴的偏移量 随着时间的推移不断变化

    private float scrollY = 0;                  // y轴的偏移量 随着时间的推移不断变化

    private float originScrollX = 0;            // x轴的原始偏移量

    private float originScrollY = 0;            // y轴的原始偏移量

    private float speed = 0;                    // 移动的速度（渐变速度）

    private int textHeight;                     // 弹幕文字的高

    private int textWidth;                      // 弹幕文字的宽

    private float offset = 0;                  // 初始位置的偏移量

    private float shadowWidth;                  // 阴影的宽度

    private int shadowColor;                    // 阴影的颜色

    private int paddingLeft;                    // 左边的内边距

    private int paddingRight;                   // 右边的内边距

    private int paddingTop;                     // 上面的内边距

    private int paddingBottom;                  // 底部的内边距

    private boolean isInit;                     // 是否初始化完成

    private ShowState showState = ShowState.STATE_NEVER_SHOWED;

    private int alpha = AlphaValue.MAX;                    // 透明度

    /**
     * 针对定点弹幕
     */
    private long duration;                      // 在屏幕显示停留的时间
    private long disappearDuration;             // 从显示到消失的时间--方便做动画   0的话就是直接消失

    public String getText() {
        return text;
    }

    public BaseDanmaku setText(String text) {
        this.text = text;
        return this;
    }

    public float getTextSize() {
        return textSize;
    }

    public BaseDanmaku setTextSize(float textSize) {
        this.textSize = textSize;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public BaseDanmaku setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public float getScrollX() {
        return scrollX;
    }

    public BaseDanmaku setScrollX(float scrollX) {
        this.scrollX = scrollX;
        return this;
    }

    public float getScrollY() {
        return scrollY;
    }

    public BaseDanmaku setScrollY(float scrollY) {
        this.scrollY = scrollY;
        return this;
    }

    public float getOriginScrollX() {
        return originScrollX;
    }

    public BaseDanmaku setOriginScrollX(float originScrollX) {
        this.originScrollX = originScrollX;
        return this;
    }

    public float getOriginScrollY() {
        return originScrollY;
    }

    public BaseDanmaku setOriginScrollY(float originScrollY) {
        this.originScrollY = originScrollY;
        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public BaseDanmaku setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public BaseDanmaku setTextHeight(int textHeight) {
        this.textHeight = textHeight;
        return this;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public BaseDanmaku setTextWidth(int textWidth) {
        this.textWidth = textWidth;
        return this;
    }

    public float getHeight() {
        return getTextHeight() + getPaddingBottom() + getPaddingTop() + +getShadowWidth() * 2;
    }


    public float getWidth() {
        return getTextWidth() + getPaddingLeft() + getPaddingRight() + getShadowWidth() * 2;
    }

    public float getOffset() {
        return offset;
    }

    public BaseDanmaku setOffset(float offset) {
        this.offset = offset;
        return this;
    }

    public float getShadowWidth() {
        return shadowWidth;
    }

    public BaseDanmaku setShadowWidth(float shadowWidth) {
        this.shadowWidth = shadowWidth;
        return this;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public BaseDanmaku setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        return this;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public BaseDanmaku setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public BaseDanmaku setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        return this;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public BaseDanmaku setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public BaseDanmaku setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        return this;
    }

    public boolean isInit() {
        return isInit;
    }

    public BaseDanmaku setInit(boolean init) {
        isInit = init;
        return this;
    }

    public boolean isVisible() {
        return showState == ShowState.STATE_SHOWING;
    }

    public boolean isGone() {
        return showState == ShowState.STATE_GONE;
    }

    public boolean isNeverShowed() {
        return showState == ShowState.STATE_NEVER_SHOWED;
    }

    public ShowState getShowState() {
        return showState;
    }

    public BaseDanmaku setShowState(ShowState showState) {
        this.showState = showState;
        return this;
    }

    public void initTextSize(Paint paint) {
        if (TextUtils.isEmpty(getText()) || paint == null) {
            return;
        }
        if (getTextSize() > 0) {
            paint.setTextSize(getTextSize());
        }
        Rect rect = new Rect();
        paint.getTextBounds(getText(), 0, getText().length(), rect);
        setTextWidth(rect.width()).setTextHeight(rect.height());
    }

    public long getDuration() {
        return duration;
    }

    public BaseDanmaku setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public long getDisappearDuration() {
        return disappearDuration;
    }

    public BaseDanmaku setDisappearDuration(long disappearDuration) {
        this.disappearDuration = disappearDuration;
        return this;
    }

    public int getAlpha() {
        return alpha;
    }

    public BaseDanmaku setAlpha(int alpha) {
        this.alpha = alpha;
        return this;
    }
}
