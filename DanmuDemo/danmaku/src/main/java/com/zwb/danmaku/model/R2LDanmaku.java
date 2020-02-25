package com.zwb.danmaku.model;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/26 14:36.
 * @ desc: 从右向左滑动
 **/
public class R2LDanmaku extends BaseDanmaku {

    @Override
    public DanmakuType getType() {
        return BaseDanmaku.DanmakuType.TYPE_SCROLL_RL;
    }

    @Override
    public void updatePosition() {
        // 已经显示过的不需要更新位置
        if (getShowState() != ShowState.STATE_GONE) {
            setScrollX(getScrollX() - getSpeed());
        }
    }

    @Override
    public void updateShowType(int canvasWidth, int canvasHeight) {
        if (getScrollX() + getWidth() <= 0
                || getScrollY() + getHeight() <= 0
                || getScrollY() >= canvasHeight) {
            setShowState(ShowState.STATE_GONE);
        } else if (getScrollX() >= canvasWidth) {
            setShowState(ShowState.STATE_NEVER_SHOWED);
        } else {
            setShowState(ShowState.STATE_SHOWING);
        }
    }
}
