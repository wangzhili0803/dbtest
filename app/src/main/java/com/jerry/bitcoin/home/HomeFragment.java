package com.jerry.bitcoin.home;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jerry.baselib.common.base.BaseFragment;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.bitcoin.R;
import com.jerry.bitcoin.beans.CoinConstant;

/**
 * @author Jerry
 * @createDate 2019-06-24
 * @description 主页
 */
public class HomeFragment extends BaseFragment implements OnCheckedChangeListener {

    @Override
    protected int getContentViewResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(final View view) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.app_name);
        CheckBox cbXrp = view.findViewById(R.id.cb_xrp);
        cbXrp.setOnCheckedChangeListener(this);
        CheckBox cbBch = view.findViewById(R.id.cb_bch);
        cbBch.setOnCheckedChangeListener(this);
        if (PreferenceHelp.getBoolean("LISTEN_" + CoinConstant.XRP)) {
            cbXrp.setChecked(true);
            if (!CoinConstant.LISTEN_COINS.contains(CoinConstant.XRP)) {
                CoinConstant.LISTEN_COINS.add(CoinConstant.XRP);
            }
        } else {
            cbXrp.setChecked(false);
            CoinConstant.LISTEN_COINS.remove(CoinConstant.XRP);
        }
        if (PreferenceHelp.getBoolean("LISTEN_" + CoinConstant.BCH)) {
            cbBch.setChecked(true);
            if (!CoinConstant.LISTEN_COINS.contains(CoinConstant.BCH)) {
                CoinConstant.LISTEN_COINS.add(CoinConstant.BCH);
            }
        } else {
            cbBch.setChecked(false);
            CoinConstant.LISTEN_COINS.remove(CoinConstant.BCH);
        }
    }

    @Override
    public void onClick(final View v) {

    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_xrp:
                if (isChecked && !CoinConstant.LISTEN_COINS.contains(CoinConstant.XRP)) {
                    CoinConstant.LISTEN_COINS.add(CoinConstant.XRP);
                } else if (!isChecked) {
                    CoinConstant.LISTEN_COINS.remove(CoinConstant.XRP);
                }
                PreferenceHelp.putBoolean("LISTEN_" + CoinConstant.XRP, isChecked);
                break;
            case R.id.cb_bch:
                if (isChecked && !CoinConstant.LISTEN_COINS.contains(CoinConstant.BCH)) {
                    CoinConstant.LISTEN_COINS.add(CoinConstant.BCH);
                } else if (!isChecked) {
                    CoinConstant.LISTEN_COINS.remove(CoinConstant.BCH);
                }
                PreferenceHelp.putBoolean("LISTEN_" + CoinConstant.BCH, isChecked);
                break;
            default:
                break;
        }
    }
}
