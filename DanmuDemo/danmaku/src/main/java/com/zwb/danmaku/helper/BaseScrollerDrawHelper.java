package com.zwb.danmaku.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.zwb.danmaku.model.BaseDanmaku;
import com.zwb.danmaku.model.TrajectoryInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/31 15:42.
 * @ desc: 移动的弹幕
 **/
public abstract class BaseScrollerDrawHelper implements IDrawHelper, IScrollerDrawHelper {

    int canvasWidth;                                            // 画布高度
    int canvasHeight;                                           // 画布高度
    private int maxTrajectoryCount = Integer.MAX_VALUE;                 // 最大弹道数量
    private float speed;                                                // 默认速度
    float den;                                                // 默认速度
    float mTrajectoryMargin;                                    // 轨道之间的间距
    private int offScreenLimit = Integer.MAX_VALUE;                     // 允许离屏初始化的弹幕数量

    private List<BaseDanmaku> penddingDanmakus = new ArrayList<>();     // 待处理的弹幕
    List<TrajectoryInfo> mTrajectoryInfos = new ArrayList<>();  // 需要展示的弹道

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
                    // 循环显示的时候可不再执行
                    if(!danmaku.isInit()) {
                        danmaku.initTextSize(textPaint);
                    }
                    initPosition(danmaku, trajectoryInfo, canvasWidth, canvasHeight);
                    danmaku.setInit(true);
                    danmaku.setShowState(BaseDanmaku.ShowState.STATE_NEVER_SHOWED);
                    trajectoryInfo.getShowingDanmakus().add(danmaku);
                }
            }
        }
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
            return getNewTrajectory(size);
        } else {
            return getMatchingTrajectory(getNotOverSizeTrajectoryInfo());
        }
    }

    /**
     * 创建新的轨道
     *
     * @param lastTrajectoryPos 上一个轨道的位置
     * @return null
     */
    protected abstract TrajectoryInfo getNewTrajectory(float[] lastTrajectoryPos);

    /**
     * 获取最匹配的弹道
     */
    protected abstract TrajectoryInfo getMatchingTrajectory(List<TrajectoryInfo> list);

    /**
     * 初始化弹幕的位置
     *
     * @param danmaku        弹幕
     * @param trajectoryInfo 轨道
     * @param canvasWidth    画布宽度
     * @param canvasHeight   画布高度
     */
    protected abstract void initPosition(BaseDanmaku danmaku, TrajectoryInfo trajectoryInfo, int canvasWidth, int canvasHeight);

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
    public BaseScrollerDrawHelper setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    @Override
    public BaseScrollerDrawHelper setOffScreenLimit(int offScreenLimit) {
        this.offScreenLimit = offScreenLimit;
        return this;
    }

    @Override
    public BaseScrollerDrawHelper setDen(float den) {
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
    public synchronized void addDanmakus(@NonNull List<BaseDanmaku> danmakus) {
        this.penddingDanmakus.addAll(danmakus);
    }

    @Override
    public BaseScrollerDrawHelper setTrajectoryMargin(float mTrajectoryMargin) {
        this.mTrajectoryMargin = mTrajectoryMargin;
        return this;
    }

    @Override
    public BaseScrollerDrawHelper setMaxTrajectoryCount(int maxTrajectoryCount) {
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
     * 获取弹道的位置
     *
     * @param trajectoryNum 弹道编号
     * @return left top right bottom
     */
    float[] getTrajectorySize(int trajectoryNum) {
        float[] size = new float[]{0, 0, 0, 0};
        if (trajectoryNum >= 0 && !mTrajectoryInfos.isEmpty() && mTrajectoryInfos.size() > trajectoryNum) {
            TrajectoryInfo trajectoryInfo = mTrajectoryInfos.get(trajectoryNum);
            if (trajectoryInfo != null) {
                calculateTrajectorySize(trajectoryInfo);
                size[0] = trajectoryInfo.getLeft();
                size[1] = trajectoryInfo.getTop();
                size[2] = trajectoryInfo.getRight();
                size[3] = trajectoryInfo.getBottom();
            }
        }
        return size;
    }

    /**
     * 计算轨道的大小
     *
     * @param trajectoryInfo 轨道信息
     */
    void calculateTrajectorySize(TrajectoryInfo trajectoryInfo) {
        if (trajectoryInfo == null) {
            return;
        }
        List<BaseDanmaku> infos = trajectoryInfo.getShowingDanmakus();
        if (!infos.isEmpty()) {
            float left = 0;
            float right = 0;
            float top = 0;
            float bottom = 0;
            for (int i = 0; i < infos.size(); i++) {
                BaseDanmaku info = infos.get(i);
                float tempLeft = info.getScrollX();
                float tempRight = info.getScrollX() + info.getWidth();
                float tempTop = info.getScrollY();
                float tempBottom = info.getScrollY() + info.getHeight();
                if (i == 0) {
                    left = tempLeft;
                    right = tempRight;
                    top = tempTop;
                    bottom = tempBottom;
                } else {
                    if (left > tempLeft) {
                        left = tempLeft;
                    }
                    if (right < tempRight) {
                        right = tempRight;
                    }
                    if (top > tempTop) {
                        top = tempTop;
                    }
                    if (bottom < tempBottom) {
                        bottom = tempBottom;
                    }
                }
            }
            trajectoryInfo.setBounds(left, top, right, bottom);
        } else {
            // 该轨道弹幕全部显示完了
            trajectoryInfo.setBounds(0, 0, 0, 0);
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
}

