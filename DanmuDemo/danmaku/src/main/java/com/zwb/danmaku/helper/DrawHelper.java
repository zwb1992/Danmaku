package com.zwb.danmaku.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.zwb.danmaku.model.BaseConfig;
import com.zwb.danmaku.model.BaseDanmaku;

import java.util.ArrayList;
import java.util.List;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/27 10:38.
 * @ desc:
 **/
public class DrawHelper implements IDrawHelper, IScrollerDrawHelper, ISpecialDrawHelper {

    private R2LHelper r2LHelper;
    private L2RHelper l2RHelper;
    private T2BHelper t2BHelper;
    private B2THelper b2THelper;
    private BaseSpecialHelper specialHelper;

    private static final int DEFAULT_OFF_SCREEN_LIMIT = 2; // 允许离屏初始化两个弹幕 （针对每一条轨道）

    private int offScreenLimit = DEFAULT_OFF_SCREEN_LIMIT;
    private BaseConfig baseConfig;
    private float den;
    private float mTrajectoryMargin;
    private int maxTrajectoryCount;
    private long interval;
    private int countLimit;

    @Override
    public void onDrawPrepared(@NonNull Paint textPaint, @NonNull Paint mTextShadowPaint, int canvasWidth, int canvasHeight) {
        if (r2LHelper != null) {
            r2LHelper.onDrawPrepared(textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
        if (l2RHelper != null) {
            l2RHelper.onDrawPrepared(textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
        if (t2BHelper != null) {
            t2BHelper.onDrawPrepared(textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
        if (b2THelper != null) {
            b2THelper.onDrawPrepared(textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
        if (specialHelper != null) {
            specialHelper.onDrawPrepared(textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull Paint textPaint, @NonNull Paint mTextShadowPaint, int canvasWidth, int canvasHeight) {
        if (r2LHelper != null) {
            r2LHelper.onDraw(canvas, textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
        if (l2RHelper != null) {
            l2RHelper.onDraw(canvas, textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
        if (t2BHelper != null) {
            t2BHelper.onDraw(canvas, textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
        if (b2THelper != null) {
            b2THelper.onDraw(canvas, textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
        if (specialHelper != null) {
            specialHelper.onDraw(canvas, textPaint, mTextShadowPaint, canvasWidth, canvasHeight);
        }
    }

    @Override
    public DrawHelper setSpeed(float speed) {
        if (baseConfig != null) {
            baseConfig.setSpeed(speed);
            if (r2LHelper != null) {
                r2LHelper.setBaseConfig(baseConfig);
            }
            if (l2RHelper != null) {
                l2RHelper.setBaseConfig(baseConfig);
            }
            if (t2BHelper != null) {
                t2BHelper.setBaseConfig(baseConfig);
            }
            if (b2THelper != null) {
                b2THelper.setBaseConfig(baseConfig);
            }
            if (specialHelper != null) {
                specialHelper.setBaseConfig(baseConfig);
            }
        }
        return this;
    }

    public DrawHelper setBaseConfig(BaseConfig baseConfig) {
        this.baseConfig = baseConfig;
        if (r2LHelper != null) {
            r2LHelper.setBaseConfig(baseConfig);
        }
        if (l2RHelper != null) {
            l2RHelper.setBaseConfig(baseConfig);
        }
        if (t2BHelper != null) {
            t2BHelper.setBaseConfig(baseConfig);
        }
        if (b2THelper != null) {
            b2THelper.setBaseConfig(baseConfig);
        }
        if (specialHelper != null) {
            specialHelper.setBaseConfig(baseConfig);
        }
        return this;
    }

    @Override
    public DrawHelper setDen(float den) {
        this.den = den;
        if (r2LHelper != null) {
            r2LHelper.setDen(den);
        }
        if (l2RHelper != null) {
            l2RHelper.setDen(den);
        }
        if (t2BHelper != null) {
            t2BHelper.setDen(den);
        }
        if (b2THelper != null) {
            b2THelper.setDen(den);
        }
        if (specialHelper != null) {
            specialHelper.setDen(den);
        }
        return this;
    }

    @Override
    public void setDanmakus(@NonNull List<BaseDanmaku> danmakus) {
        List<BaseDanmaku> r2LList = new ArrayList<>();
        List<BaseDanmaku> l2RList = new ArrayList<>();
        List<BaseDanmaku> t2BList = new ArrayList<>();
        List<BaseDanmaku> b2TList = new ArrayList<>();
        List<BaseDanmaku> specialList = new ArrayList<>();
        for (BaseDanmaku danmaku : danmakus) {
            if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_RL) {
                r2LList.add(danmaku);
            } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_LR) {
                l2RList.add(danmaku);
            } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_TB) {
                t2BList.add(danmaku);
            } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_BT) {
                b2TList.add(danmaku);
            } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SPECIAL) {
                specialList.add(danmaku);
            }
        }
        if (!r2LList.isEmpty()) {
            checkR2LHelper();
            r2LHelper.setDanmakus(r2LList);
        }

        if (!l2RList.isEmpty()) {
            checkL2RHelper();
            l2RHelper.setDanmakus(l2RList);
        }

        if (!t2BList.isEmpty()) {
            checkT2BHelper();
            t2BHelper.setDanmakus(t2BList);
        }

        if (!b2TList.isEmpty()) {
            checkB2THelper();
            b2THelper.setDanmakus(b2TList);
        }

        if (!specialList.isEmpty()) {
            checkSpecialHelper();
            specialHelper.setDanmakus(specialList);
        }
    }

    @Override
    public void addDanmaku(@NonNull BaseDanmaku danmaku) {
        if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_RL) {
            checkR2LHelper();
            r2LHelper.addDanmaku(danmaku);
        } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_LR) {
            checkL2RHelper();
            l2RHelper.addDanmaku(danmaku);
        } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_TB) {
            checkT2BHelper();
            t2BHelper.addDanmaku(danmaku);
        } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_BT) {
            checkB2THelper();
            b2THelper.addDanmaku(danmaku);
        } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SPECIAL) {
            checkSpecialHelper();
            specialHelper.addDanmaku(danmaku);
        }
    }

    @Override
    public void addDanmakus(@NonNull List<BaseDanmaku> danmakus) {
        List<BaseDanmaku> r2LList = new ArrayList<>();
        List<BaseDanmaku> l2RList = new ArrayList<>();
        List<BaseDanmaku> t2BList = new ArrayList<>();
        List<BaseDanmaku> b2TList = new ArrayList<>();
        List<BaseDanmaku> specialList = new ArrayList<>();
        for (BaseDanmaku danmaku : danmakus) {
            if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_RL) {
                r2LList.add(danmaku);
            } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_LR) {
                l2RList.add(danmaku);
            } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_TB) {
                t2BList.add(danmaku);
            } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SCROLL_BT) {
                b2TList.add(danmaku);
            } else if (danmaku.getType() == BaseDanmaku.DanmakuType.TYPE_SPECIAL) {
                specialList.add(danmaku);
            }
        }
        if (!r2LList.isEmpty()) {
            checkR2LHelper();
            r2LHelper.addDanmakus(r2LList);
        }

        if (!l2RList.isEmpty()) {
            checkL2RHelper();
            l2RHelper.addDanmakus(l2RList);
        }

        if (!t2BList.isEmpty()) {
            checkT2BHelper();
            t2BHelper.addDanmakus(t2BList);
        }

        if (!b2TList.isEmpty()) {
            checkB2THelper();
            b2THelper.addDanmakus(b2TList);
        }

        if (!specialList.isEmpty()) {
            checkSpecialHelper();
            specialHelper.addDanmakus(specialList);
        }
    }

    @Override
    public DrawHelper setOffScreenLimit(int offScreenLimit) {
        if (offScreenLimit > 0) {
            this.offScreenLimit = offScreenLimit;
            if (r2LHelper != null) {
                r2LHelper.setOffScreenLimit(offScreenLimit);
            }
            if (l2RHelper != null) {
                l2RHelper.setOffScreenLimit(offScreenLimit);
            }
            if (t2BHelper != null) {
                t2BHelper.setOffScreenLimit(offScreenLimit);
            }
            if (b2THelper != null) {
                b2THelper.setOffScreenLimit(offScreenLimit);
            }
            if (specialHelper != null) {
                specialHelper.setOffScreenLimit(offScreenLimit);
            }
        }
        return this;
    }

    @Override
    public DrawHelper setTrajectoryMargin(float mTrajectoryMargin) {
        this.mTrajectoryMargin = mTrajectoryMargin;
        if (r2LHelper != null) {
            r2LHelper.setTrajectoryMargin(mTrajectoryMargin);
        }
        if (l2RHelper != null) {
            l2RHelper.setTrajectoryMargin(mTrajectoryMargin);
        }
        if (t2BHelper != null) {
            t2BHelper.setTrajectoryMargin(mTrajectoryMargin);
        }
        if (b2THelper != null) {
            b2THelper.setTrajectoryMargin(mTrajectoryMargin);
        }
        return this;
    }

    @Override
    public DrawHelper setMaxTrajectoryCount(int maxTrajectoryCount) {
        this.maxTrajectoryCount = maxTrajectoryCount;
        if (r2LHelper != null) {
            r2LHelper.setMaxTrajectoryCount(maxTrajectoryCount);
        }
        if (l2RHelper != null) {
            l2RHelper.setMaxTrajectoryCount(maxTrajectoryCount);
        }
        if (t2BHelper != null) {
            t2BHelper.setMaxTrajectoryCount(maxTrajectoryCount);
        }
        if (b2THelper != null) {
            b2THelper.setMaxTrajectoryCount(maxTrajectoryCount);
        }
        return this;
    }

    @Override
    public DrawHelper setInterval(long interval) {
        this.interval = interval;
        if (specialHelper != null) {
            specialHelper.setInterval(interval);
        }
        return this;
    }

    @Override
    public DrawHelper setCountLimit(int countLimit) {
        this.countLimit = countLimit;
        if (specialHelper != null) {
            specialHelper.setCountLimit(countLimit);
        }
        return this;
    }


    @Override
    public void clear() {
        if (r2LHelper != null) {
            r2LHelper.clear();
        }
        if (l2RHelper != null) {
            l2RHelper.clear();
        }
        if (t2BHelper != null) {
            t2BHelper.clear();
        }
        if (b2THelper != null) {
            b2THelper.clear();
        }
        if (specialHelper != null) {
            specialHelper.clear();
        }
    }

    @Override
    public void rePlay() {
        if (r2LHelper != null) {
            r2LHelper.rePlay();
        }
        if (l2RHelper != null) {
            l2RHelper.rePlay();
        }
        if (t2BHelper != null) {
            t2BHelper.rePlay();
        }
        if (b2THelper != null) {
            b2THelper.rePlay();
        }
        if (specialHelper != null) {
            specialHelper.rePlay();
        }
    }

    @Override
    public int getState() {
        int state = 0;
        int count = 0;
        // 排除被清空的 只判断已经显示完的+1  正在显示的+2
        if (r2LHelper != null) {
            int tempState = r2LHelper.getState();
            if (tempState != DrawState.STATE_EMPTY) {
                count++;
                state += tempState;
            }
        }
        if (l2RHelper != null) {
            int tempState = l2RHelper.getState();
            if (tempState != DrawState.STATE_EMPTY) {
                count++;
                state += tempState;
            }
        }
        if (t2BHelper != null) {
            int tempState = t2BHelper.getState();
            if (tempState != DrawState.STATE_EMPTY) {
                count++;
                state += tempState;
            }
        }
        if (b2THelper != null) {
            int tempState = b2THelper.getState();
            if (tempState != DrawState.STATE_EMPTY) {
                count++;
                state += tempState;
            }
        }
        if (specialHelper != null) {
            int tempState = specialHelper.getState();
            if (tempState != DrawState.STATE_EMPTY) {
                count++;
                state += tempState;
            }
        }
        if (state == 0) {
            return DrawState.STATE_EMPTY;
        } else if (state == count) {
            return DrawState.STATE_GONE;
        } else {
            return DrawState.STATE_SHOWING;
        }
    }


    private void checkR2LHelper() {
        if (r2LHelper == null) {
            r2LHelper = new R2LHelper();
        }
        r2LHelper.setDen(den)
                .setBaseConfig(baseConfig)
                .setMaxTrajectoryCount(maxTrajectoryCount)
                .setOffScreenLimit(offScreenLimit)
                .setTrajectoryMargin(mTrajectoryMargin);
    }

    private void checkL2RHelper() {
        if (l2RHelper == null) {
            l2RHelper = new L2RHelper();
        }
        l2RHelper.setDen(den)
                .setBaseConfig(baseConfig)
                .setMaxTrajectoryCount(maxTrajectoryCount)
                .setOffScreenLimit(offScreenLimit)
                .setTrajectoryMargin(mTrajectoryMargin);
    }

    private void checkT2BHelper() {
        if (t2BHelper == null) {
            t2BHelper = new T2BHelper();
        }
        t2BHelper.setDen(den)
                .setBaseConfig(baseConfig)
                .setMaxTrajectoryCount(maxTrajectoryCount)
                .setOffScreenLimit(offScreenLimit)
                .setTrajectoryMargin(mTrajectoryMargin);
    }

    private void checkB2THelper() {
        if (b2THelper == null) {
            b2THelper = new B2THelper();
        }
        b2THelper.setDen(den)
                .setBaseConfig(baseConfig)
                .setMaxTrajectoryCount(maxTrajectoryCount)
                .setOffScreenLimit(offScreenLimit)
                .setTrajectoryMargin(mTrajectoryMargin);
    }

    private void checkSpecialHelper() {
        if (specialHelper == null) {
            specialHelper = new BaseSpecialHelper();
        }
        specialHelper.setDen(den)
                .setBaseConfig(baseConfig)
                .setOffScreenLimit(offScreenLimit)
                .setCountLimit(countLimit)
                .setInterval(interval);
    }
}
