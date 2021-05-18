package com.jerry.bitcoin.home;

import java.util.List;

import android.Manifest;
import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.asyctask.AppTask;
import com.jerry.baselib.common.asyctask.WhenTaskDone;
import com.jerry.baselib.common.base.BaseRecyclerAdapter;
import com.jerry.baselib.common.base.BaseRecyclerFragment;
import com.jerry.baselib.common.base.RecyclerViewHolder;
import com.jerry.baselib.common.bean.DyUser;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.DateUtils;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.UserManager;
import com.jerry.baselib.common.weidgt.NoticeDialog;
import com.jerry.baselib.greendao.DyUserDao.Properties;
import com.jerry.baselib.parsehelper.ExcelManager;
import com.jerry.bitcoin.R;
import com.jerry.bitcoin.helper.ContactHelper;

import androidx.core.app.ActivityCompat;

/**
 * @author Jerry
 * @createDate 2019-06-24
 * @description 主页
 */
public class HomeFragment extends BaseRecyclerFragment<DyUser> {

    @Override
    protected BaseRecyclerAdapter<DyUser> initAdapter() {
        return new BaseRecyclerAdapter<DyUser>(mActivity, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_dyuser;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final DyUser bean) {
                TextView tvName = holder.getView(R.id.tv_name);
                TextView tvDyid = holder.getView(R.id.tv_dyid);
                TextView tvPraise = holder.getView(R.id.tv_praise);
                TextView tvFollow = holder.getView(R.id.tv_follow);
                TextView tvFans = holder.getView(R.id.tv_fans);
                TextView tvPhone = holder.getView(R.id.tv_phone);
                TextView tvDesc = holder.getView(R.id.tv_desc);
                tvName.setText(bean.getName());
                String dyId = bean.getDyId();
                String phones = bean.getPhones();
                StringBuilder dyidSb = new StringBuilder();
                StringBuilder phoneSb = new StringBuilder();
                if (!UserManager.getInstance().isLogined() || !PreferenceHelp.getBoolean("follow_try")) {
                    if (dyId.length() > 5) {
                        dyidSb.append(dyId.substring(0, 5)).append("***");
                    }
                    if (phones != null && phones.length() > 3) {
                        phoneSb.append(phones.substring(0, 3)).append("***");
                    }
                } else {
                    dyidSb.append(dyId);
                    if (phones != null) {
                        phones = phones.replace(Key.COMMA, Key.SPACE);
                        phoneSb.append(phones);
                    }
                }
                tvDyid.setText(dyidSb.toString());
                tvPraise.setText(getString(R.string.praise_text, bean.getPraise()));
                tvFollow.setText(getString(R.string.follow_text, bean.getFollow()));
                tvFans.setText(getString(R.string.fans_text, bean.getFans()));
                if (TextUtils.isEmpty(phoneSb)) {
                    tvPhone.setVisibility(View.GONE);
                } else {
                    tvPhone.setVisibility(View.VISIBLE);
                    tvPhone.setText(getString(R.string.contact_text, phoneSb.toString()));
                }
                String desc = bean.getDesc();
                if (TextUtils.isEmpty(desc)) {
                    tvDesc.setVisibility(View.GONE);
                } else {
                    tvDesc.setVisibility(View.VISIBLE);
                    tvDesc.setText(desc);
                }
            }
        };
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(final View view) {
        mPtrRecyclerView = view.findViewById(R.id.ptrRecyclerView);
        view.findViewById(R.id.tv_excel).setOnClickListener(this);
        view.findViewById(R.id.tv_contact).setOnClickListener(this);
        view.findViewById(R.id.tv_clear).setOnClickListener(this);
        mPtrRecyclerView.setAdapter(mAdapter);
        mPtrRecyclerView.setOnRefreshListener(this::reload);
    }

    @Override
    protected void getData() {
        AppTask.withoutContext().assign(() -> ProManager.getInstance().queryAll(DyUser.class, null, Properties.UpdateTime))
            .whenDone((WhenTaskDone<List<DyUser>>) result -> {
                mData.clear();
                mData.addAll(result);
                mAdapter.notifyDataSetChanged();
            }).whenTaskEnd(this::onAfterRefresh).execute();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tv_excel:
                if (!UserManager.getInstance().isLogined()) {
                    return;
                }
                AppTask.withoutContext().assign(() -> {
                    String path =
                        FileUtil.getAppExternalPath() + "ex_" + DateUtils.getDateTimeByLong(System.currentTimeMillis()) + ".xls";
                    return ExcelManager.createExcel(mData, path);
                }).whenDone((WhenTaskDone<Boolean>) result -> {
                    if (result) {
                        toast("导出成功");
                    } else {
                        toast("导出失败");
                    }
                }).whenTaskEnd(this::onAfterRefresh).execute();
                break;
            case R.id.tv_contact:
                if (!UserManager.getInstance().isLogined()) {
                    return;
                }
                PackageManager pkgManager = mActivity.getPackageManager();
                // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
                boolean contactsWritePermission =
                    pkgManager.checkPermission(permission.WRITE_CONTACTS, mActivity.getPackageName()) == PackageManager.PERMISSION_GRANTED;
                if (contactsWritePermission || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    export2Contacts();
                } else {
                    ActivityCompat
                        .requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_CONTACTS}, MainActivity.REQUEST_PERMISSION_CONTACT);
                }
                break;
            case R.id.tv_clear:
                if (CollectionUtils.isEmpty(mData)) {
                    toast("暂无采集数据");
                    return;
                }
                NoticeDialog noticeDialog = new NoticeDialog(mActivity);
                noticeDialog.setPositiveText(R.string.confirm);
                noticeDialog.setTitleText("确定清除所有用户数据吗");
                noticeDialog.setPositiveListener(view -> {
                    AppTask.withoutContext().assign(() -> ProManager.getInstance().deleteAll(DyUser.class))
                        .whenDone((WhenTaskDone<Boolean>) result -> {
                            if (result) {
                                mData.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                        }).whenTaskEnd(() -> {
                        noticeDialog.dismiss();
                        onAfterRefresh();
                    }).execute();
                });
                noticeDialog.show();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    public void export2Contacts() {
        AppTask.withoutContext().assign(() -> {
            try {
                return ContactHelper.BatchAddContact(mActivity, mData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }).whenDone((WhenTaskDone<Boolean>) result -> {
            if (result) {
                toast("导出成功");
            } else {
                toast("导出失败");
            }
        }).whenTaskEnd(this::onAfterRefresh).execute();
    }

    @Override
    public void onItemClick(final View itemView, final int position) {

    }
}
