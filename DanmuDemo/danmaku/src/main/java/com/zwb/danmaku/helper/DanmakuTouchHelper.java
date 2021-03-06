package com.zwb.danmaku.helper;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.zwb.danmaku.IDanmakuView;
import com.zwb.danmaku.model.BaseDanmaku;

/**
 * @ author : zhouweibin
 * @ time: 2020/3/17 16:25.
 * @ desc: 弹幕触摸帮助
 **/
public class DanmakuTouchHelper {
    private final GestureDetector mTouchDelegate;
    private IDanmakuView danmakuView;

    private final android.view.GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent event) {
            if (danmakuView != null) {
                IDanmakuView.OnDanmakuClickListener onDanmakuClickListener = danmakuView.getOnDanmakuClickListener();
                if (onDanmakuClickListener != null) {
                    // 事件被弹幕控件消费 不能透传下去
                    if (onDanmakuClickListener.onDownView(event.getX(), event.getY())) {
                        return true;
                    }
                    BaseDanmaku danmaku = danmakuView.getTouchMatchedDamaku(event.getX(), event.getY());
                    if (danmaku != null && onDanmakuClickListener.onDownDanmaku(event.getX(), event.getY())) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            IDanmakuView.OnDanmakuClickListener onDanmakuClickListener = danmakuView.getOnDanmakuClickListener();
            if (onDanmakuClickListener == null) {
                return false;
            }
            // 事件只被弹幕控件消费
            if (onDanmakuClickListener.onDownView(event.getX(), event.getY())) {
                return performViewClick(false);
            }
            // 事件不被弹幕控件中的item消费
            if (!onDanmakuClickListener.onDownDanmaku(event.getX(), event.getY())) {
                return false;
            }
            BaseDanmaku danmaku = danmakuView.getTouchMatchedDamaku(event.getX(), event.getY());
            boolean isEventConsumed = false;
            if (null != danmaku) {
                isEventConsumed = performDanmakuClick(danmaku, false);
            }
            return isEventConsumed;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            IDanmakuView.OnDanmakuClickListener onDanmakuClickListener = danmakuView.getOnDanmakuClickListener();
            if (onDanmakuClickListener == null) {
                return;
            }
            // 事件只被弹幕控件消费
            if (onDanmakuClickListener.onDownView(event.getX(), event.getY())) {
                performViewClick(true);
                return;
            }
            // 事件不被弹幕控件中的item消费
            if (!onDanmakuClickListener.onDownDanmaku(event.getX(), event.getY())) {
                return;
            }
            BaseDanmaku danmaku = danmakuView.getTouchMatchedDamaku(event.getX(), event.getY());
            if (null != danmaku) {
                performDanmakuClick(danmaku, true);
            }
        }
    };

    public DanmakuTouchHelper(IDanmakuView danmakuView) {
        this.danmakuView = danmakuView;
        this.mTouchDelegate = new GestureDetector(((View) danmakuView).getContext(), mOnGestureListener);
    }

    private boolean performDanmakuClick(BaseDanmaku danmakus, boolean isLongClick) {
        IDanmakuView.OnDanmakuClickListener onDanmakuClickListener = danmakuView.getOnDanmakuClickListener();
        if (onDanmakuClickListener != null) {
            if (isLongClick) {
                return onDanmakuClickListener.onDanmakuLongClick(danmakus);
            } else {
                return onDanmakuClickListener.onDanmakuClick(danmakus);
            }
        }
        return false;
    }

    private boolean performViewClick(boolean isLongClick) {
        IDanmakuView.OnDanmakuClickListener onDanmakuClickListener = danmakuView.getOnDanmakuClickListener();
        if (onDanmakuClickListener != null) {
            if (!isLongClick) {
                return onDanmakuClickListener.onViewClick(danmakuView);
            } else {
                return onDanmakuClickListener.onViewLongClick(danmakuView);
            }
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return mTouchDelegate.onTouchEvent(event);
    }
}
