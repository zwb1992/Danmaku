package com.zwb.danmaku.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zwb.danmaku.utils.BitmapUtils;

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
        TYPE_SPECIAL,           // 特殊弹幕
        TYPE_MARQUEE            // 跑马灯弹幕
    }

    /**
     * 显示类型
     */
    public enum ShowState {
        STATE_NEVER_SHOWED,  // 从未显示过
        STATE_SHOWING,       // 正在显示
        STATE_GONE           // 已经显示过
    }

    public static final int SHADOW_STYLE_LAYER = 1;             // 阴影类型--paint ShadowLayer
    private static final int SHADOW_STYLE_STROKE = 2;            // 阴影类型--paint Stroke

    private Rect textRect = new Rect();                         // 文字绘制的范围

    private String text;                                        // 弹幕内容

    private float textSize;                                     // 弹幕字体大小

    private int textColor;                                      // 弹幕字体颜色

    private float scrollX = 0;                                  // x轴的偏移量 随着时间的推移不断变化

    private float scrollY = 0;                                  // y轴的偏移量 随着时间的推移不断变化

    private float originScrollX = 0;                            // x轴的原始偏移量

    private float originScrollY = 0;                            // y轴的原始偏移量

    private float speed = 0;                                    // 移动的速度（渐变速度）

    private int textHeight;                                     // 弹幕文字的高

    private int textWidth;                                      // 弹幕文字的宽

    private float offset = 0;                                   // 初始位置的偏移量

    private float shadowWidth;                                  // 阴影的宽度

    private int shadowColor;                                    // 阴影的颜色

    private float paddingLeft;                                  // 左边的内边距

    private float paddingRight;                                 // 右边的内边距

    private float paddingTop;                                   // 上面的内边距

    private float paddingBottom;                                // 底部的内边距

    private int shadowStyle = 0;                                // 阴影类型

    private boolean isInit;                                     // 是否完成初始化

    private int lineSpacingExtra = 0;                           // 文字之间的上下距离（多行的情况下）

    private int maxWidth = -1;                                  // 弹幕的最大宽度 -1代表不限制大小

    private float alpha = AlphaValue.MAX;                       // 透明度

    private ShowState showState = ShowState.STATE_NEVER_SHOWED; // 弹幕显示状态

    @DrawableRes
    private int backgroundId;                                   // 背景资源id
    private Bitmap backgroundBitmap;                            // 背景图片
    protected Rect bgSrcRect = new Rect();                      // 背景图片源范围
    protected RectF bgDestRect = new RectF();                   // 背景图片输出范围

    /**
     * 针对定点弹幕
     */
    private long duration;                      // 在屏幕显示停留的时间
    private long disappearDuration;             // 从显示到消失的时间--方便做动画   0的话就是直接消失

    private String[] texts;                     // 弹幕分割多行的数据
    private static final String DANMAKU_BR_CHAR = "\n";

    private Object tag;                                         // 给弹幕设置标记

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
        float height = getTextHeight() + getPaddingBottom() + getPaddingTop() + getShadowWidth() * 2;
        if (texts != null) {
            height = getTextHeight() * texts.length + (texts.length - 1) * lineSpacingExtra + getPaddingBottom() + getPaddingTop() + getShadowWidth() * 2;
        }
        return height;
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

    public float getPaddingLeft() {
        return paddingLeft;
    }

    public BaseDanmaku setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    public float getPaddingRight() {
        return paddingRight;
    }

    public BaseDanmaku setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
        return this;
    }

    public float getPaddingTop() {
        return paddingTop;
    }

    public BaseDanmaku setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    public float getPaddingBottom() {
        return paddingBottom;
    }

    public BaseDanmaku setPaddingBottom(float paddingBottom) {
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

    public float getAlpha() {
        return alpha;
    }

    public BaseDanmaku setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public int getShadowStyle() {
        return shadowStyle;
    }

    public BaseDanmaku setShadowStyle(int shadowStyle) {
        this.shadowStyle = shadowStyle;
        return this;
    }

    public int getLineSpacingExtra() {
        return lineSpacingExtra;
    }

    public void setLineSpacingExtra(int lineSpacingExtra) {
        this.lineSpacingExtra = lineSpacingExtra;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public String[] getTexts() {
        return texts;
    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public BaseDanmaku setBackgroundId(@DrawableRes int backgroundId) {
        this.backgroundId = backgroundId;
        return this;
    }

    public Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public abstract DanmakuType getType();

    public void startDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, @NonNull Paint mTextShadowPaint, @NonNull Paint mBgPaint, int canvasWidth, int canvasHeight) {
        if (!isInit()) {
            return;
        }
        updatePosition();
        updateShowType(canvasWidth, canvasHeight);
        // 只绘制可见的
        if (isVisible()) {
            updatePaint(textPaint, mTextShadowPaint, mBgPaint);
            onDrawBackGround(canvas, mBgPaint, canvasWidth, canvasHeight);
            if (shadowStyle == SHADOW_STYLE_STROKE) {
                onDrawShadow(canvas, mTextShadowPaint, canvasWidth, canvasHeight);
            }
            onDrawContent(canvas, textPaint, canvasWidth, canvasHeight);
        }
    }

    /**
     * @param canvas       画布
     * @param textPaint    文字画笔
     * @param canvasWidth  画布宽度
     * @param canvasHeight 画布高度
     */
    public void onDrawContent(@NonNull Canvas canvas, @NonNull Paint textPaint, int canvasWidth, int canvasHeight) {
        textPaint.setTextAlign(Paint.Align.LEFT);
        if (getTexts() == null) {
            canvas.drawText(getText(), getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop() + getTextHeight(), textPaint);
        } else {
            float offsetY = 0;
            for (String content : getTexts()) {
                canvas.drawText(content, getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop() + getTextHeight() + offsetY, textPaint);
                offsetY += getTextHeight() + getLineSpacingExtra();
            }
        }
    }


    public void onDrawShadow(@NonNull Canvas canvas, @NonNull Paint mTextShadowPaint, int canvasWidth, int canvasHeight) {
        mTextShadowPaint.setTextAlign(Paint.Align.LEFT);
        if (getTexts() == null) {
            canvas.drawText(getText(), getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop() + getTextHeight(), mTextShadowPaint);
        } else {
            float offsetY = 0;
            for (String content : getTexts()) {
                canvas.drawText(content, getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop() + getTextHeight() + offsetY, mTextShadowPaint);
                offsetY += getTextHeight() + getLineSpacingExtra();
            }
        }
    }


    public void onDrawBackGround(@NonNull Canvas canvas, @NonNull Paint mBackGroundPaint, int canvasWidth, int canvasHeight) {
        if (getBackgroundBitmap() != null) {
            bgDestRect.set(getScrollX() - getShadowWidth(), getScrollY(), getScrollX() + getWidth() - getShadowWidth(), getScrollY() + getHeight());
            canvas.drawBitmap(getBackgroundBitmap(), bgSrcRect, bgDestRect, mBackGroundPaint);
        }
    }

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

    private void updatePaint(@NonNull Paint textPaint, @NonNull Paint mTextShadowPaint, @NonNull Paint mBgPaint) {
        try {
            if (getTextSize() > 0) {
                textPaint.setTextSize(getTextSize());
                mTextShadowPaint.setTextSize(getTextSize());
            }
            if (getTextColor() != 0) {
                textPaint.setColor(getTextColor());
            }
            if (shadowStyle == SHADOW_STYLE_LAYER) {
                if (getShadowWidth() > 0 && getShadowColor() != 0) {
                    textPaint.setShadowLayer(getShadowWidth(), 0, 0, getShadowColor());
                }
            } else {
                textPaint.clearShadowLayer();
                if (getShadowWidth() > 0 && getShadowColor() != 0) {
                    mTextShadowPaint.setColor(getShadowColor());
                    mTextShadowPaint.setStrokeWidth(getShadowWidth());
                }
            }
            textPaint.setAlpha((int) getAlpha());
            mTextShadowPaint.setAlpha((int) getAlpha());
            mBgPaint.setAlpha((int) getAlpha());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSize(Paint paint) {
        if (TextUtils.isEmpty(getText()) || paint == null) {
            return;
        }
        if (getTextSize() > 0) {
            paint.setTextSize(getTextSize());
        }
        checkSplitText(paint);
        if (texts == null) {
            paint.getTextBounds(getText(), 0, getText().length(), textRect);
            setTextWidth(textRect.width()).setTextHeight(textRect.height());
        } else {
            int width = 0;
            int height = 0;
            for (String t : texts) {
                paint.getTextBounds(t, 0, t.length(), textRect);
                if (textRect.width() > width) {
                    width = textRect.width();
                }
                if (textRect.height() > height) {
                    height = textRect.height();
                }
            }
            setTextWidth(width).setTextHeight(height);
        }
    }

    /**
     * 检测是否需要分割字符串
     */
    private void checkSplitText(Paint paint) {
        texts = text.split(DANMAKU_BR_CHAR);
        // 不限制宽度就直接返回
        if (getMaxWidth() - getPaddingRight() - getPaddingRight() < 0) {
            return;
        }
        if (texts == null) {
            texts = splitText(getText(), paint).split(DANMAKU_BR_CHAR);
        } else {
            texts = splitTexts(texts, paint).split(DANMAKU_BR_CHAR);
        }
    }

    private String splitTexts(String[] texts, Paint paint) {
        StringBuilder sbNewText = new StringBuilder();
        for (String rawTextLine : texts) {
            if (!TextUtils.isEmpty(rawTextLine)) {
                if (paint.measureText(rawTextLine) <= getMaxWidth() - getPaddingRight() - getPaddingRight()) {
                    //如果整行宽度在控件可用宽度之内，就不处理了
                    sbNewText.append(rawTextLine);
                } else {
                    sbNewText.append(splitText(rawTextLine, paint));
                }
                sbNewText.append(DANMAKU_BR_CHAR);
            }
        }
        //把结尾多余的\n去掉
        if (!sbNewText.toString().endsWith(DANMAKU_BR_CHAR)) {
            sbNewText.deleteCharAt(sbNewText.length() - 1);
        }

        return sbNewText.toString();
    }

    private String splitText(String text, Paint paint) {
        StringBuilder sbNewText = new StringBuilder();
        //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
        float lineWidth = 0;
        for (int cnt = 0; cnt != text.length(); ++cnt) {
            char ch = text.charAt(cnt);
            lineWidth += paint.measureText(String.valueOf(ch));
            if (lineWidth <= getMaxWidth() - getPaddingRight() - getPaddingRight()) {
                sbNewText.append(ch);
            } else {
                sbNewText.append(DANMAKU_BR_CHAR);
                lineWidth = 0;
                --cnt;
            }
        }
        return sbNewText.toString();
    }

    /**
     * 准备背景图片
     *
     * @param context 上下文
     */
    public void preparedBg(Context context) {
        try {
            if (context != null && getBackgroundId() != 0 && backgroundBitmap == null) {
                backgroundBitmap = BitmapUtils.getBackgroundForDanmaku(context, getBackgroundId(), (int) getWidth(), (int) getHeight());
                bgSrcRect.set(0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源--当不可见的时候
     */
    public void release() {
        if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
            backgroundBitmap.recycle();
            backgroundBitmap = null;
        }
    }
}
