package com.zwb.danmaku.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.zwb.danmaku.model.BaseDanmaku;

import java.util.ArrayList;
import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/27 10:38.
 * @ desc:
 **/
public class DrawHelper implements IDrawHelper, IScrollerDrawHelper {

    private R2LHelper r2LHelper;
    private L2RHelper l2RHelper;

    private static final int DEFAULT_OFF_SCREEN_LIMIT = 2; // 允许离屏初始化2两个弹幕 （针对每一条轨道）

    private int offScreenLimit = DEFAULT_OFF_SCREEN_LIMIT;

    public DrawHelper() {
        getR2LHelper().setOffScreenLimit(offScreenLimit);
        getL2RHelper().setOffScreenLimit(offScreenLimit);
    }

    @Override
    public void onDrawPrepared(@NonNull Paint textPaint, @NonNull Paint shadowPaint, int canvasWidth, int canvasHeight) {
        getR2LHelper().onDrawPrepared(textPaint, shadowPaint, canvasWidth, canvasHeight);
        getL2RHelper().onDrawPrepared(textPaint, shadowPaint, canvasWidth, canvasHeight);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, @NonNull Paint shadowPaint, int canvasWidth, int canvasHeight) {
        getR2LHelper().onDraw(canvas, textPaint, shadowPaint, canvasWidth, canvasHeight);
        getL2RHelper().onDraw(canvas, textPaint, shadowPaint, canvasWidth, canvasHeight);
    }

    @Override
    public DrawHelper setSpeed(float speed) {
        getR2LHelper().setSpeed(speed);
        getL2RHelper().setSpeed(speed);
        return this;
    }

    @Override
    public DrawHelper setDen(float den) {
        getR2LHelper().setDen(den);
        getL2RHelper().setDen(den);
        return this;
    }

    @Override
    public void setDanmakus(@NonNull List<BaseDanmaku> danmakus) {
        List<BaseDanmaku> r2LList = new ArrayList<>();
        for (BaseDanmaku danmaku : danmakus) {
            if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_RL) {
                r2LList.add(danmaku);
            }
        }
        getR2LHelper().setDanmakus(r2LList);

        List<BaseDanmaku> l2RList = new ArrayList<>();
        for (BaseDanmaku danmaku : danmakus) {
            if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_LR) {
                l2RList.add(danmaku);
            }
        }
        getL2RHelper().setDanmakus(l2RList);
    }

    @Override
    public void addDanmaku(@NonNull BaseDanmaku danmaku) {
        if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_RL) {
            getR2LHelper().addDanmaku(danmaku);
        } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_LR) {
            getL2RHelper().addDanmaku(danmaku);
        }
    }

    @Override
    public void addDanmakus(@NonNull List<BaseDanmaku> danmakus) {
        List<BaseDanmaku> r2LList = new ArrayList<>();
        for (BaseDanmaku danmaku : danmakus) {
            if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_RL) {
                r2LList.add(danmaku);
            }
        }
        getR2LHelper().addDanmakus(r2LList);

        List<BaseDanmaku> l2RList = new ArrayList<>();
        for (BaseDanmaku danmaku : danmakus) {
            if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_LR) {
                l2RList.add(danmaku);
            }
        }
        getL2RHelper().addDanmakus(l2RList);
    }

    @Override
    public DrawHelper setOffScreenLimit(int offScreenLimit) {
        if (offScreenLimit > 0) {
            this.offScreenLimit = offScreenLimit;
            getR2LHelper().setOffScreenLimit(offScreenLimit);
            getL2RHelper().setOffScreenLimit(offScreenLimit);
        }
        return this;
    }

    @Override
    public DrawHelper setTrajectoryMargin(float mTrajectoryMargin) {
        getR2LHelper().setTrajectoryMargin(mTrajectoryMargin);
        getL2RHelper().setTrajectoryMargin(mTrajectoryMargin);
        return this;
    }

    @Override
    public DrawHelper setMaxTrajectoryCount(int maxTrajectoryCount) {
        getR2LHelper().setMaxTrajectoryCount(maxTrajectoryCount);
        getL2RHelper().setMaxTrajectoryCount(maxTrajectoryCount);
        return this;
    }

    @Override
    public void clear() {
        getR2LHelper().clear();
        getL2RHelper().clear();
    }

    private R2LHelper getR2LHelper() {
        if (r2LHelper == null) {
            r2LHelper = new R2LHelper();
        }
        return r2LHelper;
    }

    private L2RHelper getL2RHelper() {
        if (l2RHelper == null) {
            l2RHelper = new L2RHelper();
        }
        return l2RHelper;
    }
}
