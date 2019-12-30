package com.zwb.danmaku.helper;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/29 18:03.
 * @ desc:
 **/
public interface IScrollerDrawHelper {

    /**
     * 设置轨道直接的距离
     * @param mTrajectoryMargin 距离
     * @return 0
     */
    IScrollerDrawHelper setTrajectoryMargin(float mTrajectoryMargin);

    /**
     * 设置轨道的最大数量
     * @param maxTrajectoryCount 数量
     * @return 0
     */
    IScrollerDrawHelper setMaxTrajectoryCount(int maxTrajectoryCount);
}
