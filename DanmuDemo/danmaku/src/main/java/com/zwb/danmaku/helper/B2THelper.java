package com.zwb.danmaku.helper;


import com.zwb.danmaku.model.BaseDanmaku;
import com.zwb.danmaku.model.TrajectoryInfo;

import java.util.List;
import java.util.Random;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/27 11:21.
 * @ desc: 从下到上
 **/
public class B2THelper extends BaseScrollerDrawHelper {


    @Override
    protected void initPosition(BaseDanmaku danmaku, TrajectoryInfo trajectoryInfo, int canvasWidth, int canvasHeight) {
        danmaku.setScrollX(trajectoryInfo.getLeft()).setOriginScrollX(trajectoryInfo.getLeft());
        if (trajectoryInfo.getBottom() < canvasHeight) {
            danmaku.setScrollY(canvasHeight + danmaku.getOffset()).setOriginScrollY(canvasHeight + danmaku.getOffset());
        } else {
            danmaku.setScrollY(trajectoryInfo.getBottom() + danmaku.getOffset())
                    .setOriginScrollY(trajectoryInfo.getBottom() + danmaku.getOffset());
        }
    }

    /**
     * 创建新的轨道
     *
     * @return null
     */
    @Override
    protected TrajectoryInfo getNewTrajectory(float[] lastTrajectoryPos) {
        // 超过控件的宽度(上一条轨道的右边+轨道之间的距离)
        float lastRight = lastTrajectoryPos[2];
        if (lastRight + mTrajectoryMargin > canvasWidth) {
            return null;
        } else {
            TrajectoryInfo trajectoryInfo = new TrajectoryInfo();
            trajectoryInfo.setTop(canvasHeight + mTrajectoryInfos.size() % 3 * den * new Random().nextInt(100));// 初始化弹道的top是从屏幕外开始的+偏移量
            trajectoryInfo.setLeft(lastRight + (mTrajectoryInfos.isEmpty() ? 0 : mTrajectoryMargin)).setNum(mTrajectoryInfos.size());
            mTrajectoryInfos.add(trajectoryInfo);
            return trajectoryInfo;
        }
    }


    @Override
    protected TrajectoryInfo getMatchingTrajectory(List<TrajectoryInfo> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        // 已经达到弹道的上限/宽度已经超过控件的高度(如果弹幕已经全部显示完毕，轨道大小会被重置，所以需要重新计算轨道的位置)
        TrajectoryInfo target = list.get(0);
        calculateTrajectorySize(target);
        // 找到top和bottom都为0的，return
        if (target.getTop() == 0 && target.getBottom() == 0) {
            return target;
        }
        int index = 0;
        float bottom = target.getBottom();
        for (int i = 1; i < list.size(); i++) {
            TrajectoryInfo trajectoryInfo = list.get(i);
            calculateTrajectorySize(trajectoryInfo);
            // 找到top和bottom都为0的，return
            if (trajectoryInfo.getTop() == 0 && trajectoryInfo.getBottom() == 0) {
                index = i;
                break;
            }
            if (bottom > trajectoryInfo.getBottom()) {
                bottom = trajectoryInfo.getBottom();
                index = i;
            }
        }
        target = list.get(index);
        // 找出当前bottom最小的弹道--并重新计算X轴偏移量
        if (target.getLeft() <= 0 && index != 0) {
            target.setLeft(getTrajectorySize(index - 1)[2] + mTrajectoryMargin);
        }
        return target;
    }

    @Override
    protected TrajectoryInfo getEmptyTrajectory() {
        TrajectoryInfo target = null;
        int index = -1;
        for (int i = 0; i < mTrajectoryInfos.size(); i++){
            TrajectoryInfo trajectoryInfo = mTrajectoryInfos.get(i);
            if(trajectoryInfo.getShowingDanmakus().isEmpty()){
                index = i;
                break;
            }
        }
        if(index != -1){
            target = mTrajectoryInfos.get(index);
            calculateTrajectorySize(target);
            // 找出当前bottom最小的弹道--并重新计算X轴偏移量
            if (target.getLeft() <= 0 && index != 0) {
                target.setLeft(getTrajectorySize(index - 1)[2] + mTrajectoryMargin);
            }
        }
        return target;
    }
}
