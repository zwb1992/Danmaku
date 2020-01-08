package com.zwb.danmaku.helper;

import com.zwb.danmaku.model.BaseDanmaku;
import com.zwb.danmaku.model.TrajectoryInfo;

import java.util.List;
import java.util.Random;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/27 11:21.
 * @ desc: 从右到左
 **/
public class R2LHelper extends BaseScrollerDrawHelper {

    @Override
    protected void initPosition(BaseDanmaku danmaku, TrajectoryInfo trajectoryInfo, int canvasWidth, int canvasHeight) {
        danmaku.setScrollY(trajectoryInfo.getTop()).setOriginScrollY(trajectoryInfo.getTop());
        if (trajectoryInfo.getRight() < canvasWidth) {
            danmaku.setScrollX(canvasWidth + danmaku.getOffset()).setOriginScrollX(canvasWidth + danmaku.getOffset());
        } else {
            danmaku.setScrollX(trajectoryInfo.getRight() + danmaku.getOffset()).setOriginScrollX(trajectoryInfo.getRight() + danmaku.getOffset());
        }
    }

    /**
     * 获取最匹配的弹道
     *
     * @return null
     */
    @Override
    protected TrajectoryInfo getNewTrajectory(float[] lastTrajectoryPos) {
        // 超过控件的高度(上一条轨道的底部+轨道之间的距离)
        float lastBottom = lastTrajectoryPos[3];
        if (lastBottom + mTrajectoryMargin > canvasHeight) {
            return null;
        } else {
            TrajectoryInfo trajectoryInfo = new TrajectoryInfo();
            trajectoryInfo.setRight(canvasWidth + mTrajectoryInfos.size() % 3 * den * new Random().nextInt(100));// 初始化弹道的宽度是从右边的屏幕外开始的+偏移量
            trajectoryInfo.setTop(lastBottom + (mTrajectoryInfos.isEmpty() ? 0 : mTrajectoryMargin)).setNum(mTrajectoryInfos.size());
            mTrajectoryInfos.add(trajectoryInfo);
            return trajectoryInfo;
        }
    }

    @Override
    protected TrajectoryInfo getMatchingTrajectory(List<TrajectoryInfo> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        // 已经达到弹道的上限/高度已经超过控件的高度(如果弹幕已经全部显示完毕，轨道大小会被重置，所以需要重新计算轨道的位置)

        TrajectoryInfo target = list.get(0);
        calculateTrajectorySize(target);
        // 找到left和right都为0的，return
        if (target.getLeft() == 0 && target.getRight() == 0) {
            return target;
        }
        int index = 0;
        float right = target.getRight();
        for (int i = 1; i < list.size(); i++) {
            TrajectoryInfo trajectoryInfo = list.get(i);
            calculateTrajectorySize(trajectoryInfo);
            // 找到left和right都为0的，return
            if (trajectoryInfo.getLeft() == 0 && trajectoryInfo.getRight() == 0) {
                index = i;
                break;
            }
            if (right > trajectoryInfo.getRight()) {
                right = trajectoryInfo.getRight();
                index = i;
            }
        }
        target = list.get(index);
        // 找出当前right最小的弹道--并重新计算Y轴偏移量
        if (target.getTop() <= 0 && index != 0) {
            target.setTop(getTrajectorySize(index - 1)[3] + mTrajectoryMargin);
        }
        return list.get(index);
    }
}
