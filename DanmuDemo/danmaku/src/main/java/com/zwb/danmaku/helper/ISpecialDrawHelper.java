package com.zwb.danmaku.helper;

/**
 * @ author : zhouweibin
 * @ time: 2020/1/13 15:09.
 * @ desc:
 **/
public interface ISpecialDrawHelper {

    /**
     * 设置下一个弹幕显示的间隔时间
     *
     * @param interval 间隔时间
     * @return this
     */
    ISpecialDrawHelper setInterval(long interval);


    /**
     * 设置屏幕上允许存在的弹幕数量
     *
     * @param countLimit 弹幕数量
     * @return this
     */
    ISpecialDrawHelper setCountLimit(int countLimit);
}
