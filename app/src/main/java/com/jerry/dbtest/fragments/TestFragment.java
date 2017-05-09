package com.jerry.dbtest.fragments;

import com.jerry.dbtest.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wzl-pc on 2017/4/18.
 */

public class TestFragment extends Fragment {
    private String title;
    @BindView(R.id.tv_test)
    TextView mTvTest;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fm_test, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTest.setText(title);
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
