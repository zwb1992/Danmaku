package com.zwb.danmudemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zwb.danmaku.DanmakuFactory;
import com.zwb.danmaku.helper.SimpleDanmakuClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import master.flame.danmaku.controller.IDanmakuView;
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
    private Button btAdd4, btStart4, btStop4, btClear4, btData4;

    private com.zwb.danmaku.DanmakuView danmu;
    private com.zwb.danmaku.DanmakuView danmu3;
    private com.zwb.danmaku.DanmakuView danmu4;

    private List<Integer> danmakuStyle = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        danmakuStyle.add(R.drawable.ic_danmu_style_bg_vip);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_2);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_3);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_4);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_5);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_6);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_7);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_8);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_9);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_10);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_11);
        danmakuStyle.add(R.drawable.ic_danmu_style_bubble_12);
        mDanmakuView = findViewById(R.id.danmu);
        danmu = findViewById(R.id.danmu2);
        danmu3 = findViewById(R.id.danmu3);
        danmu4 = findViewById(R.id.danmu4);
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

        btAdd4 = findViewById(R.id.btAdd4);
        btStart4 = findViewById(R.id.btStart4);
        btStop4 = findViewById(R.id.btStop4);
        btClear4 = findViewById(R.id.btClear4);
        btData4 = findViewById(R.id.btData4);

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
                com.zwb.danmaku.model.BaseDanmaku info = DanmakuFactory.create(com.zwb.danmaku.model.BaseDanmaku.DanmakuType.TYPE_SCROLL_LR);
                info.setText("霸气霸气")
                        .setOffset((10 + new Random().nextInt(30)) * getResources().getDisplayMetrics().density)
                        .setTextSize(getResources().getDisplayMetrics().density * 20)
                        .setTextColor(getResources().getColor(R.color.white))
                        .setSpeed(3)
                        .setBackgroundId(R.mipmap.ic_launcher)
                        .setShadowColor(getResources().getColor(R.color.gray))
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
                com.zwb.danmaku.model.BaseDanmaku info = DanmakuFactory.create(com.zwb.danmaku.model.BaseDanmaku.DanmakuType.TYPE_SCROLL_RL);
                info.setText("霸气霸气")
                        .setOffset((10 + new Random().nextInt(30)) * getResources().getDisplayMetrics().density)
                        .setTextSize(getResources().getDisplayMetrics().density * 26)
                        .setTextColor(Color.RED)
                        .setSpeed(1)
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

        btAdd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.zwb.danmaku.model.BaseDanmaku info = DanmakuFactory.create(com.zwb.danmaku.model.BaseDanmaku.DanmakuType.TYPE_SPECIAL);
                info.setText("霸气霸气")
                        .setTextSize(getResources().getDisplayMetrics().density * 16)
                        .setTextColor(Color.RED)
                        .setShadowColor(Color.YELLOW)
                        .setShadowWidth(3 * getResources().getDisplayMetrics().density)
                        .setScrollX(new Random().nextInt((int) (getResources().getDisplayMetrics().widthPixels - getResources().getDisplayMetrics().density * 100)))
                        .setScrollY(new Random().nextInt((int) (getResources().getDisplayMetrics().density * 250)))
                        .setDisappearDuration(600)
                        .setDuration(4000);// 显示4秒钟
                danmu4.addDanmuku(info);
            }
        });
        btData4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDanmu4();
            }
        });
        btStart4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmu4.start();
            }
        });
        btStop4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmu4.stop();
            }
        });
        btClear4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmu4.clear();
            }
        });

        danmu.setOnDanmakuClickListener(new SimpleDanmakuClickListener() {

            @Override
            public boolean onDanmakuClick(com.zwb.danmaku.model.BaseDanmaku danmakus) {
                Toast.makeText(MainActivity.this, "点击的弹幕：" + danmakus.getText(), Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onDanmakuLongClick(com.zwb.danmaku.model.BaseDanmaku danmakus) {
                Toast.makeText(MainActivity.this, "长按的弹幕：" + danmakus.getText(), Toast.LENGTH_SHORT).show();
                return true;
            }

        });

        danmu3.setOnDanmakuClickListener(new SimpleDanmakuClickListener() {

            @Override
            public boolean onDanmakuClick(com.zwb.danmaku.model.BaseDanmaku danmakus) {
                Toast.makeText(MainActivity.this, "点击的弹幕：" + danmakus.getText(), Toast.LENGTH_SHORT).show();
                if (danmu3.isPlaying()) {
                    danmu3.pause();
                } else {
                    danmu3.resume();
                }
                return true;
            }

            @Override
            public boolean onDanmakuLongClick(com.zwb.danmaku.model.BaseDanmaku danmakus) {
                Toast.makeText(MainActivity.this, "长按的弹幕：" + danmakus.getText(), Toast.LENGTH_SHORT).show();
                if (danmu3.isPlaying()) {
                    danmu3.pause();
                } else {
                    danmu3.resume();
                }
                return true;
            }
        });

        danmu4.setOnDanmakuClickListener(new SimpleDanmakuClickListener() {

            @Override
            public boolean onDownDanmaku(float x, float y) {
                return true;
            }

            @Override
            public boolean onDownView(float x, float y) {
                return false;
            }

            @Override
            public boolean onDanmakuClick(com.zwb.danmaku.model.BaseDanmaku danmakus) {
                Toast.makeText(MainActivity.this, "点击的弹幕：" + danmakus.getText(), Toast.LENGTH_SHORT).show();
                if (danmu4.isPlaying()) {
                    danmu4.pause();
                } else {
                    danmu4.resume();
                }
                return false;
            }

            @Override
            public boolean onDanmakuLongClick(com.zwb.danmaku.model.BaseDanmaku danmakus) {
                Toast.makeText(MainActivity.this, "长按的弹幕：" + danmakus.getText(), Toast.LENGTH_SHORT).show();
                if (danmu4.isPlaying()) {
                    danmu4.pause();
                } else {
                    danmu4.resume();
                }
                return false;
            }

            @Override
            public boolean onViewClick(com.zwb.danmaku.IDanmakuView view) {
                Toast.makeText(MainActivity.this, "弹幕的点击事件", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onViewLongClick(com.zwb.danmaku.IDanmakuView view) {
                Toast.makeText(MainActivity.this, "弹幕的长按事件", Toast.LENGTH_SHORT).show();
                return super.onViewLongClick(view);
            }
        });
        initDanmu();

        findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "底部控件", Toast.LENGTH_SHORT).show();
            }
        });
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
            mDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {


                @Override
                public boolean onDanmakuClick(IDanmakus danmakus) {
                    Toast.makeText(MainActivity.this, danmakus.first().text, Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onDanmakuLongClick(IDanmakus danmakus) {
                    Toast.makeText(MainActivity.this, danmakus.first().text, Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onViewClick(IDanmakuView view) {
                    return false;
                }
            });
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
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        //弹幕显示的文字
        danmaku.text = "这是一条/n弹幕" + System.nanoTime();
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
            com.zwb.danmaku.model.BaseDanmaku info = DanmakuFactory.create(com.zwb.danmaku.model.BaseDanmaku.DanmakuType.TYPE_SCROLL_LR);
            info.setText("太好看了,喜欢")
                    .setOffset((10 + new Random().nextInt(30)) * getResources().getDisplayMetrics().density)
                    .setTextSize(getResources().getDisplayMetrics().density * 14)
                    .setTextColor(getResources().getColor(R.color.white))
                    .setSpeed(2.6f)
                    .setBackgroundId(R.mipmap.ic_launcher)
                    .setBackgroundId(danmakuStyle.get(new Random().nextInt(12)))
                    .setShadowColor(getResources().getColor(R.color.gray))
                    .setShadowWidth(3 * getResources().getDisplayMetrics().density)
                    .setPaddingBottom((3 * getResources().getDisplayMetrics().density))
                    .setPaddingTop((3 * getResources().getDisplayMetrics().density))
                    .setPaddingLeft((6 * getResources().getDisplayMetrics().density))
                    .setPaddingRight((6 * getResources().getDisplayMetrics().density))
            ;
            list.add(info);
        }
        danmu.setDanmukus(list);
    }

    private void initDanmu3() {
        List<com.zwb.danmaku.model.BaseDanmaku> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            com.zwb.danmaku.model.BaseDanmaku info = DanmakuFactory.create(com.zwb.danmaku.model.BaseDanmaku.DanmakuType.TYPE_SCROLL_BT);
            info.setText("太好看了,喜欢")
                    .setOffset((10 + new Random().nextInt(30)) * getResources().getDisplayMetrics().density)
                    .setTextSize(getResources().getDisplayMetrics().density * 16)
//                    .setTextColor(Color.RED)
                    .setSpeed(2.6f)
                    .setBackgroundId(R.mipmap.ic_launcher)
                    .setBackgroundId(danmakuStyle.get(new Random().nextInt(12)))
                    .setShadowStyle(com.zwb.danmaku.model.BaseDanmaku.SHADOW_STYLE_LAYER)
//                    .setShadowColor(Color.YELLOW)
                    .setShadowWidth(3 * getResources().getDisplayMetrics().density)
                    .setPaddingBottom((3 * getResources().getDisplayMetrics().density))
                    .setPaddingTop((3 * getResources().getDisplayMetrics().density))
                    .setPaddingLeft((6 * getResources().getDisplayMetrics().density))
                    .setPaddingRight((6 * getResources().getDisplayMetrics().density));
            list.add(info);
        }
        danmu3.setDanmukus(list);
    }

    private void initDanmu4() {
        List<com.zwb.danmaku.model.BaseDanmaku> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            com.zwb.danmaku.model.BaseDanmaku info = DanmakuFactory.create(com.zwb.danmaku.model.BaseDanmaku.DanmakuType.TYPE_SPECIAL);
            info.setText("太好看了,喜欢")
                    .setTextSize(getResources().getDisplayMetrics().density * 16)
//                    .setTextColor(Color.RED)
//                    .setShadowColor(Color.YELLOW)
//                    .setShadowStyle(com.zwb.danmaku.model.BaseDanmaku.SHADOW_STYLE_STROKE)
                    .setShadowWidth(3 * getResources().getDisplayMetrics().density)
                    .setScrollX(new Random().nextInt((int) (getResources().getDisplayMetrics().widthPixels - getResources().getDisplayMetrics().density * 100)))
                    .setScrollY(new Random().nextInt((int) (getResources().getDisplayMetrics().density * 250)))
                    .setDisappearDuration(600)
                    .setShadowStyle(com.zwb.danmaku.model.BaseDanmaku.SHADOW_STYLE_LAYER)
                    .setDuration(4000)// 显示4秒钟
//                    .setBackgroundId(danmakuStyle.get(new Random().nextInt(12)))
                    .setBackgroundId(R.drawable.shape_corner_4_7f666666)
//                    .setBackgroundId(R.mipmap.ic_launcher)
                    .setPaddingBottom((3 * getResources().getDisplayMetrics().density))
                    .setPaddingTop((3 * getResources().getDisplayMetrics().density))
                    .setPaddingLeft((6 * getResources().getDisplayMetrics().density))
                    .setPaddingRight((6 * getResources().getDisplayMetrics().density));
            list.add(info);
        }
        danmu4.setDanmukus(list);
    }
}
