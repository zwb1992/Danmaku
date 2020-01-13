package com.zwb.danmaku;

import com.zwb.danmaku.model.B2TDanmaku;
import com.zwb.danmaku.model.BaseDanmaku;
import com.zwb.danmaku.model.L2RDanmaku;
import com.zwb.danmaku.model.R2LDanmaku;
import com.zwb.danmaku.model.SpecialDanmaku;
import com.zwb.danmaku.model.T2BDanmaku;

/**
 * @ author : zhouweibin
 * @ time: 2019/12/29 18:22.
 * @ desc:
 **/
public class DanmakuFactory {

    public static BaseDanmaku create(BaseDanmaku.DanmakuType type) {
        switch (type) {
            case TYPE_SCROLL_LR:
                return new L2RDanmaku();
            case TYPE_SCROLL_TB:
                return new T2BDanmaku();
            case TYPE_SCROLL_BT:
                return new B2TDanmaku();
            case TYPE_SPECIAL:
                return new SpecialDanmaku();
            case TYPE_SCROLL_RL:
            default:
                return new R2LDanmaku();
        }
    }
}
