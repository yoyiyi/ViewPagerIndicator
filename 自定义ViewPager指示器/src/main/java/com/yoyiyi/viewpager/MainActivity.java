package com.yoyiyi.viewpager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.yoyiyi.viewpager.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.vpi_top)
    ViewPagerIndicator mVpiTop;
    @BindView(R.id.vp)
    ViewPager mVp;
    private List<String> mTitle =
            Arrays.asList("页面1", "页面2", "页面3", "页面4"
                    , "页面5", "页面6", "页面7", "页面8"
                    , "页面9");
    private ArrayList<VpSimpleFragment> mContents =
            new ArrayList<>();
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        mVp.setAdapter(mAdapter);
        mVpiTop.setViewPager(mVp, 0);
    }

    private void initData() {
        //初始化数据
        for (String title : mTitle) {
            VpSimpleFragment fragment = VpSimpleFragment.newInstance(title);
            mContents.add(fragment);

        }
        //初始化ViewPager的Adapter 适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public VpSimpleFragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        };
        mVpiTop.setVisibleTabCount(5);
        mVpiTop.setTabItemTitle(mTitle);
    }

}
