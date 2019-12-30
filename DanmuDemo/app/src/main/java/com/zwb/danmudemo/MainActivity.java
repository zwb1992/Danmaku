package com.zwb.danmudemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zwb.danmaku.DanmakuFactory;
import com.zwb.danmaku.model.R2LDanmaku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class MainActivity extends AppCompatActivity {
    private DanmakuView mDanmakuView;
    private DanmakuContext mContext;
    private Button btAdd, btAdd2, btStart2, btStop2, btClear2, btAdd3, btStart3, btStop3, btClear3, btData3;

    private com.zwb.danmaku.DanmakuView danmu;
    private com.zwb.danmaku.DanmakuView danmu3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDanmakuView = findViewById(R.id.danmu);
        danmu = findViewById(R.id.danmu2);
        danmu3 = findViewById(R.id.danmu3);
        btAdd = findViewById(R.id.btAdd);
        btAdd2 = findViewById(R.id.btAdd2);
        btStart2 = findViewById(R.id.btStart2);
        btStop2 = findViewById(R.id.btStop2);
        btClear2 = findViewById(R.id.btClear2);

        btAdd3 = findViewById(R.id.btAdd3);
        btStart3 = findViewById(R.id.btStart3);
        btStop3 = findViewById(R.id.btStop3);
        btClear3 = findViewById(R.id.btClear3);
        btData3 = findViewById(R.id.btData3);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDanmakuView.isPrepared()) {
                    addDanmaku(true);
                }
            }
        });
        btAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.zwb.danmaku.model.BaseDanmaku info = new R2LDanmaku();
                info.setText("霸气霸气")
                        .setOffset((10 + new Random().nextInt(30)) * getResources().getDisplayMetrics().density)
                        .setTextSize(getResources().getDisplayMetrics().density * 20)
                        .setTextColor(Color.BLUE)
                        .setSpeed(3 * getResources().getDisplayMetrics().density)
                        .setShadowColor(Color.YELLOW)
                        .setShadowWidth(3 * getResources().getDisplayMetrics().density)
                        .setPaddingBottom((int) (3 * getResources().getDisplayMetrics().density))
                        .setPaddingTop((int) (3 * getResources().getDisplayMetrics().density))
                        .setPaddingLeft((int) (6 * getResources().getDisplayMetrics().density))
                        .setPaddingRight((int) (6 * getResources().getDisplayMetrics().density));
                danmu.addDanmuku(info);
            }
        });
        btStart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDanmu2();
                danmu.start();
            }
        });
        btStop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmu.stop();
            }
        });
        btClear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmu.clear();
            }
        });

        btAdd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.zwb.danmaku.model.BaseDanmaku info = new R2LDanmaku();
                info.setText("霸气霸气")
                        .setOffset((10 + new Random().nextInt(30)) * getResources().getDisplayMetrics().density)
                        .setTextSize(getResources().getDisplayMetrics().density * 26)
                        .setTextColor(Color.RED)
                        .setSpeed(3 * getResources().getDisplayMetrics().density)
                        .setShadowColor(Color.YELLOW)
                        .setShadowWidth(3 * getResources().getDisplayMetrics().density)
                        .setPaddingBottom((int) (3 * getResources().getDisplayMetrics().density))
                        .setPaddingTop((int) (3 * getResources().getDisplayMetrics().density))
                        .setPaddingLeft((int) (6 * getResources().getDisplayMetrics().density))
                        .setPaddingRight((int) (6 * getResources().getDisplayMetrics().density));
                danmu3.addDanmuku(info);
            }
        });
        btData3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDanmu3();
            }
        });
        btStart3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmu3.start();
            }
        });
        btStop3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmu3.stop();
            }
        });
        btClear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmu3.clear();
            }
        });
        initDanmu();
    }

    private void initDanmu() {
        //设置最大显示行数
        HashMap<Integer, Integer> maxLInesPair = new HashMap();
        maxLInesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 2);
        //设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        //创建弹幕上下文
        mContext = DanmakuContext.create();
        //设置一些相关的配置
        mContext.setDuplicateMergingEnabled(false)
                //是否重复合并
                .setScrollSpeedFactor(1.2f)
                //设置文字的比例
                .setScaleTextSize(1.2f)
                //图文混排的时候使用！这里可以不用
