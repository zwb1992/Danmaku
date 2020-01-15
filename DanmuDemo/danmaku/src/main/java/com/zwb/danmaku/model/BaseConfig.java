package com.zwb.danmaku.model;

import android.graphics.Color;

/**
 * @ author : zhouweibin
 * @ time: 2020/1/15 14:19.
 * @ desc: 弹幕基本配置
 **/
public class BaseConfig {

    private int textSize = 24;                                         // 默认文字大小
    private int textColor = Color.WHITE;                               // 默认文字颜色
    private int textShadowColor = Color.TRANSPARENT;                   // 默认阴影颜色
    private int textShadowWidth = 0;                                   // 默认阴影宽度
    private float speed;                                                // 默认速度
    private int shadowStyle = BaseDanmaku.SHADOW_STYLE_LAYER;             // 阴影的类型

    public int getTextSize() {
        return textSize;
    }

    public BaseConfig setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public BaseConfig setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public int getTextShadowColor() {
        return textShadowColor;
    }

    public BaseConfig setTextShadowColor(int textShadowColor) {
        this.textShadowColor = textShadowColor;
        return this;
    }

    public int getTextShadowWidth() {
        return textShadowWidth;
    }

    public BaseConfig setTextShadowWidth(int textShadowWidth) {
        this.textShadowWidth = textShadowWidth;
        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public BaseConfig setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public int getShadowStyle() {
        return shadowStyle;
    }

    public BaseConfig setShadowStyle(int shadowStyle) {
        this.shadowStyle = shadowStyle;
        return this;
    }

    /**
     * 检测弹幕的基本配置
     *
     * @param danmaku     弹幕
     * @param ignoreSpeed 是否忽略速度
     */
    public void checkDanmakuConfig(BaseDanmaku danmaku, boolean ignoreSpeed) {
        if (danmaku != null) {
            if (danmaku.getTextSize() <= 0 && textSize > 0) {
                danmaku.setTextSize(textSize);
            }
            if (danmaku.getTextColor() == 0 && textColor != 0) {
                danmaku.setTextColor(textColor);
            }
            if (danmaku.getShadowColor() == 0 && textShadowColor != 0) {
                danmaku.setShadowColor(textShadowColor);
            }
            if (danmaku.getShadowWidth() <= 0 && textShadowWidth > 0) {
                danmaku.setShadowWidth(textShadowWidth);
            }
            if (danmaku.getSpeed() <= 0 && speed > 0 && !ignoreSpeed) {
                danmaku.setSpeed(speed);
            }
            if (danmaku.getShadowStyle() <= 0 && shadowStyle > 0) {
                danmaku.setShadowStyle(shadowStyle);
            }
        }
    }
}
