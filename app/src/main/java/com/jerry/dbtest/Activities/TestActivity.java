package com.jerry.dbtest.Activities;

import java.util.List;

import com.jerry.dbtest.R;
import com.jerry.dbtest.fragments.TestFragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    //    @BindView(R.id.tab_test)
//    TabLayout mTabTest;
//    @BindView(R.id.vp_test)
//    ViewPager mVpTest;
    private String[] titles = {"F1", "F2", "F3"};
    private List<TestFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
//        fragments = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            TestFragment fragment = new TestFragment();
//            fragment.setTitle(titles[i]);
//            fragments.add(fragment);
//        }
//
//        mTabTest.setupWithViewPager(mVpTest);
//        mVpTest.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
//            @Override
//            public Fragment getItem(final int position) {
//                return fragments.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return titles.length;
//            }
//
//            @Override
//            public CharSequence getPageTitle(final int position) {
//                return titles[position];
//            }
//        });
    }

    public void btnClick(View view) {

    }
}