//                .setCacheStuffer(new MyCacheStuffer(mActivity), mBackgroundCacheStuffer)
                //设置显示最大行数
                .setMaximumLines(maxLInesPair)
                //设置防，null代表可以重叠
                .preventOverlapping(overlappingEnablePair);
        //设置解析器
        BaseDanmakuParser defaultDanmakuParser = getDefaultDanmakuParser();

        if (mDanmakuView != null) {
            //相应的回掉
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                    //定时器更新的时候回掉
                }

                @Override
                public void drawingFinished() {
                    //弹幕绘制完成时回掉
                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                    //弹幕展示的时候回掉
                }

                @Override
                public void prepared() {
                    //弹幕准备好的时候回掉，这里启动弹幕
                    mDanmakuView.start();
                    Log.e("zwb", "prepared=======");
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
//                    addDanmaku(true);
                }
            });
            mDanmakuView.prepare(defaultDanmakuParser, mContext);
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    public static BaseDanmakuParser getDefaultDanmakuParser() {
        return new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    private void addDanmaku(boolean islive) {
        //创建一个弹幕对象，这里后面的属性是设置滚动方向的！
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_FIX_TOP);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        //弹幕显示的文字
        danmaku.text = "这是一条弹幕" + System.nanoTime();
        //设置相应的边距，这个设置的是四周的边距
        danmaku.padding = 5;
        // 可能会被各种过滤器过滤并隐藏显示，若果是本机发送的弹幕，建议设置成1；
        danmaku.priority = 1;
        //是否是直播弹幕
        danmaku.isLive = islive;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        //设置文字大小
        danmaku.textSize = 25f;
        //设置文字颜色
        danmaku.textColor = Color.RED;
        //设置阴影的颜色
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
        //设置背景颜色
        danmaku.borderColor = Color.GREEN;
        //添加这条弹幕，也就相当于发送
        mDanmakuView.addDanmaku(danmaku);
    }


    private void initDanmu2() {
        List<com.zwb.danmaku.model.BaseDanmaku> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            com.zwb.danmaku.model.BaseDanmaku info = DanmakuFactory.create(com.zwb.danmaku.model.BaseDanmaku.DanmakuType.TYPE_SCROLL_RL);
            info.setText("太好看了, 喜欢")
                    .setOffset((10 + new Random().nextInt(30)) * getResources().getDisplayMetrics().density)
                    .setTextSize(getResources().getDisplayMetrics().density * 20)
                    .setTextColor(Color.argb(255,new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)))
                    .setSpeed(2 * getResources().getDisplayMetrics().density)
                    .setShadowColor(Color.YELLOW)
                    .setShadowWidth(3 * getResources().getDisplayMetrics().density)
                    .setPaddingBottom((int) (3 * getResources().getDisplayMetrics().density))
                    .setPaddingTop((int) (3 * getResources().getDisplayMetrics().density))
                    .setPaddingLeft((int) (6 * getResources().getDisplayMetrics().density))
                    .setPaddingRight((int) (6 * getResources().getDisplayMetrics().density));
            list.add(info);
        }
        danmu.setDanmukus(list);
    }

    private void initDanmu3() {
        List<com.zwb.danmaku.model.BaseDanmaku> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            com.zwb.danmaku.model.BaseDanmaku info = DanmakuFactory.create(com.zwb.danmaku.model.BaseDanmaku.DanmakuType.TYPE_SCROLL_RL);
            info.setText("太好看了, 喜欢")
                    .setOffset((10 + new Random().nextInt(30)) * getResources().getDisplayMetrics().density)
                    .setTextSize(getResources().getDisplayMetrics().density * 20)
                    .setTextColor(Color.RED)
                    .setSpeed(2 * getResources().getDisplayMetrics().density)
                    .setShadowColor(Color.YELLOW)
                    .setShadowWidth(3 * getResources().getDisplayMetrics().density)
                    .setPaddingBottom((int) (3 * getResources().getDisplayMetrics().density))
                    .setPaddingTop((int) (3 * getResources().getDisplayMetrics().density))
                    .setPaddingLeft((int) (6 * getResources().getDisplayMetrics().density))
                    .setPaddingRight((int) (6 * getResources().getDisplayMetrics().density));
            list.add(info);
        }
        danmu3.setDanmukus(list);
    }
}
