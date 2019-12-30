package com.zwb.danmaku.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.zwb.danmaku.model.BaseDanmaku;
import com.zwb.danmaku.model.TrajectoryInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/27 11:21.
 * @ desc: 从左到右
 **/
public class R2LHelper implements IDrawHelper, IScrollerDrawHelper {

    private int canvasWidth;                                            // 画布高度
    private int canvasHeight;                                           // 画布高度
    private int maxTrajectoryCount = Integer.MAX_VALUE;                 // 最大弹道数量
    private float speed;                                                // 默认速度
    private float den;                                                // 默认速度
    private float mTrajectoryMargin;                                    // 轨道之间的间距
    private int offScreenLimit = Integer.MAX_VALUE;                     // 允许离屏初始化的弹幕数量

    private List<BaseDanmaku> penddingDanmakus = new ArrayList<>();     // 待处理的弹幕
    private List<TrajectoryInfo> mTrajectoryInfos = new ArrayList<>();  // 需要展示的弹道

    @Override
    public synchronized void onDrawPrepared(@NonNull Paint textPaint, @NonNull Paint shadowPaint, int canvasWidth, int canvasHeight) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
        // 还有待处理的弹幕，准备加入轨道
        if (!penddingDanmakus.isEmpty()) {
            Iterator<BaseDanmaku> iterator = penddingDanmakus.iterator();
            while (iterator.hasNext()) {
                TrajectoryInfo trajectoryInfo = getMatchingTrajectory();
                // 无可匹配的弹道，说明当前的已经塞满了
                if (trajectoryInfo == null) {
                    break;
                } else {
                    BaseDanmaku danmaku = iterator.next();
                    iterator.remove();
                    if (danmaku.getSpeed() <= 0 && speed > 0) {
                        danmaku.setSpeed(speed);
                    }
                    danmaku.initTextSize(textPaint);
                    danmaku.setScrollY(trajectoryInfo.getScrollY()).setOriginScrollY(trajectoryInfo.getScrollY());
                    if (trajectoryInfo.getWidth() < canvasWidth) {
                        danmaku.setScrollX(canvasWidth + danmaku.getOffset()).setOriginScrollX(canvasWidth + danmaku.getOffset());
                    } else {
                        danmaku.setScrollX(trajectoryInfo.getWidth() + danmaku.getOffset()).setOriginScrollX(trajectoryInfo.getWidth() + danmaku.getOffset());
                    }
                    danmaku.setInit(true);
                    danmaku.setShowState(BaseDanmaku.ShowState.STATE_NEVER_SHOWED);
                    trajectoryInfo.getShowingDanmakus().add(danmaku);
                }
            }
        }
    }

    @Override
    public synchronized void onDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, @NonNull Paint shadowPaint, int canvasWidth, int canvasHeight) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
        for (TrajectoryInfo trajectoryInfo : mTrajectoryInfos) {
            trajectoryInfo.checkedGoneDanmaku();
            for (BaseDanmaku info : trajectoryInfo.getShowingDanmakus()) {
                info.startDraw(canvas, textPaint, shadowPaint, canvasWidth, canvasHeight);
            }
        }
    }

    @Override
    public R2LHelper setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    @Override
    public R2LHelper setOffScreenLimit(int offScreenLimit) {
        this.offScreenLimit = offScreenLimit;
        return this;
    }

    @Override
    public R2LHelper setDen(float den) {
        this.den = den;
        return this;
    }

    @Override
    public synchronized void setDanmakus(@NonNull List<BaseDanmaku> danmakuList) {
        clear();
        this.penddingDanmakus.addAll(danmakuList);
    }

    @Override
    public synchronized void addDanmaku(@NonNull BaseDanmaku danmaku) {
        this.penddingDanmakus.add(danmaku);
    }

    @Override
    public void addDanmakus(@NonNull List<BaseDanmaku> danmakus) {
        this.penddingDanmakus.addAll(danmakus);
    }

    @Override
    public R2LHelper setTrajectoryMargin(float mTrajectoryMargin) {
        this.mTrajectoryMargin = mTrajectoryMargin;
        return this;
    }

    @Override
    public R2LHelper setMaxTrajectoryCount(int maxTrajectoryCount) {
        if (maxTrajectoryCount > 0) {
            this.maxTrajectoryCount = maxTrajectoryCount;
        }
        return this;
    }

    @Override
    public synchronized void clear() {
        penddingDanmakus.clear();
        mTrajectoryInfos.clear();
    }


    /**
     * 获取最匹配的弹道
     *
     * @return null
     */
    private TrajectoryInfo getMatchingTrajectory() {
        // 弹道数量小于最大弹道数量，生成一个新的
        if (mTrajectoryInfos.size() < maxTrajectoryCount) {
            float[] size = getTrajectorySize(mTrajectoryInfos.size() - 1);
            // 超过控件的高度(上一条轨道的底部+轨道之间的距离)
            float lastBottom = size[1] + size[2];
            if (lastBottom + mTrajectoryMargin > canvasHeight) {
                return null;
            } else {
                TrajectoryInfo trajectoryInfo = new TrajectoryInfo();
                trajectoryInfo.setWidth(canvasWidth + mTrajectoryInfos.size() % 3 * den * new Random().nextInt(100));// 初始化弹道的宽度是从右边的屏幕外开始的+偏移量
                trajectoryInfo.setHeight(0).setScrollY(lastBottom + +(mTrajectoryInfos.isEmpty() ? 0 : mTrajectoryMargin)).setNum(mTrajectoryInfos.size());
                mTrajectoryInfos.add(trajectoryInfo);
                return trajectoryInfo;
            }
        } else {
            return getMinWidthTrajectory(getNotOverSizeTrajectoryInfo());
        }
    }

    /**
     * 获取弹道的位置
     *
     * @param trajectoryNum 弹道编号
     * @return 宽 高 y轴位置
     */
    private float[] getTrajectorySize(int trajectoryNum) {
        float[] size = new float[]{0, 0, 0};
        if (trajectoryNum >= 0 && !mTrajectoryInfos.isEmpty() && mTrajectoryInfos.size() > trajectoryNum) {
            TrajectoryInfo trajectoryInfo = mTrajectoryInfos.get(trajectoryNum);
            if (trajectoryInfo != null) {
                calculateTrajectorySize(trajectoryInfo);
                size[0] = trajectoryInfo.getWidth();
                size[1] = trajectoryInfo.getHeight();
                size[2] = trajectoryInfo.getScrollY();
            }
        }
        return size;
    }

    /**
     * 计算轨道的大小
     *
     * @param trajectoryInfo 轨道信息
     */
    private void calculateTrajectorySize(TrajectoryInfo trajectoryInfo) {
        if (trajectoryInfo == null) {
            return;
        }
        List<BaseDanmaku> infos = trajectoryInfo.getShowingDanmakus();
        if (!infos.isEmpty()) {
            float height = 0;
            BaseDanmaku danmakuInfo = infos.get(infos.size() - 1);
            float width = danmakuInfo.getScrollX() + danmakuInfo.getWidth() + danmakuInfo.getOffset();
            float scrollY = 0;
            for (BaseDanmaku info : infos) {
                height = Math.max(height, info.getHeight());
                scrollY = Math.max(scrollY, info.getScrollY());
            }
            trajectoryInfo.setWidth(width).setHeight(height).setScrollY(scrollY);
        } else {
            // 该轨道弹幕全部显示完了
            trajectoryInfo.setWidth(0).setHeight(0);
        }
    }

    private List<TrajectoryInfo> getNotOverSizeTrajectoryInfo() {
        if (mTrajectoryInfos.isEmpty()) {
            return null;
        }
        List<TrajectoryInfo> list = new ArrayList<>();
        for (TrajectoryInfo trajectoryInfo : mTrajectoryInfos) {
            // 该轨道预加载的弹幕小于弹幕缓存限制
            if (trajectoryInfo.getNeverShowedNum() < offScreenLimit) {
                list.add(trajectoryInfo);
            }
        }
        return list;
    }

    /**
     * 获取最短的弹道
     */
    private TrajectoryInfo getMinWidthTrajectory(List<TrajectoryInfo> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        // 已经达到弹道的上限/高度已经超过控件的高度
        TrajectoryInfo trajectoryInfo1 = list.get(0);
        calculateTrajectorySize(trajectoryInfo1);
        int index = 0;
        float width = trajectoryInfo1.getWidth();
        for (int i = 1; i < list.size(); i++) {
            trajectoryInfo1 = list.get(i);
            calculateTrajectorySize(trajectoryInfo1);
            if (width > trajectoryInfo1.getWidth()) {
                index = i;
            }
        }
        // 找出当前宽度最小的弹道
        return list.get(index);
    }
}
