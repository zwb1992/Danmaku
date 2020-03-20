package com.zwb.danmaku;

import com.zwb.danmaku.helper.DrawHelper;
import com.zwb.danmaku.model.BaseDanmaku;

import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2020/3/17 15:24.
 * @ desc:
 **/
public interface IDanmakuView {

    /**
     * 获取弹幕绘制帮助类
     *
     * @return DrawHelper
     */
    DrawHelper getDrawHelper();

    /**
     * 设置最大弹道数量
     *
     * @param maxTrajectoryCount 数量
     * @return 弹幕
     */
    DanmakuView setMaxTrajectoryCount(int maxTrajectoryCount);

    /**
     * 设置最大循环播放次数
     *
     * @param maxRepeatCount 次数
     * @return 弹幕
     */
    DanmakuView setMaxRepeatCount(int maxRepeatCount);

    /**
     * 设置特殊弹幕显示时间
     *
     * @param interval 显示的时间间隔
     * @return 弹幕
     */
    DanmakuView setInterval(long interval);

    /**
     * 设置特殊弹幕显示数量
     *
     * @param countLimit 显示的弹幕数量
     * @return 弹幕
     */
    DanmakuView setCountLimit(int countLimit);

    /**
     * 设置阴影样式
     *
     * @param shadowStyle 样式
     */
    void setShadowStyle(int shadowStyle);

    /**
     * 设置显示的弹幕列表
     *
     * @param danmukus 弹幕列表
     */
    void setDanmukus(List<BaseDanmaku> danmukus);

    /**
     * 添加显示的弹幕
     *
     * @param info 弹幕信息
     */
    void addDanmuku(BaseDanmaku info);

    /**
     * 添加显示的弹幕列表
     *
     * @param danmukus 弹幕列表
     */
    void addDanmukus(List<BaseDanmaku> danmukus);

    /**
     * 停止播放
     */
    void stop();

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 恢复播放
     */
    void resume();

    /**
     * 清空弹幕
     */
    void clear();

    /**
     * 获取手势触摸命中的弹幕
     *
     * @param x x轴坐标
     * @param y y轴坐标
     * @return null
     */
    BaseDanmaku getTouchMatchedDamaku(float x, float y);


    // ------------- Click Listener -------------------
    interface OnDanmakuClickListener {
        /**
         * 弹幕单击事件
         *
         * @param danmaku 点击的弹幕信息
         * @return 是否拦截事件
         */
        boolean onDanmakuClick(BaseDanmaku danmaku);

        /**
         * 弹幕长按事件
         *
         * @param danmaku 长按的弹幕信息
         * @return 是否拦截事件
         */
        boolean onDanmakuLongClick(BaseDanmaku danmaku);

        /**
         * 弹幕单击事件
         *
         * @param view 弹幕控件
         * @return 是否拦截事件
         */
        boolean onViewClick(IDanmakuView view);

        /**
         * 弹幕长按事件
         *
         * @param view 弹幕控件
         * @return 是否拦截事件
         */
        boolean onViewLongClick(IDanmakuView view);

        /**
         * 点击弹幕事件--整个控件
         *
         * @param x x轴坐标
         * @param y y轴坐标
         * @return 是否消费down事件 false 事件由某一个弹幕控件消费
         */
        boolean onDownView(float x, float y);

        /**
         * 点击弹幕事件--控件上的某个弹幕
         *
         * @param x x轴坐标
         * @param y y轴坐标
         * @return 是否消费down事件 false   事件由某一个弹幕item消费
         */
        boolean onDownDanmaku(float x, float y);
    }

    void setOnDanmakuClickListener(OnDanmakuClickListener listener);

    OnDanmakuClickListener getOnDanmakuClickListener();
}
