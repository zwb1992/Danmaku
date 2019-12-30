package com.zwb.danmaku.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/24 13:58.
 * @ desc: 弹道数据
 **/
public class TrajectoryInfo {
    private float width;
    private float height;
    private float scrollY;
    private float scrollX;
    private int num = 0;// 标号
    private List<BaseDanmaku> showingDanmakus = new ArrayList<>();      // 正在显示或即将显示的弹幕
    private List<BaseDanmaku> goneDanmakus = new ArrayList<>();         // 已经显示过的弹幕（不再显示）

    public float getWidth() {
        return width;
    }

    public TrajectoryInfo setWidth(float width) {
        this.width = width;
        return this;
    }

    public float getHeight() {
        return height;
    }

    public TrajectoryInfo setHeight(float height) {
        this.height = height;
        return this;
    }

    public float getScrollY() {
        return scrollY;
    }

    public TrajectoryInfo setScrollY(float scrollY) {
        this.scrollY = scrollY;
        return this;
    }

    public float getScrollX() {
        return scrollY;
    }

    public TrajectoryInfo setScrollX(float scrollX) {
        this.scrollX = scrollX;
        return this;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "TrajectoryInfo{" +
                "width=" + width +
                ", height=" + height +
                ", scrollY=" + scrollY +
                ", num=" + num +
                ", showingDanmakus=" + showingDanmakus +
                ", goneDanmakus=" + goneDanmakus +
                '}';
    }

    /**
     * 把不可见的弹幕加入列表
     */
    public void checkedGoneDanmaku() {
        if (showingDanmakus.isEmpty()) {
            return;
        }
        Iterator<BaseDanmaku> iterator = showingDanmakus.iterator();
        while (iterator.hasNext()) {
            BaseDanmaku danmaku = iterator.next();
            if (danmaku.isGone()) {
                iterator.remove();
                goneDanmakus.add(danmaku);
            } else {
                // 第一个弹幕不是隐藏状态，后面的更不可能是了
                break;
            }
        }
    }

    /**
     * 获取从未显示过的弹幕数量
     *
     * @return 0
     */
    public int getNeverShowedNum() {
        int size = 0;
        for (BaseDanmaku danmaku : showingDanmakus) {
            if (danmaku.isNeverShowed()) {
                size++;
            }
        }
        return size;
    }

    public List<BaseDanmaku> getShowingDanmakus() {
        return showingDanmakus;
    }

    public List<BaseDanmaku> getGoneDanmakus() {
        return goneDanmakus;
    }
}
