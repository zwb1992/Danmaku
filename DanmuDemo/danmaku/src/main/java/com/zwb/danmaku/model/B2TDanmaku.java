package com.zwb.danmaku.model;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/26 14:36.
 * @ desc: 从下向上滑动
 **/
public class B2TDanmaku extends BaseDanmaku {

    @Override
    public DanmakuType getType() {
        return DanmakuType.TYPE_SCROLL_BT;
    }

    @Override
    public void updatePosition() {
        // 已经显示过的不需要更新位置
        if (getShowState() != ShowState.STATE_GONE) {
            setScrollY(getScrollY() - getSpeed());
        }
    }

    @Override
    public void updateShowType(int canvasWidth, int canvasHeight) {
        if (getScrollY() + getHeight() <= 0
                || getScrollX() + getWidth() <= 0
                || getScrollX() >= canvasWidth) {
            setShowState(ShowState.STATE_GONE);
        } else if (getScrollY() >= canvasHeight) {
            setShowState(ShowState.STATE_NEVER_SHOWED);
        } else {
            setShowState(ShowState.STATE_SHOWING);
        }
    }
}
