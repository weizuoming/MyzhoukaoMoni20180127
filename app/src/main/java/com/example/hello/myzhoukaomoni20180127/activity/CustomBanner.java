package com.example.hello.myzhoukaomoni20180127.activity;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.hello.myzhoukaomoni20180127.R;

import java.util.ArrayList;
import java.util.List;

/**
 * on 2018/1/27.
 *
 * 自定义属性
 * values下面创建一个attrs.xml文件...attribute
 * 添加标签declare_styable自己声明的样式,name自动提示为类名
 * 自定义属性attr,,,name为属性名称
 * format为数据类型,,,string boolean
 * xml声明的时候引用属性
 命名空间:xmlns:dash="http://schemas.android.com/apk/res-auto"
 * 获取设置的属性
 text = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "text");//第一个参数为命名空间,第二个参数为属性名
 checked = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res-auto", "checked", false);
 */
public class CustomBanner extends RelativeLayout {

    private ViewPager custom_banner_view_pager;
    private LinearLayout linear_doc;
    private List<String> list;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {

                int currentItem = custom_banner_view_pager.getCurrentItem();
                //接收到之后 显示下一张
                custom_banner_view_pager.setCurrentItem(currentItem +1 );

                //再次发送
                handler.sendEmptyMessageDelayed(0,time *1000);
            }
        }
    };

    private int time = 2;//初始化自动轮播的时间
    private List<ImageView> imageViews;
    private OnBannerListner onBannerListner;

    //使用new在代码中创建的时候调用
    public CustomBanner(Context context) {
        super(context);

        init();
    }

    //在xml文件中使用的时候 反射调用该构造方法
    public CustomBanner(Context context, AttributeSet attrs) {
        super(context, attrs);

        time = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto", "time", 1);
        Log.i("-----","获取的时间值:"+time);

        init();
    }
    ////在xml文件中使用的时候 反射调用该构造方法
    public CustomBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    /**
     * 初始化的方法
     */
    private void init() {
        //把拿到的视图挂载到当前自定义控件上
        View view = View.inflate(getContext(), R.layout.custom_banner_layout,this);
        custom_banner_view_pager = view.findViewById(R.id.custom_banner_view_pager);
        linear_doc = view.findViewById(R.id.linear_doc);

    }

    /**
     * 设置图片的路径
     * @param list
     */
    public void setImageUrls(final List<String> list) {
        this.list = list;

        if (list == null) {
            return;
        }

        //设置适配器
        CustomBannerAdapter customBannerAdapter = new CustomBannerAdapter();
        custom_banner_view_pager.setAdapter(customBannerAdapter);

        //初始化小圆点
        initDoc();


        //无线自动轮播...
        //刚开始展示中间某个位置的图片
        custom_banner_view_pager.setCurrentItem(list.size()*100000,false);

        //发送延时消息
        handler.sendEmptyMessageDelayed(0,time *1000);

        //监听
        custom_banner_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当选中展示改页面的时候 小圆点是红色的

                for (int i =0;i<list.size();i++) {

                    if (i == position %list.size() ) {
                        imageViews.get(i).setImageResource(R.drawable.checked_shape);
                    }else {
                        imageViews.get(i).setImageResource(R.drawable.un_checked_shape);
                    }

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化小圆点
     */
    private void initDoc() {

        //先创建一个集合 去记录小圆点
        imageViews = new ArrayList<>();
        //清空
        imageViews.clear();
        linear_doc.removeAllViews();

        //添加
        for (int i = 0;i<list.size();i++) {
            ImageView imageDoc = new ImageView(getContext());

            if (i == 0) {
                imageDoc.setImageResource(R.drawable.checked_shape);
            }else {
                imageDoc.setImageResource(R.drawable.un_checked_shape);
            }

            //添加到集合
            imageViews.add(imageDoc);
            //添加到线性布局
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,0,5,0);

            linear_doc.addView(imageDoc,layoutParams);

        }

    }

    public void setOnBannerListner(OnBannerListner onBannerListner) {
        this.onBannerListner = onBannerListner;
    }

    /**
     * 对外提供设置时间的方法
     * @param time
     */
    public void setTimeSeconds(int time) {
        this.time = time;
    }

    private class CustomBannerAdapter extends PagerAdapter {

        @Override
        public int getCount() {

            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            //创建imageView
            ImageView imageView = new ImageView(getContext());

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            //触摸事件的处理
            imageView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //停止handler的一切动作
                            handler.removeCallbacksAndMessages(null);

                            break;
                        case MotionEvent.ACTION_MOVE:
                            handler.removeCallbacksAndMessages(null);

                            break;
                        case MotionEvent.ACTION_CANCEL:

                            handler.sendEmptyMessageDelayed(0,time *1000);
                            break;
                        case MotionEvent.ACTION_UP:

                            handler.sendEmptyMessageDelayed(0,time *1000);
                            break;
                    }

                    return false;
                }
            });

            //点击事件
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //postion...做%的操作
                    onBannerListner.onBannerClick(position % list.size());
                }
            });


            //加载图片显示
            Glide.with(getContext()).load(list.get(position % list.size())).into(imageView);
            //添加到容器
            container.addView(imageView);

            //返回imageView
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public interface OnBannerListner{
        void onBannerClick(int position);
    }
}
