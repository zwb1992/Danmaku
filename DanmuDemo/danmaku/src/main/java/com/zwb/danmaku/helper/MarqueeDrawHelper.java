package com.zwb.danmaku.helper;

import com.zwb.danmaku.model.BaseDanmaku;

/**
 * @ author : zhouweibin
 * @ time: 2021/5/20 09:49.
 * @ desc: 跑马灯弹幕，默认从右向左 并且屏幕居中 只有一条轨道
 **/
public class MarqueeDrawHelper extends BaseMarqueeDrawHelper {

    @Override
    protected void initPosition(BaseDanmaku danmaku, BaseDanmaku lastNeedShowingDanmaku, int canvasWidth, int canvasHeight) {
        float offRight = canvasWidth;
        if (lastNeedShowingDanmaku != null && lastNeedShowingDanmaku.getScrollX() + lastNeedShowingDanmaku.getWidth() > canvasWidth) {
            offRight = lastNeedShowingDanmaku.getScrollX() + lastNeedShowingDanmaku.getWidth();
        }
        danmaku.setScrollX(offRight + danmaku.getOffset()).setOriginScrollX(offRight + danmaku.getOffset());
    }

}
