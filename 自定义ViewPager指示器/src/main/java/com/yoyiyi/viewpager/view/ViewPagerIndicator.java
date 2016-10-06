package com.yoyiyi.viewpager.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yoyiyi.viewpager.R;

import java.util.List;

/**
 * Created by yoyiyi on 2016/10/6.
 */
public class ViewPagerIndicator extends LinearLayout {
    private Paint mPaint;
    private Path mPath;
    private int mTriangleWidth;//三角形宽度
    private int mTriangleHeight;//三角形高度
    //设置三角形宽度的比例
    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6f;
    //设置三角形最大宽度
    private final int TRIANGLE_MAX_WIDTH = (int) (getScreeWidth() / 4 * RADIO_TRIANGLE_WIDTH);
    private int mIniTranslationX;//三角形初始位置
    private int mTranslationX;//三角形移动位置
    private int mTabVisibleCount;//可见的指示器
    private static final int COUNT_DEFAULT_COCUNT = 4;//默认子控件个数
    private List<String> mTitle;//子控件标题集合
    private static final int COLOR_TEXT_NORMAL = 0x66FFFFFF;//正常文字
    private static final int COLOR_TEXT_HIGHLIGHT = 0x99FFFFFF;//设置文字高亮


    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ViewPagerIndicator);
        mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_visible_tab_count,
                COUNT_DEFAULT_COCUNT);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = COUNT_DEFAULT_COCUNT;
        }
        a.recycle();
    }


    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));


    }

    //分发给子组件绘制 View
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mIniTranslationX + mTranslationX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置三角形宽度
        mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE_WIDTH);
        //取约定最小宽度
        mTriangleWidth = Math.min(mTriangleWidth, TRIANGLE_MAX_WIDTH);
        //设置三角形高度
        mTriangleHeight = mTriangleWidth / 2;
        //设置初始位置
        mIniTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;
        initTriangle();
    }

    //初始化三角形
    private void initTriangle() {
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();//闭合

    }

    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams params =
                    (LayoutParams) view.getLayoutParams();
            params.weight = 0;
            params.width = getScreeWidth() / mTabVisibleCount;
            view.setLayoutParams(params);

        }
        setItemChickEvent();

    }

    //三角形跟随指示器移动
    public void scroll(int position, float offset) {
        int tabWidth = getWidth() / mTabVisibleCount;
        mTranslationX = (int) (tabWidth * (offset + position));

        //容器移动，当tab处于移动到最后一个时候
        if (position >= mTabVisibleCount - 2 &&
                offset > 0 &&
                getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth +
                        (int) (tabWidth * offset), 0);
            } else {
                this.scrollTo(position * tabWidth + (int) (tabWidth * offset), 0);
            }
        }

        invalidate();
    }

    //获取屏幕宽度
    public int getScreeWidth() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreeWidth = dm.widthPixels;
        return mScreeWidth;
    }

    //设置Tab指示器内容
    public void setTabItemTitle(List<String> title) {
        if (title != null && title.size() > 0) {
            this.removeAllViews();
            this.mTitle = title;
            for (String myTitle : mTitle) {
                this.addView(addTextView(myTitle));
            }
            setItemChickEvent();
        }

    }

    private View addTextView(String myTitle) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams param = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        param.width = getScreeWidth() / mTabVisibleCount;
        tv.setLayoutParams(param);
        tv.setText(myTitle);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        return tv;
    }

    //设置初始Tab个数
    public void setVisibleTabCount(int count) {
        this.mTabVisibleCount = count;

    }

    private ViewPager mViewPager;

    //设置关联ViewPager
    public void setViewPager(ViewPager vp, int pos) {
        this.mViewPager = vp;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position, positionOffset);
                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset,
                            positionOffsetPixels);
                }

            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
                highLightTextView(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }


            }
        });
        //设置成首页
        mViewPager.setCurrentItem(pos);
        highLightTextView(pos);
    }

    //提供回调接口
    public PagerOnChangeListener mListener;

    public interface PagerOnChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    public void setPagerOnChangeListener(PagerOnChangeListener listener) {
        this.mListener = listener;
    }

    //重置文本颜色
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }

        }

    }

    //高亮文本
    private void highLightTextView(int pos) {
        resetTextViewColor();
        View view = getChildAt(pos);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
    }

    //设置子控件点击事件
    private void setItemChickEvent() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }
}
