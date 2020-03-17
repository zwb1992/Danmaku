package com.zwb.danmaku.helper;

import com.zwb.danmaku.IDanmakuView;
import com.zwb.danmaku.model.BaseDanmaku;

/**
 * @ author : zhouweibin
 * @ time: 2020/3/17 17:24.
 * @ desc:
 **/
public class SimpleDanmakuClickListener implements IDanmakuView.OnDanmakuClickListener {
    @Override
    public boolean onDanmakuClick(BaseDanmaku danmaku) {
        return false;
    }

    @Override
    public boolean onDanmakuLongClick(BaseDanmaku danmaku) {
        return false;
    }

    @Override
    public boolean onViewClick(IDanmakuView view) {
        return false;
    }

    @Override
    public boolean onDown(float x, float y) {
        return false;
    }
}
