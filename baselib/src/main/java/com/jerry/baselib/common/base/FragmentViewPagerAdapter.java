package com.jerry.baselib.common.base;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author Jerry
 * @createDate 2019-07-26
 * @description ViewPager适配器
 */
public class FragmentViewPagerAdapter<T extends BaseFragment> extends FragmentPagerAdapter {

    private List<T> fragmentList;
    private List<String> titles;
    private FragmentManager fm;

    public FragmentViewPagerAdapter(FragmentManager manager, List<String> titles, List<T> fragments) {
        super(manager);
        this.titles = titles;
        this.fragmentList = fragments;
        this.fm = manager;
    }

    public void updateData(final List<String> titles, final List<T> fragmentList) {
        clearFragmentCache();
        this.titles = titles;
        this.fragmentList = fragmentList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull final Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public T getItem(int position) {
        return fragmentList.get(position);
    }

    private void clearFragmentCache() {
        if (fm.isDestroyed()) {
            return;
        }
        FragmentTransaction ft = fm.beginTransaction();
        List<Fragment> fragmentList = fm.getFragments();
        for (Fragment t : fragmentList) {
            ft.remove(t);
        }
        ft.commitNowAllowingStateLoss();
    }
}
