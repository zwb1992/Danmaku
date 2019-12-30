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
    /**
     * 正在显示或即将显示的弹幕的边缘
     */
    private float left;             // 轨道的left
    private float right;            // 轨道的right
    private float top;              // 轨道的top
    private float bottom;           // 轨道的bottom
    private int num = 0;// 标号
    private List<BaseDanmaku> showingDanmakus = new ArrayList<>();      // 正在显示或即将显示的弹幕
    private List<BaseDanmaku> goneDanmakus = new ArrayList<>();         // 已经显示过的弹幕（不再显示）

    public float getWidth() {
        return right - left;
    }


    public float getHeight() {
        return bottom - top;
    }

    public float getLeft() {
        return left;
    }

    public TrajectoryInfo setLeft(float left) {
        this.left = left;
        return this;
    }

    public TrajectoryInfo setBounds(float left,float top,float right,float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        return this;
    }

    public float getRight() {
        return right;
    }

    public TrajectoryInfo setRight(float right) {
        this.right = right;
        return this;
    }

    public float getTop() {
        return top;
    }

    public TrajectoryInfo setTop(float top) {
        this.top = top;
        return this;
    }

    public float getBottom() {
        return bottom;
    }

    public TrajectoryInfo setBottom(float bottom) {
        this.bottom = bottom;
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
                ", left=" + left +
                ", right=" + right +
                ", top=" + top +
                ", bottom=" + bottom +
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
